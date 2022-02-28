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

        // Эпик с 3-мя подзадачами
        Epic epic1 = new Epic("Пройти курс по Java", "Сдать вовремя все задачи", Status.NEW);
        Subtask subtask1 = new Subtask("Сдать 1-ю задачу", "Написать хороший код", Status.NEW, epic1);
        Subtask subtask2 = new Subtask("Сдать 2-ю задачу", "Написать хороший код", Status.DONE, epic1);
        Subtask subtask3 = new Subtask("Сдать 3-ю задачу", "Написать хороший код", Status.DONE, epic1);
        taskManager.createTask(epic1); // Создаем задачу
        taskManager.createTask(subtask1); // Создаем задачу
        taskManager.createTask(subtask2); // Создаем задачу
        taskManager.createTask(subtask3); // Создаем задачу
        taskManager.getEpicById(1); // Запрашиваем (получаем) эпик ID-1
        taskManager.getSubTaskById(2); // Запрашиваем подзадачу № 1, ID-2
        taskManager.getSubTaskById(3); // Запрашиваем подзадачу № 2, ID-3
        taskManager.getSubTaskById(4); // Запрашиваем подзадачу № 3, ID-4
        System.out.println("История в порядке запрашивания элементов");
        System.out.println(historyManager.getHistory()); // Запрашиваем историю и проверяем порядок элементов
        taskManager.getSubTaskById(2); // Запрашиваем повторно первую подзадачу с ID-2
        System.out.println("История, после повторного запроса элемента № 2, который после запроса станет последним.");
        System.out.println(historyManager.getHistory()); // Проверяем, что нет повторов и порядок элементов правильный
        historyManager.remove(3); // Удаляем вторую подзадачу с ID-3
        System.out.println("История в правильном порядке, после удаления элемента № 3");
        System.out.println(historyManager.getHistory()); // проверяем, что порядок в истории сохранился,
        // после удаления подзадачи № 2 с ID-3

        // Эпик без подзадач
        Epic epic2 = new Epic("Пережить трудные времена", "Не падать духом", Status.NEW);
        taskManager.createTask(epic2); // Создаем задачу
        taskManager.getEpicById(5); // Запрашиваем (получаем) эпик с ID-5
        System.out.println("Выводим историю, в конец должен добавится эпик с ID-5");
        System.out.println(historyManager.getHistory());
        taskManager.deleteEpicById(1); // Удаляем 1-ый эпик
        System.out.println("Удалили эпик № 1 из истории, а значит его подзадачи, " +
                "тоже удалятся и останется только эпик № 2, с ID-5");
        System.out.println(historyManager.getHistory());
    }
}