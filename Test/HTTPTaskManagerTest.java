import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.server.HTTPTaskManager;
import tracker.server.KVServer;
import tracker.server.MyJsonForDifferentTypeOfTasks;
import tracker.controllers.Managers;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.IOException;

public class HTTPTaskManagerTest {

    KVServer kvServer; // Создал сервер
    HTTPTaskManager httpTaskManager; // Менеджен для связывания сервера с его обработчиком
    // Объект класса содержащий все требуемые типы Json
    MyJsonForDifferentTypeOfTasks gson = new MyJsonForDifferentTypeOfTasks();

    @BeforeEach
    public void launchKVServer() throws IOException {
        kvServer = new KVServer(); // Создали KVServer
        kvServer.start(); // Запускаем KVServer
        httpTaskManager = (HTTPTaskManager) Managers.getDefault(); // Создали менеджера
    }

    @AfterEach
    void stopKVServer() throws IOException {
        kvServer.stop(); // останавливаем KVServer
    }

    @Test
    public void saveTaskTest() {
        // Создаем задачу для теста
        Task task = new Task("Task", "Task", Status.NEW, null, null);
        httpTaskManager.createTask(task);
        // Вызываем метод
        httpTaskManager.save();
        Assertions.assertNotNull(kvServer.getData(), "Хранилище содержит задачу");
    }

    @Test
    public void saveSubTaskTest() {
        // Создаем подзадачу для теста
        Subtask subtask = new Subtask("SubTask", "SubTask", Status.NEW,
                new Epic("Epic", "Epic", Status.NEW, null, null),
                null, null);
        httpTaskManager.createTask(subtask);
        // Вызываем метод
        httpTaskManager.save();
        Assertions.assertNotNull(kvServer.getData(), "Хранилище содержит подзадачу");
    }

    @Test
    public void saveEpicTest() {
        // Создаем эпик для теста
        Epic epic = new Epic("Epic", "Epic", Status.NEW, null, null);
        httpTaskManager.createTask(epic);
        // Вызываем метод
        httpTaskManager.save();
        Assertions.assertNotNull(kvServer.getData(), "Хранилище содержит эпик");
    }

    @Test
    public void loadFromKVServerTaskTest() {
        // Создаем задачу для теста
        Task task = new Task("Task", "Task", Status.NEW, null, null);
        httpTaskManager.createTask(task);
        // Вызываем метод, чтоб было, что забирать с сервера
        httpTaskManager.save();
        // Удаляем значение из мапы, чтоб точно быть увереными, что данные вернулись именно с KVServer
        httpTaskManager.getSaveTask().clear();
        // Вызываем метод
        httpTaskManager.loadFromKVServer("tasks");
        Assertions.assertNotNull(httpTaskManager.getForReturnConditionManagerTaskAndSubtaskAndEpic(),
                "Задача успешно выгружена с KVServer");
    }

    @Test
    public void loadFromKVServerSubTaskTest() {
        // Создаем подзадачу для теста
        Subtask subtask = new Subtask("SubTask", "SubTask", Status.NEW,
                new Epic("Epic", "Epic", Status.NEW, null, null),
                null, null);
        httpTaskManager.createTask(subtask);
        // Вызываем метод, чтоб было, что забирать с сервера
        httpTaskManager.save();
        // Удаляем значение из мапы, чтоб точно быть увереными, что данные вернулись именно с KVServer
        httpTaskManager.getSaveSubTask().clear();
        // Вызываем метод
        httpTaskManager.loadFromKVServer("subtasks");
        Assertions.assertNotNull(httpTaskManager.getForReturnConditionManagerTaskAndSubtaskAndEpic(),
                "Подадача успешно выгружена с KVServer");
    }

    @Test
    public void loadFromKVServerEpicTest() {
        // Создаем подзадачу для теста
        Epic epic = new Epic("Epic", "Epic", Status.NEW, null, null);
        httpTaskManager.createTask(epic);
        // Вызываем метод, чтоб было, что забирать с сервера
        httpTaskManager.save();
        // Удаляем значение из мапы, чтоб точно быть увереными, что данные вернулись именно с KVServer
        httpTaskManager.getSaveEpic().clear();
        // Вызываем метод
        httpTaskManager.loadFromKVServer("epics");
        Assertions.assertNotNull(httpTaskManager.getForReturnConditionManagerTaskAndSubtaskAndEpic(),
                "Эпик успешно выгружен с KVServer");
    }

    @Test
    public void saveHistoryTest() {
        // Создаем задачу для теста
        Task task = new Task("Task", "Task", Status.NEW, null, null);
        httpTaskManager.createTask(task);
        // Добавляем задачу в историю
        httpTaskManager.getHistoryManager().addTask(task);
        // Вызываем метод
        httpTaskManager.save();
        Assertions.assertNotNull(kvServer.getData(), "Хранилище содержит историю");
    }

    @Test
    public void loadFromKVServerHistoryTest() {
        // Создаем задачу для теста
        Task task = new Task("Task", "Task", Status.NEW, null, null);
        httpTaskManager.createTask(task);
        httpTaskManager.getHistoryManager().addTask(task); // Добавляем задачу в историю
        // Вызываем метод, чтоб было, что забирать с сервера
        httpTaskManager.save();
        // Удаляем значение из мапы и истории, чтоб точно быть увереными, что данные вернулись именно с KVServer
        httpTaskManager.getSaveTask().clear();
        httpTaskManager.getHistoryManager().getHistory().remove(task);
        // Вызываем метод
        httpTaskManager.loadFromKVServer("history");
        Assertions.assertNotNull(httpTaskManager.getForReturnConditionManagerHistory(),
                "История успешно выгружена с KVServer");
    }
}
