import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.controllers.Managers;
import tracker.controllers.TaskManager;
import tracker.model.Status;
import tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

class HistoryManager {

    TaskManager manager;

    @BeforeEach
    void initialization() {
        manager = Managers.getDefault();
    }

    @Test
    void getHistoryTestWhenAddEmptyTaskListShouldBeEmpty() { // а. Пустая история задач
        manager.getHistoryManager().addTask(null); // Добавили задачу в историю
        Assertions.assertEquals(0, manager.getHistoryManager().getSize(), "История должна быть пустой");
    }

    @Test
    void getHistoryTestNotShouldTwoTheSameTask() { // b. Дублирование
        LocalDateTime startTime = LocalDateTime.of(2022, 04, 01, 12, 0);
        Duration duration = Duration.ofHours(2);
        Task task1 = new Task("Бросить курить", "Купить никотиновые пластыри", Status.NEW,
                startTime, duration);
        Task task2 = new Task("Бросить курить", "Купить никотиновые пластыри", Status.NEW,
                startTime, duration);
        manager.getHistoryManager().addTask(task1); // Добавили задачу в историю
        manager.getHistoryManager().addTask(task2); // Добавили задачу в историю
        Assertions.assertEquals(1, manager.getHistoryManager().getSize(), "В истории недолжно быть одинаковых задач");
    }

    @Test
    void addTaskTestListNotShouldBeEmpty() { // Тестим метод добавляющий задачу в историю
        LocalDateTime startTime = LocalDateTime.of(2022, 04, 01, 12, 0);
        Duration duration = Duration.ofHours(2);
        Task task = new Task("Бросить курить", "Купить никотиновые пластыри", Status.NEW,
                startTime, duration);
        manager.getHistoryManager().addTask(task); // Добавили задачу в историю
        Assertions.assertFalse(manager.getHistoryManager().getHistory().isEmpty(), "История не должна быть пустой");
    }

    @Test
    void removeTestRemoveTheFirstElementListShouldBeContains2Task() { // с. Удаление из истории: начало
        LocalDateTime startTime = LocalDateTime.of(2022, 04, 01, 12, 0);
        Duration duration = Duration.ofHours(2);
        Task task1 = new Task("Бросить курить", "Купить никотиновые пластыри", Status.NEW,
                startTime, duration);
        Task task2 = new Task("Записаться на йогу", "Найти время", Status.NEW,
                startTime, duration);
        Task task3 = new Task("Закончить работу", "Найти время", Status.NEW, startTime, duration);
        manager.getHistoryManager().addTask(task1); // Добавили задачу в историю
        manager.getHistoryManager().addTask(task2); // Добавили задачу в историю
        manager.getHistoryManager().addTask(task3); // Добавили задачу в историю
        manager.getHistoryManager().remove(task1.getId()); // Удалили задачу из истории
        // System.out.println(manager.getHistoryManager().getHistory().);
        Assertions.assertEquals(2, manager.getHistoryManager().getSize(), "История должна быть пустой");
    }

    @Test
    void removeTestRemoveMiddleElementListShouldBeContains2Task() { // с. Удаление из истории: середина
        LocalDateTime startTime = LocalDateTime.of(2022, 04, 01, 12, 0);
        Duration duration = Duration.ofHours(2);
        Task task1 = new Task("Бросить курить", "Купить никотиновые пластыри", Status.NEW,
                startTime, duration);
        Task task2 = new Task("Записаться на йогу", "Найти время", Status.NEW,
                startTime, duration);
        Task task3 = new Task("Закончить работу", "Найти время", Status.NEW, startTime, duration);
        manager.getHistoryManager().addTask(task1); // Добавили задачу в историю
        manager.getHistoryManager().addTask(task2); // Добавили задачу в историю
        manager.getHistoryManager().addTask(task3); // Добавили задачу в историю
        manager.getHistoryManager().remove(task2.getId()); // Удалили задачу из истории
        Assertions.assertEquals(2, manager.getHistoryManager().getSize(), "История должна быть пустой");
    }

    @Test
    void removeTestRemoveTheLastElementListShouldBeContains2Task() { // с. Удаление из истории: конец
        LocalDateTime startTime = LocalDateTime.of(2022, 04, 01, 12, 0);
        Duration duration = Duration.ofHours(2);
        Task task1 = new Task("Бросить курить", "Купить никотиновые пластыри", Status.NEW,
                startTime, duration);
        Task task2 = new Task("Записаться на йогу", "Найти время", Status.NEW,
                startTime, duration);
        Task task3 = new Task("Закончить работу", "Найти время", Status.NEW,
                startTime, duration);
        manager.getHistoryManager().addTask(task1); // Добавили задачу в историю
        manager.getHistoryManager().addTask(task2); // Добавили задачу в историю
        manager.getHistoryManager().addTask(task3); // Добавили задачу в историю
        manager.getHistoryManager().remove(task3.getId()); // Удалили задачу из истории
        Assertions.assertEquals(2, manager.getHistoryManager().getSize(), "История должна быть пустой");
    }

}