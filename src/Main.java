import tracker.server.KVServer;
import tracker.controllers.Managers;
import tracker.controllers.TaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException {
        KVServer kvServer;
        kvServer = new KVServer();
        kvServer.start();
        TaskManager taskManager = Managers.getDefault(); // создали главного менеджера

        // Эпик с 3-мя подзадачами
        Epic epic1 = new Epic("Пройти курс по Java", "Сдать вовремя все задачи", Status.NEW,
                null, null);
        Subtask subtask1 = new Subtask("Сдать 1-ю задачу", "Написать хороший код",
                Status.NEW, epic1, LocalDateTime.of(2020, 1, 1, 12, 0),
                Duration.ofHours(2));
        Subtask subtask2 = new Subtask("Сдать 2-ю задачу", "Написать хороший код",
                Status.DONE, epic1, LocalDateTime.of(2021, 1, 1, 12, 0),
                Duration.ofHours(2));
        Subtask subtask3 = new Subtask("Сдать 3-ю задачу", "Написать хороший код",
                Status.DONE, epic1, LocalDateTime.of(2022, 1, 1, 12, 0),
                Duration.ofHours(2));
        taskManager.createTask(epic1); // Создаем задачу
        taskManager.createTask(subtask1); // Создаем задачу
        taskManager.createTask(subtask2); // Создаем задачу
        taskManager.createTask(subtask3); // Создаем задачу
        taskManager.getEpicById(1); // Запрашиваем (получаем) эпик ID-1
        taskManager.getSubTaskById(2); // Запрашиваем подзадачу № 1, ID-2
        taskManager.getSubTaskById(3); // Запрашиваем подзадачу № 2, ID-3
        taskManager.getSubTaskById(4); // Запрашиваем подзадачу № 3, ID-4
        System.out.println("История в порядке запрашивания элементов");
        // Запрашиваем историю и проверяем порядок элементов
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getSubTaskById(2); // Запрашиваем повторно первую подзадачу с ID-2
        System.out.println("История, после повторного запроса элемента № 2, " +
                "                                                       который после запроса станет последним.");
        System.out.println(taskManager.getHistoryManager().getHistory()); // Проверяем, что нет повторов и
        // порядок элементов правильный
        taskManager.getHistoryManager().remove(3); // Удаляем вторую подзадачу с ID-3
        System.out.println("История в правильном порядке, после удаления элемента № 3");
        // проверяем, что порядок в истории сохранился, после удаления подзадачи № 2 с ID-3
        System.out.println(taskManager.getHistoryManager().getHistory());

        // Эпик без подзадач
        Epic epic2 = new Epic("Пережить трудные времена", "Не падать духом", Status.NEW,
                null, null);
        taskManager.createTask(epic2); // Создаем задачу
        taskManager.getEpicById(5); // Запрашиваем (получаем) эпик с ID-5
        System.out.println("Выводим историю, в конец должен добавится эпик с ID-5");
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.deleteEpicById(1); // Удаляем 1-ый эпик
        System.out.println("Удалили эпик № 1 из истории, а значит его подзадачи, " +
                "тоже удалятся и останется только эпик № 2, с ID-5");
        System.out.println(taskManager.getHistoryManager().getHistory());
    }
}

