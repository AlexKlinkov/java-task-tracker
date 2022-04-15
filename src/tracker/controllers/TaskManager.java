package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {

    void createTask(Task task); // 1. Метод создающий задачу

    ArrayList<Task> getListOfAllTasks(); // 2.1 Метод для получения списка задач (отдельно стоящих)

    ArrayList<Subtask> getListOfAllSubTasks(); // 2.1 Метод для получения списка всех подзадач

    ArrayList<Epic> getListOfAllEpic(); // 2.1 Метод для получения списка всех сложных (эпических) задач

    void deleteAllTasks(); // 2.2 Метод для удаления всех задач

    void deleteAllSubTasks(); // 2.2 Метод для удаления всех подзадач

    void deleteAllEpic(); // 2.2 Метод для удаления всех сложных (эпических) задач

    Task getTaskById(int id); // 2.3 Метод для получения задачи по идентификатору

    Subtask getSubTaskById(int id); // 2.3 Метод для получения отдельно подзадачи по идентификатору

    Epic getEpicById(int id); // 2.3 Метод для получения отдельной (сложной) эпической задачи по идентификатору

    void updateTask(Task task) throws Exception; // 2.5 Обновление задачи по идентификатору

    void updateSubTask(Subtask subtask) throws Exception; // 2.5 Обновление подзадачи по идентификатору

    void updateEpic(Epic epic) throws Exception; // 2.5 Обновление задачи (сложные) эпические по идентификатору

    void deleteTaskById(int id); // 2.6 Удаление задачи по идентификатору

    void deleteSubTaskById(int id); // 2.6 Удаление подзадачи по идентификатору

    void deleteEpicById(int id); // 2.6 Удаление сложной (эпической) задачи по идентификатору

    ArrayList<Subtask> getAllSubTasksAttitudeToEpic(Epic epic); // 3.1 Метод для получения списка всех подзадач
    // определенного эпика

    HashMap<Integer, Task> getSaveTask(); // возвращает коллекцию задач

    HashMap<Integer, Subtask> getSaveSubTask(); // возвращает коллекцию подзадач

    HashMap<Integer, Epic> getSaveEpic(); // возвращает коллекцию эпиков

    HistoryManager getHistoryManager();
}