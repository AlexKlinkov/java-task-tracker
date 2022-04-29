import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tracker.controllers.*;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class InMemoryTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    void setManager() {
        manager = (InMemoryTaskManager) Managers.getDefault();
    }

    @Test
    public void getPrioritizedTasksTest() { // Тестим метод возвращающий список задач и подзадач в заданном порядке.
        Task task1 = new Task("Test1", "Test1", Status.NEW, null, null);
        Epic epic = new Epic("Epic", "Epic", Status.NEW, null, null);
        Subtask subtask1 = new Subtask("SubTask1", "SubTask1", Status.NEW, epic,
                LocalDateTime.of(2022, 02, 01, 12, 0), Duration.ofHours(2));
        Subtask subtask2 = new Subtask("SubTask2", "SubTask2", Status.NEW, epic,
                LocalDateTime.of(2022, 01, 01, 12, 0), Duration.ofHours(2));
        manager.createTask(task1);
        manager.createTask(epic);
        manager.createTask(subtask1);
        manager.createTask(subtask2);
        Assertions.assertEquals(subtask2, manager.getPrioritizedTasks().get(0),
                "Самой первой в списке будет подзадача № 2");
        Assertions.assertEquals(task1, manager.getPrioritizedTasks().get(2),
                "Последний элемент будет с нулевым значением времени");
    }
}
