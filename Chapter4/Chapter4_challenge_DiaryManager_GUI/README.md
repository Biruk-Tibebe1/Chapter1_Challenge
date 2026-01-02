# Personal Diary Manager — JavaFX GUI

A simple desktop personal diary manager implemented with JavaFX. The UI is an HTML/CSS dashboard rendered in a JavaFX `WebView` and communicates with Java code for entry editing and autosave.

Links to important files and symbols
- Application entry: [src/main/java/org/example/personaldiarymanagergui/DiaryApplication.java](src/main/java/org/example/personaldiarymanagergui/DiaryApplication.java)  
- Main controller and Java↔JS bridge: [`org.example.personaldiarymanagergui.DiaryController`](src/main/java/org/example/personaldiarymanagergui/DiaryController.java) ([openNewEntryEditor](src/main/java/org/example/personaldiarymanagergui/DiaryController.java#L1), [editEntry](src/main/java/org/example/personaldiarymanagergui/DiaryController.java#L1), [saveEntry](src/main/java/org/example/personaldiarymanagergui/DiaryController.java#L1))
- FXML layout used by JavaFX: [src/main/resources/org/example/personaldiarymanagergui/dashboard-view.fxml](src/main/resources/org/example/personaldiarymanagergui/dashboard-view.fxml)
- HTML dashboard and JS UI: [src/main/resources/org/example/personaldiarymanagergui/DiaryUI/index.html](src/main/resources/org/example/personaldiarymanagergui/DiaryUI/index.html)
- Dashboard styles: [src/main/resources/org/example/personaldiarymanagergui/DiaryUI/style.css](src/main/resources/org/example/personaldiarymanagergui/DiaryUI/style.css)
- Theme CSS: [src/main/resources/org/example/personaldiarymanagergui/light-theme.css](src/main/resources/org/example/personaldiarymanagergui/light-theme.css), [src/main/resources/org/example/personaldiarymanagergui/dark-theme.css](src/main/resources/org/example/personaldiarymanagergui/dark-theme.css)
- Example saved entries (runtime artifacts): [entry-1.html](entry-1.html), [entry-2.html](entry-2.html), [new-entry.html](new-entry.html), [current-entry.html](current-entry.html)
- Build and run: [pom.xml](pom.xml), [mvnw](mvnw), [mvnw.cmd](mvnw.cmd)
- Module descriptor: [src/main/java/module-info.java](src/main/java/module-info.java)

Design choices (why things are the way they are)
- HTML/CSS dashboard in `WebView`  
  - Renders a responsive, modern UI using standard web tech (see [index.html](src/main/resources/org/example/personaldiarymanagergui/DiaryUI/index.html) and [style.css](src/main/resources/org/example/personaldiarymanagergui/DiaryUI/style.css)). This lets you iterate UI quickly without recompiling Java.
- Two-way JS ↔ Java communication  
  - The controller (`DiaryController`) injects itself into the `window` object (`window.diaryApp`) so JavaScript can call `diaryApp.openNewEntryEditor()` and `diaryApp.editEntry(...)`. The Java side can also call JS functions such as `updateEntry(...)` to update the in-page model. See [`DiaryController`](src/main/java/org/example/personaldiarymanagergui/DiaryController.java) initialize and JS calls in [index.html](src/main/resources/org/example/personaldiarymanagergui/DiaryUI/index.html).
- Simple file-based persistence (demo)  
  - To keep the prototype minimal and platform-friendly, entries are written as HTML files in the working directory (`new-entry.html` or `entry-{id}.html`) by [`DiaryController.saveEntry()`](src/main/java/org/example/personaldiarymanagergui/DiaryController.java). This is intentionally simple; swapping in a database or structured storage is straightforward later.
- HTMLEditor overlay for editing
  - The FXML layout uses a `WebView` for the dashboard and an `HTMLEditor` overlay (see [dashboard-view.fxml](src/main/resources/org/example/personaldiarymanagergui/dashboard-view.fxml)). This keeps viewing and editing contexts separated in the UI.
- Autosave with JavaFX `Timeline`
  - `DiaryController` starts a background `Timeline` to autosave every 5 seconds. This is a pragmatic trade-off for the demo: frequent saves without blocking the UI thread.

How to run (development)
1. Ensure a JDK and JavaFX compatible with the pom are installed and JAVA_HOME is set. If using the included Maven wrapper:
   - Linux/macOS:
     - ./mvnw clean javafx:run
   - Windows:
     - mvnw.cmd clean javafx:run
2. Or with a system Maven:
   - mvn clean javafx:run
3. The plugin is configured to launch the `Launcher` (see [pom.xml](pom.xml) and [src/main/java/org/example/personaldiarymanagergui/Launcher.java](src/main/java/org/example/personaldiarymanagergui/Launcher.java)) which calls `DiaryApplication.start(...)` in [DiaryApplication](src/main/java/org/example/personaldiarymanagergui/DiaryApplication.java).

Run-time behavior and usage
- Main dashboard
  - The HTML dashboard loads sample entries defined in the page JS and shows the "Today" feed, a sidebar of entries, a calendar view, and settings. See [index.html](src/main/resources/org/example/personaldiarymanagergui/DiaryUI/index.html).
- Create a new entry
  - Click the floating "+" button in the dashboard which calls `diaryApp.openNewEntryEditor()` and triggers [`DiaryController.openNewEntryEditor()`](src/main/java/org/example/personaldiarymanagergui/DiaryController.java). This displays the HTMLEditor overlay.
- Edit an entry
  - From dashboard, opening an entry triggers `diaryApp.editEntry(id, title, content)`, which calls [`DiaryController.editEntry(...)`](src/main/java/org/example/personaldiarymanagergui/DiaryController.java).
- Save and autosave
  - Manual: press "Save Entry" in the HTMLEditor toolbar to invoke [`onSaveButtonClick()`](src/main/java/org/example/personaldiarymanagergui/DiaryController.java), which calls `saveEntry()` and then attempts to update the JS model via `updateEntry(...)`.
  - Autosave: the app autosaves current editor content every 5 seconds to an HTML file (see `saveEntry()` implementation).
- UI update after save
  - For edited existing entries the controller attempts to call the JS function `updateEntry(id, title, content)` to update the in-page model. For new entries the demo reloads the `WebView` (simple fallback since there is no backend ID generation).

Implementation notes and caveats
- Escaping HTML/JS strings  
  - Passing rich HTML content between Java and JS is handled with basic escaping (single quotes and newlines). Complex HTML (containing quotes, scripts, or special characters) may break the ad-hoc escaping. For production, use structured messaging (JSON with base64 or DOM APIs) or a proper back-end.
- IDs and persistence  
  - Currently, the code uses `currentEditingId` to determine whether to write `new-entry.html` or `entry-{id}.html`. There is no ID allocation service; editing entries assumes IDs exist in the in-memory JS model. Persisted files are not re-imported automatically into the JS model — you will need to implement a loader to read saved HTML files into the UI.
- Module and JavaFX versions  
  - The project uses Java modules (`module-info.java`) and OpenJFX dependencies declared in `pom.xml`. Run-time issues often come from mismatched Java/OpenJFX versions or unset JAVA_HOME. See the Troubleshooting section below.
- Security  
  - The app renders HTML in `WebView` and uses `HTMLEditor`. Do not load untrusted remote HTML without sanitization.

Troubleshooting
- "JavaFX runtime components are missing" or module errors:
  - Ensure JavaFX SDK versions match the ones in `pom.xml` and that you either use the javafx-maven-plugin or pass the correct --add-modules to the JVM when running.
- "JAVA_HOME not set" when using mvnw:
  - Set JAVA_HOME to a JDK installation directory. The wrappers will complain otherwise.
- WebView JS bridge not working:
  - Confirm the HTML page finishes loading. The controller sets the JS bridge when the `WebEngine` load worker reaches `Worker.State.SUCCEEDED`.


Build & package
- Development run:
  - ./mvnw clean javafx:run
- Package (regular Maven packaging; packaging may require additional config for a self-contained image):
  - mvn clean package

License and attribution
- This repository is a learning/demo project. Replace or extend the persistence and sanitization logic before handling real user data.
