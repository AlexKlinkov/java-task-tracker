import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {

    LocalDateTime startTime;
    Duration duration;

    @BeforeEach
    public void init () {
        startTime = LocalDateTime.of(2022, 04, 01, 12, 0);
        duration = Duration.ofHours(2);
    }

    /// 1. Для расчета статуса эпика
    @Test
    public void shouldBeStatusNewIfListWithSubtasksIsEmpty() { // а. Пустой список подзадач
        Epic epic = new Epic("Построить дом", "Присмотреть участок", Status.NEW,
                startTime,
                duration);
        epic.setStatusForEpic(); // Вызываем метод, который меняет статус эпика
        assertEquals(Status.NEW, epic.getStatus()); // Проверяем
    }

    @Test
    public void shouldBeStatusNewIfAllSubtaskWithStatusNew() { // b. Все подзадачи со статусом NEW
        Epic epic = new Epic("Построить дом", "Присмотреть участок", Status.NEW, startTime, duration);
        Subtask subtask1 = new Subtask("Найти мастера", "Провести несколько встреч", Status.NEW, epic,
                startTime, duration);
        Subtask subtask2 = new Subtask("Купить кота", "Провести несколько встреч", Status.NEW, epic,
                startTime, duration);
        epic.getListWithAllSubTasks().add(subtask1); // Добавляем подзадачу в список со всеми подзадачами эпика
        epic.getListWithAllSubTasks().add(subtask2); // Добавляем подзадачу в список со всеми подзадачами эпика
        epic.setStatusForEpic(); // Вызываем метод, который меняет статус эпика
        assertEquals(Status.NEW, epic.getStatus()); // Проверяем
    }

    @Test
    public void shouldBeStatusDoneIfAllSubtaskWithStatusDone() { // c. Все подзадачи со статусом DONE
        Epic epic = new Epic("Построить дом", "Присмотреть участок", Status.NEW,
                startTime, duration);
        Subtask subtask1 = new Subtask("Найти мастера", "Провести несколько встреч", Status.DONE, epic,
                startTime, duration);
        Subtask subtask2 = new Subtask("Купить кота", "Провести несколько встреч", Status.DONE, epic,
                startTime, duration);
        epic.getListWithAllSubTasks().add(subtask1); // Добавляем подзадачу в список со всеми подзадачами эпика
        epic.getListWithAllSubTasks().add(subtask2); // Добавляем подзадачу в список со всеми подзадачами эпика
        epic.setStatusForEpic(); // Вызываем метод, который меняет статус эпика
        assertEquals(Status.DONE, epic.getStatus()); // Проверяем
    }

    @Test
    public void shouldBeStatusInProgressIfSubtaskWithStatusNewAndDone() { // d. Подзадачи со статусами NEW и DONE
        Epic epic = new Epic("Построить дом", "Присмотреть участок", Status.NEW,
                startTime, duration);
        Subtask subtask1 = new Subtask("Найти мастера", "Провести несколько встреч", Status.NEW, epic,
                startTime, duration);
        Subtask subtask2 = new Subtask("Купить кота", "Провести несколько встреч", Status.DONE, epic,
                startTime, duration);
        epic.getListWithAllSubTasks().add(subtask1); // Добавляем подзадачу в список со всеми подзадачами эпика
        epic.getListWithAllSubTasks().add(subtask2); // Добавляем подзадачу в список со всеми подзадачами эпика
        epic.setStatusForEpic(); // Вызываем метод, который меняет статус эпика
        assertEquals(Status.IN_PROGRESS, epic.getStatus()); // Проверяем
    }

    @Test
    public void shouldBeStatusInProgressIfAllSubtaskNotHaveStatusNewOrDone() { // e. Подзадачи со статусом IN_PROGRESS
        Epic epic = new Epic("Построить дом", "Присмотреть участок", Status.NEW,
                startTime, duration);
        Subtask subtask1 = new Subtask("Найти мастера", "Провести несколько встреч", Status.IN_PROGRESS,
                epic,
                startTime, duration);
        Subtask subtask2 = new Subtask("Купить кота", "Провести несколько встреч", Status.DONE, epic,
                startTime, duration);
        Subtask subtask3 = new Subtask("Построить платину", "Найти бобра", Status.NEW, epic,
                startTime, duration);
        epic.getListWithAllSubTasks().add(subtask1); // Добавляем подзадачу в список со всеми подзадачами эпика
        epic.getListWithAllSubTasks().add(subtask2); // Добавляем подзадачу в список со всеми подзадачами эпика
        epic.getListWithAllSubTasks().add(subtask3); // Добавляем подзадачу в список со всеми подзадачами эпика
        epic.setStatusForEpic(); // Вызываем метод, который меняет статус эпика
        assertEquals(Status.IN_PROGRESS, epic.getStatus()); // Проверяем
    }

    @Test
    public void setTimeForEpicTest() { // Тестим метод устанавливающий время эпика на основе его подзадач
        Epic epic = new Epic("Улететь в париж", "Купить билет", Status.NEW, null, null);
        Subtask subtask1 = new Subtask("Найти время", "Взять отгул", Status.NEW, epic,
                LocalDateTime.of(2022, 2, 1, 12, 0), Duration.ofHours(2));
        Subtask subtask2 = new Subtask("Собрать чемодан", "Купить чемодан", Status.NEW, epic,
                LocalDateTime.of(2022, 1, 1, 12, 0), Duration.ofHours(2));
        epic.getListWithAllSubTasks().add(subtask1);
        epic.getListWithAllSubTasks().add(subtask2);
        epic.setTimeForEpic(); // Устанавливаем время для эпика на основе подзадач
        Assertions.assertEquals(LocalDateTime.of(2022, 1, 1, 12, 0),
                epic.getStartTime(), "Время начала эпика, это время самой ранней подзадачи");
        Assertions.assertEquals(LocalDateTime.of(2022, 2, 1, 14, 0),
                epic.getStartTime().plus(epic.getDuration()), "Время завершения эпика, это " +
                        "завершение его самой последней подзадачи");
        Assertions.assertEquals(Duration.ofHours(746),
                epic.getDuration(), "Продолжительность эпика это время от старта самой ранней подзадачи, " +
                        "до окончания самой поздней подзадачи");
    }
}