# Learning Projects — Java Lecture Demonstrations

This repository collects small Java projects I built to demonstrate topics from my lecture Java class and to let my lecturer evaluate my understanding. Each project is focused, self-contained, and includes a short explanation, how to run it, and what I learned.

## Purpose

I built these projects to practice Java fundamentals and some practical toolchains. Each project demonstrates one or more lecture topics: lifecycle and threading (applets), modern Java GUI (JavaFX), modularity, and simple persistence patterns. The goal is to show correct usage of APIs, readable structure, and that I can explain design choices. I present the projects so that a lecturer can run them, inspect the code, and grade both functional correctness and conceptual understanding.

---


## What I learned (short essay)

Working on these projects helped me connect lecture theory with practical tasks. The Applet assignment reinforced lifecycle and threading basics. The EcoLife widget taught me modern Java tooling, UI design with FXML, and safe background HTTP calls that update the UI. The Diary Manager showed how desktop apps can host web UIs and how to structure cross-technology communication. I practiced using Maven, module descriptors, and learned common pitfalls with JavaFX versions and JAVA_HOME configuration.

---

## Quick setup notes and troubleshooting

- JavaFX runtime: ensure the local JDK and JavaFX versions match the pom files. If you see "JavaFX runtime components are missing", check JAVA_HOME and the pom plugin configuration in each project (`pom.xml` links above).
- Maven wrapper: use `./mvnw` on Unix or `mvnw.cmd` on Windows inside each project directory.
- Legacy applet: Applets are deprecated. Use `appletviewer` bundled with older JDKs or run inside a compatible IDE. See [Chapter2_Challenge_Applet/applet.html](Chapter2_Challenge_Applet/applet.html).
- For the Diary Manager, the HTML dashboard persists sample files like `entry-1.html` and `entry-2.html` in the project root; these are runtime artifacts and can be opened with a browser or the app.

---

## Files & quick links

- Applet demo: [Chapter2_Challenge_Applet/src/BouncingTextApplet.java](Chapter2_Challenge_Applet/src/BouncingTextApplet.java), [Chapter2_Challenge_Applet/applet.html](Chapter2_Challenge_Applet/applet.html)
- EcoLife (JavaFX): [Chapter3_EcoLife/src/main/java/com/example/chapter3challenge1/HelloApplication.java](Chapter3_EcoLife/src/main/java/com/example/chapter3challenge1/HelloApplication.java), [`com.example.chapter3challenge1.HelloController`](Chapter3_EcoLife/src/main/java/com/example/chapter3challenge1/HelloController.java), [Chapter3_EcoLife/pom.xml](Chapter3_EcoLife/pom.xml)
- Diary Manager (JavaFX + WebView): [Chapter4/Chapter4_challenge_DiaryManager_GUI/src/main/java/org/example/personaldiarymanagergui/DiaryApplication.java](Chapter4/Chapter4_challenge_DiaryManager_GUI/src/main/java/org/example/personaldiarymanagergui/DiaryApplication.java), [`org.example.personaldiarymanagergui.DiaryController`](Chapter4/Chapter4_challenge_DiaryManager_GUI/src/main/java/org/example/personaldiarymanagergui/DiaryController.java), [Chapter4/Chapter4_challenge_DiaryManager_GUI/pom.xml](Chapter4/Chapter4_challenge_DiaryManager_GUI/pom.xml)

---
