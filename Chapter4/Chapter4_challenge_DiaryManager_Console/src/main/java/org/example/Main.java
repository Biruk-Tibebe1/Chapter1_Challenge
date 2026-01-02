package org.example;

public class Main {
    public static void main(String[] args) {
        DiaryManager diary = new DiaryManager("entries");
        // show last saved state if available
        DiaryManager.DiaryState state = diary.loadState();
        if (state != null) {
            System.out.println("Loaded state: entries=" + state.entryCount + " lastSaved=" + state.lastSaved);
        }
        Menu menu = new Menu(diary);
        menu.loop();
    }
}
