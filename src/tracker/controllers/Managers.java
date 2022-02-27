package tracker.controllers;

public class Managers {

    private static final HistoryManager historyManager = new InMemoryHistoryManager();
    private static final TaskManager usualManager = new InMemoryTaskManager(historyManager);

    public static TaskManager getDefault() {
        return usualManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}