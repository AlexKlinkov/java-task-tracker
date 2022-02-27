package tracker;

import tracker.controllers.HistoryManager;
import tracker.controllers.Managers;
import tracker.controllers.TaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;

public class Main {
    public static void main(String[] args) {
        HistoryManager historyManager = Managers.getDefaultHistory(); // Создали менеджера отвечающего за историю
        TaskManager taskManager = Managers.getDefault(); // создали главного менеджера

        Epic epic1 = new Epic("Пройти курс по Java", "Сдать вовремя все задачи", Status.NEW);
        Subtask subtask1 = new Subtask("Сдать 1-ю задачу", "Написать хороший код", Status.NEW, epic1);
        Subtask subtask2 = new Subtask("Сдать 2-ю задачу", "Написать хороший код", Status.DONE, epic1);
        Subtask subtask3 = new Subtask("Сдать 3-ю задачу", "Написать хороший код", Status.DONE, epic1);
        taskManager.createTask(epic1); // Создаем задачу
        taskManager.createTask(subtask1); // Создаем задачу
        taskManager.createTask(subtask2); // Создаем задачу
        taskManager.createTask(subtask3); // Создаем задачу
        historyManager.addTask(epic1); // добавляем задачу в список ID - 1
        historyManager.addTask(subtask1); // добавляем задачу в список ID - 2
        historyManager.addTask(subtask2); // добавляем задачу в список ID - 3
        historyManager.addTask(subtask3); // добавляем задачу в список ID - 4
        System.out.println(historyManager.getHistory()); // Печатаем историю
//      historyManager.addTask(subtask1); // добавляем задачу в список, чтоб проверить ли будет ли дубль в истории
//      taskManager.deleteEpicById(1); // Удаляем эпик, смотрим, что удалиться все подзадачи из истории
        historyManager.addTask(subtask2); // добавляем задачу в список ID - 3
        System.out.println(historyManager.getHistory()); // Печатаем историю заново
    }
}
