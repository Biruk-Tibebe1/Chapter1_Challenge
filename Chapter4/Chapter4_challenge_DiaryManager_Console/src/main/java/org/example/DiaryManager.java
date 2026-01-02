package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * DiaryManager - manages per-entry text files plus a small JSON index (title + tags).
 * Provides write/edit/delete, index persistence, JSON export/import, ZIP backup and simple state serialization.
 */
public class DiaryManager {
    private final Path baseDir;
    private final Path indexFile;
    private final Path stateFile;
    private final DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
    private final DateTimeFormatter zipFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private final ObjectMapper mapper = new ObjectMapper();

    // in-memory index: filename -> metadata
    private final Map<String, EntryMeta> index = new LinkedHashMap<>();

    public DiaryManager() {
        this("entries");
    }

    public DiaryManager(String baseDirName) {
        this.baseDir = Paths.get(baseDirName);
        this.indexFile = baseDir.resolve("index.json");
        this.stateFile = baseDir.resolve("state.ser");
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            System.out.println("Failed to create entries directory: " + e.getMessage());
        }
        loadIndex();
    }

    // metadata holder
    public static class EntryMeta {
        public String title;
        public List<String> tags = new ArrayList<>();
        public EntryMeta() {}
        public EntryMeta(String title, List<String> tags) {
            this.title = title;
            this.tags = tags == null ? new ArrayList<>() : new ArrayList<>(tags);
        }
    }

    // write new entry with metadata
    public Path writeEntry(String title, List<String> tags, String text) {
        String filename = "diary_" + LocalDateTime.now().format(fileFormatter) + ".txt";
        Path out = baseDir.resolve(filename);
        try (BufferedWriter bw = Files.newBufferedWriter(out, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            bw.write(text != null ? text : "");
            bw.flush();
            index.put(filename, new EntryMeta(title, tags));
            saveIndex();
            return out;
        } catch (IOException e) {
            System.out.println("Failed to write entry: " + e.getMessage());
            return null;
        }
    }

    // list entry files (newest first)
    public List<Path> listEntries() {
        List<Path> list = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(baseDir, "diary_*.txt")) {
            for (Path p : ds) {
                if (Files.isRegularFile(p)) list.add(p);
            }
        } catch (IOException e) {
            System.out.println("Failed to list entries: " + e.getMessage());
        }
        list.sort(Comparator.comparing(Path::getFileName).reversed());
        return list;
    }

    // read full content
    public String readEntry(Path entryFile) {
        if (entryFile == null || !Files.exists(entryFile)) return null;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = Files.newBufferedReader(entryFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
            return sb.toString();
        } catch (IOException e) {
            System.out.println("Failed to read entry: " + e.getMessage());
            return null;
        }
    }

    // edit existing entry content (overwrite) and optionally update meta
    public boolean editEntry(String filename, String newTitle, List<String> newTags, String newText) {
        Path file = baseDir.resolve(filename);
        if (!Files.exists(file)) return false;
        try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            bw.write(newText != null ? newText : "");
            bw.flush();
            EntryMeta meta = index.getOrDefault(filename, new EntryMeta());
            if (newTitle != null) meta.title = newTitle;
            if (newTags != null) meta.tags = new ArrayList<>(newTags);
            index.put(filename, meta);
            saveIndex();
            return true;
        } catch (IOException e) {
            System.out.println("Failed to edit entry: " + e.getMessage());
            return false;
        }
    }

    // delete entry file and remove meta
    public boolean deleteEntry(String filename) {
        Path file = baseDir.resolve(filename);
        try {
            boolean deleted = Files.deleteIfExists(file);
            index.remove(filename);
            saveIndex();
            return deleted;
        } catch (IOException e) {
            System.out.println("Failed to delete entry: " + e.getMessage());
            return false;
        }
    }

    // search by keyword in content or title/tags
    public List<Path> searchEntries(String query) {
        List<Path> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return results;
        String q = query.toLowerCase();
        for (Path p : listEntries()) {
            String fname = p.getFileName().toString();
            EntryMeta meta = index.get(fname);
            boolean matched = false;
            if (meta != null) {
                if (meta.title != null && meta.title.toLowerCase().contains(q)) matched = true;
                for (String t : meta.tags) if (t.toLowerCase().contains(q)) matched = true;
            }
            if (!matched) {
                try (BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.toLowerCase().contains(q)) {
                            matched = true;
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Failed to read file during search: " + e.getMessage());
                }
            }
            if (matched) results.add(p);
        }
        return results;
    }

    // index persistence
    private void loadIndex() {
        if (!Files.exists(indexFile)) return;
        try {
            Map<String, EntryMeta> loaded = mapper.readValue(indexFile.toFile(), new TypeReference<Map<String, EntryMeta>>() {});
            index.clear();
            if (loaded != null) index.putAll(loaded);
        } catch (IOException e) {
            System.out.println("Failed to load index: " + e.getMessage());
        }
    }

    private void saveIndex() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(indexFile.toFile(), index);
        } catch (IOException e) {
            System.out.println("Failed to save index: " + e.getMessage());
        }
    }

    // export all entries+meta to a JSON file (portable)
    public Path exportToJson(Path outFile) {
        List<ExportEntry> list = new ArrayList<>();
        for (Path p : listEntries()) {
            String fname = p.getFileName().toString();
            EntryMeta meta = index.getOrDefault(fname, new EntryMeta());
            String content = readEntry(p);
            list.add(new ExportEntry(fname, meta.title, meta.tags, content));
        }
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(outFile.toFile(), list);
            return outFile;
        } catch (IOException e) {
            System.out.println("Failed to export to JSON: " + e.getMessage());
            return null;
        }
    }

    // import JSON created by exportToJson (merge into entries folder)
    public int importFromJson(Path inFile) {
        if (!Files.exists(inFile)) return 0;
        try {
            List<ExportEntry> list = mapper.readValue(inFile.toFile(), new TypeReference<List<ExportEntry>>() {});
            int added = 0;
            for (ExportEntry ee : list) {
                // avoid clobbering: if filename already exists, create new filename with timestamp
                Path target = baseDir.resolve(ee.filename);
                if (Files.exists(target)) {
                    String altName = "diary_import_" + LocalDateTime.now().format(fileFormatter) + ".txt";
                    target = baseDir.resolve(altName);
                    ee.filename = altName;
                }
                // write content
                try (BufferedWriter bw = Files.newBufferedWriter(target, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                    bw.write(ee.content != null ? ee.content : "");
                    bw.flush();
                    index.put(target.getFileName().toString(), new EntryMeta(ee.title, ee.tags));
                    added++;
                } catch (IOException e) {
                    System.out.println("Failed to write imported entry " + ee.filename + ": " + e.getMessage());
                }
            }
            saveIndex();
            return added;
        } catch (IOException e) {
            System.out.println("Failed to import JSON: " + e.getMessage());
            return 0;
        }
    }

    // small DTO used for JSON export/import
    public static class ExportEntry {
        public String filename;
        public String title;
        public List<String> tags = new ArrayList<>();
        public String content;
        public ExportEntry() {}
        public ExportEntry(String filename, String title, List<String> tags, String content) {
            this.filename = filename;
            this.title = title;
            this.tags = tags == null ? new ArrayList<>() : new ArrayList<>(tags);
            this.content = content;
        }
    }

    // retrieve metadata for UI
    public EntryMeta getMeta(String filename) {
        return index.get(filename);
    }

    // --- Backup to ZIP (restores earlier functionality) ---
    public Path backupToZip(String backupDirName) {
        Path backupDir = Paths.get(backupDirName == null || backupDirName.isEmpty() ? "backups" : backupDirName);
        try {
            Files.createDirectories(backupDir);
        } catch (IOException e) {
            System.out.println("Failed to create backup directory: " + e.getMessage());
            return null;
        }

        String zipName = "diary_backup_" + LocalDateTime.now().format(zipFormatter) + ".zip";
        Path zipPath = backupDir.resolve(zipName);

        try (OutputStream fos = Files.newOutputStream(zipPath, StandardOpenOption.CREATE_NEW);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (Path entry : listEntries()) {
                ZipEntry ze = new ZipEntry(entry.getFileName().toString());
                zos.putNextEntry(ze);
                try (InputStream is = Files.newInputStream(entry, StandardOpenOption.READ)) {
                    byte[] buffer = new byte[4096];
                    int read;
                    while ((read = is.read(buffer)) != -1) {
                        zos.write(buffer, 0, read);
                    }
                }
                zos.closeEntry();
            }
            return zipPath;
        } catch (IOException e) {
            System.out.println("Failed to create backup ZIP: " + e.getMessage());
            return null;
        }
    }

    // --- Simple serialization of application state (entry count + last saved time) ---
    public boolean saveState() {
        DiaryState state = new DiaryState();
        state.entryCount = listEntries().size();
        state.lastSaved = LocalDateTime.now();
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(stateFile,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
            oos.writeObject(state);
            return true;
        } catch (IOException e) {
            System.out.println("Failed to save state: " + e.getMessage());
            return false;
        }
    }

    public DiaryState loadState() {
        if (!Files.exists(stateFile)) return null;
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(stateFile, StandardOpenOption.READ))) {
            Object obj = ois.readObject();
            if (obj instanceof DiaryState) return (DiaryState) obj;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed to load state: " + e.getMessage());
        }
        return null;
    }

    public static class DiaryState implements Serializable {
        private static final long serialVersionUID = 1L;
        public int entryCount;
        public LocalDateTime lastSaved;
    }
}
