package tracker;

import tracker.controllers.Manager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager(); // Создал менеджера, который управляет задачами
        Epic epic1 = new Epic("Переезд", "Переезжаем из пункта А в пункт Б", "NEW");
        Subtask subtask1ForEpic1 = new Subtask("Собрать кошку", "Упаковать все в чемодан", "DONE",
                epic1); // Первая подзадача первого эпика
        Subtask subtask2ForEpic1 = new Subtask("Найти машину", "Заказать грузовик", "NEW",
                epic1); // Вторая подзадача первого эпика
        Epic epic2 = new Epic("Найти работу", "Ходить на собеседования", "NEW");
        Subtask subtask1ForEpic2 = new Subtask("Пройти успешно собеседование", "Произвести впечатление",
                "DONE", epic2);
        Task task = new Task("Сходить к доктору", "Посетить терапевта", "NEW");
        manager.saveTask(epic1); // Сохраняем эпическую задачу 1
        manager.saveTask(subtask1ForEpic1); // Сохраняем первую позадачу первого эпика
        manager.saveTask(subtask2ForEpic1); // Сохраняем вторую позадачу первого эпика
        manager.saveTask(epic2); // Сохраняем эпическую задачу 2
        manager.saveTask(subtask1ForEpic2); // Сохраняем первую (единственную подзадачу) второго эпика
        manager.saveTask(task); // Сохраняем обычную задачу
        System.out.println(manager.getListOfAllEpic()); // Печатаем список всех эпических задач
        System.out.println(manager.getListOfAllSubTasks()); // Печатаем список всех подзадач
        System.out.println(manager.getListOfAllTasks()); // Печатаем список всех обычных задач

        Epic epic3 = new Epic("Держаться достойной на новой работе", "Не опозорится", "NEW",
                1); // Новый эпик для проверки обновления
        System.out.println(manager.getAllSubTasksAttitudeToEpic(epic2));
        manager.deleteEpicById(2); // Удаляем второй эпик
        manager.updateEpic(epic3); // Обновляем первый эпик, тот, что остался
        // Проверяем удалился ли эпик и все его подзадачи
        System.out.println(manager.getListOfAllEpic()); // Печатаем заного список со всеми эпиками
        System.out.println(manager.getListOfAllSubTasks()); // Печатаем заного список всех подзадач
        manager.deleteTaskById(1); // Удаляем обычную задачу
        System.out.println(manager.getListOfAllTasks()); // Печатаем заного список с обычными задачами
        Subtask subTaskRefresh = new Subtask("Собрать кролика", "Посадить его в клетку",
                "IN_PROGRESS", epic3, 1); // Обновляем первую подзадачу первого эпика
        manager.updateSubTask(subTaskRefresh); // Вызываем метод обновления
        System.out.println(manager.getListOfAllSubTasks()); // Печатаем заного список всех подзадач
        System.out.println(manager.getListOfAllEpic()); // Печатаем заного список со всеми эпиками, чтоб проверить
        // обновится ли статус эпика после обновления подзадачи
        System.out.println(manager.getAllSubTasksAttitudeToEpic(epic3));
        // Проверяем обновится ли статус эпика, если мы удалим подзадачу по ID
        manager.deleteSubTaskById(1); // удаляем кролика
        System.out.println(manager.getListOfAllEpic()); // проверяем обновился ли статус эпика
    }
}
