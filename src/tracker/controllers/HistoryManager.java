package tracker.controllers;

import tracker.model.Task;

import java.util.List;

public interface HistoryManager { // интерфейс для управления историей просмотра

    void addTask(Task task); // помечает задачи, как просмотренные

    void remove(int id); // метод для удаление задачи по id

    List<Task> getHistory(); // Возвращает список с историей просмотров задач
}
