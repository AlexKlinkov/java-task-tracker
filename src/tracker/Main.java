package tracker;

import tracker.controllers.HistoryManager;
import tracker.controllers.Managers;
import tracker.controllers.TaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

public class Main {
    public static void main(String[] args) {
        HistoryManager historyManager = Managers.getDefaultHistory(); // Создали менеджера отвечающего за историю
        TaskManager taskManager = Managers.getDefault(historyManager); // создали главного менеджера
        Epic epic1 = new Epic("Переезд", "Переезжаем из пункта А в пункт Б", Status.NEW);
        Subtask subtask1ForEpic1 = new Subtask("Собрать кошку", "Упаковать все в чемодан", Status.DONE,
                epic1); // Первая подзадача первого эпика
        Subtask subtask2ForEpic1 = new Subtask("Найти машину", "Заказать грузовик", Status.NEW,
                epic1); // Вторая подзадача первого эпика
        Epic epic2 = new Epic("Найти работу", "Ходить на собеседования", Status.NEW);
        Subtask subtask1ForEpic2 = new Subtask("Пройти успешно собеседование", "Произвести впечатление",
                Status.DONE, epic2);
        Task task = new Task("Сходить к доктору", "Посетить терапевта", Status.NEW);
        taskManager.createTask(epic1); // Сохраняем эпическую задачу 1
        taskManager.createTask(subtask1ForEpic1); // Сохраняем первую позадачу первого эпика
        taskManager.createTask(subtask2ForEpic1); // Сохраняем вторую позадачу первого эпика
        taskManager.createTask(epic2); // Сохраняем эпическую задачу 2
        taskManager.createTask(subtask1ForEpic2); // Сохраняем первую (единственную подзадачу) второго эпика
        taskManager.createTask(task); // Сохраняем обычную задачу
        System.out.println(taskManager.getListOfAllEpic()); // Печатаем список всех эпических задач
        System.out.println(taskManager.getListOfAllSubTasks()); // Печатаем список всех подзадач
        System.out.println(taskManager.getListOfAllTasks()); // Печатаем список всех обычных задач

        Epic epic3 = new Epic("Держаться достойной на новой работе", "Не опозорится", Status.NEW,
                1); // Новый эпик для проверки обновления
        System.out.println(taskManager.getAllSubTasksAttitudeToEpic(epic2));
        taskManager.deleteEpicById(2); // Удаляем второй эпик
        taskManager.updateEpic(epic3); // Обновляем первый эпик, тот, что остался
        // Проверяем удалился ли эпик и все его подзадачи
        System.out.println(taskManager.getListOfAllEpic()); // Печатаем заного список со всеми эпиками
        System.out.println(taskManager.getListOfAllSubTasks()); // Печатаем заного список всех подзадач
        // inMemoryTaskManager.deleteTaskById(1); // Удаляем обычную задачу
        System.out.println(taskManager.getListOfAllTasks()); // Печатаем заного список с обычными задачами
        Subtask subTaskRefresh = new Subtask("Собрать кролика", "Посадить его в клетку",
                Status.IN_PROGRESS, epic3, 1); // Обновляем первую подзадачу первого эпика
        taskManager.updateSubTask(subTaskRefresh); // Вызываем метод обновления
        System.out.println(taskManager.getListOfAllSubTasks()); // Печатаем заного список всех подзадач
        System.out.println(taskManager.getListOfAllEpic()); // Печатаем заного список со всеми эпиками,
        // чтоб проверить обновится ли статус эпика после обновления подзадачи
        System.out.println(taskManager.getAllSubTasksAttitudeToEpic(epic3));
        // Проверяем обновится ли статус эпика, если мы удалим подзадачу по ID
        //inMemoryTaskManager.deleteSubTaskById(1); // удаляем кролика
        System.out.println(taskManager.getListOfAllEpic()); // проверяем обновился ли статус эпика

        System.out.println(historyManager.getHistory()); // выводим историю просмотров менеджера
        // Получаем задачи по ID, чтоб проверить как работает список с историей менеджера
        taskManager.getTaskById(1); // 1
        taskManager.getEpicById(1); // 2
        taskManager.getSubTaskById(1); // 3
        taskManager.getTaskById(1); // 4 +
        taskManager.getEpicById(1); // 5
        taskManager.getSubTaskById(1); // 6
        taskManager.getTaskById(1); // 7
        taskManager.getEpicById(1); // 8
        taskManager.getSubTaskById(1); // 9
        taskManager.getTaskById(1); // 10
        taskManager.getSubTaskById(1); // 11 Последнии три задачи должны оказаться в истории
        taskManager.getSubTaskById(1); // 12
        taskManager.getSubTaskById(1); // 13
        System.out.println(historyManager.getHistory()); // выводим историю просмотров снова, чтоб проверить что
        // список заполняется историей
        System.out.println(historyManager.getHistory().size()); // размер истории менеджера
        taskManager.deleteTaskById(1); // удаляем задачу, чтоб посмотреть исчезнет ли она из истории
        System.out.println(historyManager.getHistory()); // выводим историю просмотров снова
        System.out.println(historyManager.getHistory().size()); // размер истории менеджера

        taskManager.deleteAllSubTasks(); // Удаляем все подзадачи
        System.out.println(historyManager.getHistory()); // проверяем удалились ли все сабтаски из истории
        System.out.println(historyManager.getHistory().size()); // размер истории менеджера

    }
}
