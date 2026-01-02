package org.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Menu - user interaction extended with metadata, export/import, edit/delete and paging.
 */
public class Menu {
    private final DiaryManager diary;
    private final Scanner in = new Scanner(System.in);
    private final int PAGE_LINES = 20;

    public Menu(DiaryManager diary) {
        this.diary = diary;
    }

    public void loop() {
        while (true) {
            System.out.println();
            System.out.println("1) Write entry (with title & tags)");
            System.out.println("2) List & Read entry (paged)");
            System.out.println("3) Search entries");
            System.out.println("4) Backup (zip)");
            System.out.println("5) Export to JSON");
            System.out.println("6) Import from JSON");
            System.out.println("7) Edit entry (content/title/tags)");
            System.out.println("8) Delete entry");
            System.out.println("9) Save application state (serialize)");
            System.out.println("0) Exit");
            System.out.print("Choose: ");
            String choice = in.nextLine().trim();
            switch (choice) {
                case "1": writeFlow(); break;
                case "2": listAndReadPagedFlow(); break;
                case "3": searchFlow(); break;
                case "4": backupFlow(); break;
                case "5": exportFlow(); break;
                case "6": importFlow(); break;
                case "7": editFlow(); break;
                case "8": deleteFlow(); break;
                case "9": saveStateFlow(); break;
                case "0": System.out.println("Exiting."); return;
                default: System.out.println("Unknown option."); break;
            }
        }
    }

    private void writeFlow() {
        System.out.print("Title: ");
        String title = in.nextLine().trim();
        System.out.print("Tags (comma separated): ");
        String tagsLine = in.nextLine().trim();
        List<String> tags = parseTags(tagsLine);
        System.out.println("Enter your diary text. Finish with an empty line.");
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = in.nextLine();
            if (line == null || line.isEmpty()) break;
            sb.append(line).append(System.lineSeparator());
        }
        Path saved = diary.writeEntry(title.isEmpty() ? null : title, tags, sb.toString());
        if (saved != null) System.out.println("Saved: " + saved.toAbsolutePath());
    }

    private List<String> parseTags(String line) {
        if (line == null || line.trim().isEmpty()) return new ArrayList<>();
        String[] parts = line.split(",");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) out.add(t);
        }
        return out;
    }

    private void listAndReadPagedFlow() {
        List<Path> entries = diary.listEntries();
        if (entries.isEmpty()) { System.out.println("No entries found."); return; }
        for (int i = 0; i < entries.size(); i++) {
            String fname = entries.get(i).getFileName().toString();
            DiaryManager.EntryMeta meta = diary.getMeta(fname);
            String title = meta != null && meta.title != null ? meta.title : "";
            System.out.printf("%d) %s %s%n", i + 1, fname, title.isEmpty() ? "" : ("| " + title));
        }
        System.out.print("Choose number to read (or empty to cancel): ");
        String s = in.nextLine().trim();
        if (s.isEmpty()) return;
        try {
            int idx = Integer.parseInt(s) - 1;
            if (idx < 0 || idx >= entries.size()) { System.out.println("Invalid selection."); return; }
            readPaged(entries.get(idx));
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }
    }

    private void readPaged(Path path) {
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                count++;
                if (count >= PAGE_LINES) {
                    System.out.print("--more-- (Enter to continue, q to quit) ");
                    String cmd = in.nextLine();
                    if ("q".equalsIgnoreCase(cmd)) return;
                    count = 0;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to read file: " + e.getMessage());
        }
    }

    private void searchFlow() {
        System.out.print("Enter search keyword: ");
        String q = in.nextLine().trim();
        List<Path> results = diary.searchEntries(q);
        if (results.isEmpty()) { System.out.println("No matches."); return; }
        for (int i = 0; i < results.size(); i++) {
            String fname = results.get(i).getFileName().toString();
            DiaryManager.EntryMeta meta = diary.getMeta(fname);
            String title = meta != null && meta.title != null ? meta.title : "";
            System.out.printf("%d) %s %s%n", i + 1, fname, title.isEmpty() ? "" : ("| " + title));
        }
        System.out.print("Choose number to read (or empty to cancel): ");
        String s = in.nextLine().trim();
        if (s.isEmpty()) return;
        try {
            int idx = Integer.parseInt(s) - 1;
            if (idx < 0 || idx >= results.size()) { System.out.println("Invalid selection."); return; }
            readPaged(results.get(idx));
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }
    }

    private void backupFlow() {
        System.out.print("Backup directory (default: backups): ");
        String dir = in.nextLine().trim();
        if (dir.isEmpty()) dir = "backups";
        // reuse existing method name backupToZip (if present)
        Path zip = diary.backupToZip(dir);
        if (zip != null) System.out.println("Backup created: " + zip.toAbsolutePath());
    }

    private void exportFlow() {
        System.out.print("Export JSON path (default: diary_export.json): ");
        String p = in.nextLine().trim();
        if (p.isEmpty()) p = "diary_export.json";
        Path out = Paths.get(p);
        Path res = diary.exportToJson(out);
        if (res != null) System.out.println("Exported to: " + res.toAbsolutePath());
    }

    private void importFlow() {
        System.out.print("Import JSON path: ");
        String p = in.nextLine().trim();
        if (p.isEmpty()) { System.out.println("Cancelled."); return; }
        Path inFile = Paths.get(p);
        int added = diary.importFromJson(inFile);
        System.out.println("Imported entries: " + added);
    }

    private void editFlow() {
        List<Path> entries = diary.listEntries();
        if (entries.isEmpty()) { System.out.println("No entries."); return; }
        for (int i = 0; i < entries.size(); i++) {
            String fname = entries.get(i).getFileName().toString();
            DiaryManager.EntryMeta meta = diary.getMeta(fname);
            String title = meta != null && meta.title != null ? meta.title : "";
            System.out.printf("%d) %s %s%n", i + 1, fname, title.isEmpty() ? "" : ("| " + title));
        }
        System.out.print("Choose number to edit (or empty to cancel): ");
        String s = in.nextLine().trim();
        if (s.isEmpty()) return;
        try {
            int idx = Integer.parseInt(s) - 1;
            if (idx < 0 || idx >= entries.size()) { System.out.println("Invalid selection."); return; }
            Path target = entries.get(idx);
            String fname = target.getFileName().toString();
            DiaryManager.EntryMeta meta = diary.getMeta(fname);
            System.out.print("New title (leave empty to keep): ");
            String newTitle = in.nextLine();
            System.out.print("New tags (comma separated, leave empty to keep): ");
            String tagsLine = in.nextLine();
            List<String> newTags = tagsLine.isEmpty() ? null : parseTags(tagsLine);
            System.out.println("Current content preview (first 5 lines):");
            try (BufferedReader br = Files.newBufferedReader(target, StandardCharsets.UTF_8)) {
                for (int i = 0; i < 5; i++) {
                    String l = br.readLine();
                    if (l == null) break;
                    System.out.println(l);
                }
            } catch (IOException ignore) {}
            System.out.println("Enter new full content. Finish with empty line. Leave completely empty to keep existing content.");
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = in.nextLine();
                if (line == null || line.isEmpty()) break;
                sb.append(line).append(System.lineSeparator());
            }
            String newText = sb.length() == 0 ? null : sb.toString();
            boolean ok = diary.editEntry(fname, newTitle.isEmpty() ? null : newTitle, newTags, newText);
            System.out.println(ok ? "Edited." : "Edit failed.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }
    }

    private void deleteFlow() {
        List<Path> entries = diary.listEntries();
        if (entries.isEmpty()) { System.out.println("No entries."); return; }
        for (int i = 0; i < entries.size(); i++) {
            String fname = entries.get(i).getFileName().toString();
            DiaryManager.EntryMeta meta = diary.getMeta(fname);
            String title = meta != null && meta.title != null ? meta.title : "";
            System.out.printf("%d) %s %s%n", i + 1, fname, title.isEmpty() ? "" : ("| " + title));
        }
        System.out.print("Choose number to delete (or empty to cancel): ");
        String s = in.nextLine().trim();
        if (s.isEmpty()) return;
        try {
            int idx = Integer.parseInt(s) - 1;
            if (idx < 0 || idx >= entries.size()) { System.out.println("Invalid selection."); return; }
            String fname = entries.get(idx).getFileName().toString();
            System.out.print("Confirm delete " + fname + " (y/N): ");
            String conf = in.nextLine().trim();
            if (!"y".equalsIgnoreCase(conf)) { System.out.println("Cancelled."); return; }
            boolean ok = diary.deleteEntry(fname);
            System.out.println(ok ? "Deleted." : "Delete failed.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }
    }

    private void saveStateFlow() {
        boolean ok = diary.saveState();
        System.out.println(ok ? "State saved." : "State save failed.");
    }
}
