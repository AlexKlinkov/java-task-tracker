package tracker.controllers;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultBackedTaskManager(File file) {
        return new FileBackedTasksManager(getDefaultHistory(), file);
    }
}
