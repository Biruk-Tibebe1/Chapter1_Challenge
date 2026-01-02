# DiaryManager

A command-line diary application. Each entry is a timestamped text file. Features:
- Per-entry metadata (title, tags) stored in index.json
- Read / write / edit / delete entries
- Search entries (content, title, tags)
- JSON export / import for portability
- ZIP backup of all entries
- Paging when reading large entries
- Simple serialized application state (optional)

---

## What’s new / design summary

- Entries: saved as `diary_yyyy_MM_dd_HH_mm_ss.txt` under `entries/`.
- Metadata: `entries/index.json` maps filename → { title, tags } (keeps metadata separate from content).
- JSON export/import: portable export of all entries and metadata; import merges entries and avoids clobbering.
- Edit/Delete: modify entry content and metadata or remove an entry and its index entry.
- Paging: long entries are displayed page-by-page (press Enter to continue, `q` to quit).
- Backup: creates `diary_backup_yyyyMMdd_HHmmss.zip` in the chosen backup dir.
- Uses BufferedReader/BufferedWriter and java.nio.file.Files for all IO.

---

## Project layout

Important paths (relative to project root):
- Sources: `src/main/java/org/example/`
- Entries directory (created automatically): `./entries`
  - Files: `diary_*.txt`
  - Metadata: `index.json`
  - State (optional): `state.ser`
- Backups (default): `./backups`
- Export default filename: `diary_export.json`

---

## Build

Requires JDK 17 and Maven.

Recommended: build shaded (uber) JAR (pom.xml includes maven-shade-plugin):

PowerShell:
```powershell
cd \Chapter4_challenge1_DiaryManager
mvn clean package
```

This produces a shaded jar (example):
- `target\Chapter4_challenge1_DiaryManager-1.0-SNAPSHOT-shaded.jar`
or the normal jar:
- `target\Chapter4_challenge1_DiaryManager-1.0-SNAPSHOT.jar`

If you prefer not to build a shaded jar you can run with dependencies on the classpath (see Run section).

Note: Maven may warn about -source/-target 17; build succeeds with JDK 17.

---

## Run

Option A — run shaded jar:
```powershell
cd \Chapter4_challenge1_DiaryManager
java -jar .\target\Chapter4_challenge1_DiaryManager-1.0-SNAPSHOT-shaded.jar
```

Option B — run with dependencies copied:
```powershell
mvn dependency:copy-dependencies -DoutputDirectory=target\dependency
java -cp "target\classes;target\dependency/*" org.example.Main
```

Option C — via Maven exec plugin:
```powershell
mvn -Dexec.mainClass="org.example.Main" org.codehaus.mojo:exec-maven-plugin:3.1.0:java
```

---

## Menu / Usage

When running, the menu options are:

1) Write entry (with title & tags)  
2) List & Read entry (paged)  
3) Search entries  
4) Backup (zip)  
5) Export to JSON  
6) Import from JSON  
7) Edit entry (content/title/tags)  
8) Delete entry  
9) Save application state (serialize)  
0) Exit

Quick workflows:

- Write entry:
  - Choose `1`
  - Enter Title, Tags (comma-separated), then content lines; finish with an empty line.
  - File saved to `entries/`; metadata saved to `index.json`.

- List & Read (paged):
  - Choose `2`
  - Pick a number from list to read; long entries pause every page (press Enter to continue, `q` to quit).

- Search:
  - Choose `3`
  - Enter keyword (case-insensitive); results show filename + title; choose to read.

- Backup:
  - Choose `4`, enter backup directory name (default `backups`), ZIP created.

- Export:
  - Choose `5`, specify path (default `diary_export.json`), JSON file created with all entries + meta.

- Import:
  - Choose `6`, provide JSON file path. Imported entries are added; filename collisions are avoided by renaming.

- Edit:
  - Choose `7`, pick an entry, optionally update title/tags, preview, and provide new full content. Leave content empty to keep existing content.

- Delete:
  - Choose `8`, pick entry, confirm `y` to delete file and metadata.

- Save state:
  - Choose `9` to write `entries/state.ser` (small serialized DiaryState).

---

## File formats

- Entry content: plain UTF-8 text files.
- Index: `index.json` — JSON object mapping filename → { title, tags }.
- Export file: JSON array of objects { filename, title, tags, content }.
- Backup: ZIP containing the diary_*.txt files.

---

## Troubleshooting

- NoClassDefFoundError for Jackson at runtime: run shaded jar or include dependency jars on classpath (see Run section).
- If shaded jar name differs, list `target\` contents:
  ```powershell
  Get-ChildItem -Path .\target | Select-Object Name, Length
  ```
- Permission errors when creating `entries/` or `backups/`: run with appropriate user rights or change output directory.
- If import/export fails, validate JSON with an external tool — format must match the Export DTO.

---
