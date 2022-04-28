package tracker.controllers;

import tracker.Server.HTTPTaskManager;
import tracker.Server.KVTaskClient;

import java.io.File;

public class Managers {
   private static String url = "http://localhost:8078"; // Поле путь на сервер

    public static TaskManager getDefault() {
        return new HTTPTaskManager(getDefaultHistory(),
                new File("HttpServerTest"), new KVTaskClient(url), url);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultBackedTaskManager(File file) {
        return new FileBackedTasksManager(getDefaultHistory(), file);
    }

    public static String getUrl() {
        return url;
    }
}
