import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tracker.controllers.FileBackedTasksManager;
import tracker.controllers.ManagerSaveException;
import tracker.controllers.Managers;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    public static final File FILE = new File("TEST.csv");

    @Override
    void setManager() {
        manager = (FileBackedTasksManager) Managers.getDefaultBackedTaskManager(FILE);
    }

    @AfterEach
    void deleteFile() throws IOException {
        if (FILE.exists()) {
            Files.deleteIfExists(FILE.toPath());
        }
    }

    @Test
    public void saveTestShouldGetMessageWhenTaskIsNull() {
        // a. Пустой список задач
        manager.createTask(null); // Создали и записали задачу в мапу
        ManagerSaveException exception = Assertions.assertThrows(ManagerSaveException.class, () -> manager.save());
        Assertions.assertEquals("НЕТ ДАННЫХ ДЛЯ СОХРАНЕНИЯ В ФАЙЛ", exception.getMessage());
    }

    @Test
    public void saveTestShouldBeNotEmptyFileEpicWithoutSubTask() {
        // b. Эпик без подзадач
        Epic epic = new Epic("Починить холодильник", "Вызвать мастера", Status.NEW, startTime, duration);
        manager.createTask(epic); // Создали и записали эпик в мапу
        manager.save(); // Записываем эпик в файл
        Assertions.assertFalse(FILE.toString().isEmpty(), "Файл не должен быть пустым");
    }

    @Test
    public void saveTestShouldNotEmptyFile() {
        // c. Пустой список истории.
        Task task = new Task("Починить холодильник", "Вызвать мастера", Status.NEW, startTime, duration);
        manager.createTask(task); // Создали и записали задачу в мапу
        manager.getHistoryManager().getHistory().clear(); // Очищаем историю.
        manager.save(); // Записываем задачу в файл
        Assertions.assertFalse(FILE.toString().isEmpty(), "Файл не должен быть пустым");
    }

    @Test
    public void loadFromFileTestShouldGetMessageWhenTaskIsNull() {
        // a. Пустой список задач
        manager.createTask(null); // Создали и записали задачу в мапу
        manager = FileBackedTasksManager.loadFromFile(FILE);
        Assertions.assertNull(manager, "ФАЙЛА БЕЗ ЗАДАЧ НЕ ДОЛЖНО СУЩЕСТВОВАТЬ");
    }

    @Test
    public void loadFromFileTestShouldNotEmptyMapWithEpic() {
        // b. Эпик без подзадач
        Epic epic = new Epic("Починить холодильник", "Вызвать мастера", Status.NEW, startTime, duration);
        manager.createTask(epic); // Создали и записали эпик в мапу
        manager.save(); // Записываем задачу в файл
        manager.getSaveEpic().clear(); // отчищаем мапу, чтоб проверить, что восстановится все из файла
        Assertions.assertTrue(manager.getSaveEpic().isEmpty(), "Мапа отчистилась перед записью из файла");
        manager = FileBackedTasksManager.loadFromFile(FILE); // Выгружаем эпик из файла
        Assertions.assertTrue(manager.getSaveEpic().containsValue(epic), "Задача восстановлена");
    }

    @Test
    public void loadFromFileTestShouldNotEmptyMap() {
        // с. Пустой список истории
        Task task = new Task("Починить холодильник", "Вызвать мастера", Status.NEW, startTime, duration);
        manager.createTask(task); // Создали и записали задачу в мапу
        manager.save(); // Записываем задачу в файл
        manager.getSaveTask().clear(); // отчищаем мапу, чтоб проверить, что восстановится все из файла
        Assertions.assertTrue(manager.getSaveTask().isEmpty(), "Мапа отчистилась перед записью из файла");
        manager = FileBackedTasksManager.loadFromFile(FILE);
        Assertions.assertTrue(manager.getSaveTask().containsValue(task), "Задача восстановлена");
    }
}
