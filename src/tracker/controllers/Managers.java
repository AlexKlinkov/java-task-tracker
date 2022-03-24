package tracker.controllers;

import java.io.File;
import java.io.IOException;

public class Managers {

    private static final HistoryManager historyManager = new InMemoryHistoryManager();
    private static final TaskManager usualManager = new InMemoryTaskManager(historyManager);

    public static TaskManager getDefault() {
        return usualManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }

    public static FileBackedTasksManager getDefaultBackedTaskManager(String file) throws IOException {
        FileBackedTasksManager backedTaskManager = new FileBackedTasksManager(historyManager, new File(file));
        return backedTaskManager;
    }

}
