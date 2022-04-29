import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import tracker.controllers.TaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.server.KVServer;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;


public abstract class TaskManagerTest<T extends TaskManager> { // Класс для избежания дублирования кода
    protected T manager;
    LocalDateTime startTime;
    Duration duration;
    KVServer kvServer;

    abstract void setManager();

    @BeforeEach
    public void initialization() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        setManager();
    }

    @BeforeEach
    public void initTime() {
        startTime = LocalDateTime.of(2022, 04, 01, 12, 0);
        duration = Duration.ofHours(2);
    }

    @AfterEach
    void stopKVServer() {
        kvServer.stop();
    }

    @Test
    public void createTaskTestWhenTasksExistsAndDoNotIntersection() { // 1.1. Тестим метод создающий задачу, подзадачу,
        // эпик
        // a. Со стандартным поведением
        /// ОБЫЧНАЯ ЗАДАЧА
        Task task = new Task("Покормить кота", "Насыпать корм в миску", Status.NEW, startTime, duration);
        manager.createTask(task); // Создаем задачу и добавляем в мапу
        Assertions.assertNotNull(manager.getSaveTask(), "Задача успешно создана"); // Проверка
        /// ЭПИЧЕСКАЯ ЗАДАЧА
        Epic epic = new Epic("Помогать ближним", "Найти кому нужна помощь", Status.NEW,
                startTime, duration);
        manager.createTask(epic); // Создаем эпик и добавляем в мапу'
        Assertions.assertNotNull(manager.getSaveEpic(), "Эпик успешно создан"); // Проверка
        /// ПОДЗАДАЧА
        Subtask subtask = new Subtask("Поддержать друга", "Похлопать по плечу", Status.NEW, epic,
                LocalDateTime.of(2022, 05, 01, 12, 0), Duration.ofHours(2));
        manager.createTask(subtask); // Создаем подзадачу и добавляем в мапу'
        Assertions.assertNotNull(manager.getSaveSubTask(), "Подзадача успешно создана"); // Проверка
    }

    @Test
    public void createTaskTestWhenTasksIsNull() { // 1.1. Тестим метод создающий задачу
        // b. С пустым списком задач
        /// ОБЫЧНАЯ ЗАДАЧА
        manager.createTask(null); // Создаем задачу и добавляем в мапу
        Assertions.assertTrue(manager.getSaveTask().isEmpty(), "Задачи нет, передан NULL"); // Проверка

    }

    @Test
    public void createTaskTestWhenTasksExistsAndTasksIntersectionWithEachOther() { // 1.1. Тестим метод создающий задачу
        // Пересечение задач, должно вылетить исключение
        Task task1 = new Task("Завести кота", "Ухаживать за котом", Status.NEW, startTime, duration);
        Task task2 = new Task("Завести кота", "Ухаживать за котом", Status.NEW, startTime, duration);
        manager.createTask(task1);
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> manager.createTask(task2));
        Assertions.assertEquals("Задача пересекается по времени", exception.getMessage(),
                "Вторая задача не будет создана" +
                        "так как пересекается по времени с первой, вылетит исключение");
    }

    @Test
    public void getListOfAllTasksTestShouldBeNoEmptyListWithTask() { // 2.1 Тестим метод для получения списка задач
        // a. Со стандартным поведением
        /// ОБЫЧНАЯ ЗАДАЧА
        Task task = new Task("Покормить кота", "Насыпать корм в миску", Status.NEW, startTime, duration);
        manager.createTask(task); // Создали задачу
        Assertions.assertFalse(manager.getListOfAllTasks().isEmpty(), "Получаем не пустой список с задачами");
        Assertions.assertEquals(1, manager.getListOfAllTasks().size()); // Проверяем размер списка
    }

    @Test
    public void getListOfAllSubTaskTestShouldBeNoEmptyListWithSubTask() { // 2.1 Тестим метод для получения списка
        // подзадач
        // a. Со стандартным поведением
        /// ПОДЗАДАЧА
        Subtask subtask = new Subtask("Покормить кота", "Насыпать корм в миску", Status.NEW,
                new Epic("Позаботится о животных", "Найти нуждающихся", Status.NEW,
                        startTime, duration), startTime, duration);
        manager.createTask(subtask); // Создали подзадачу
        Assertions.assertFalse(manager.getListOfAllSubTasks().isEmpty(), "Получаем не пустой список " +
                "с подзадачами");
        Assertions.assertEquals(1, manager.getListOfAllSubTasks().size()); // Проверяем размер списка
        Assertions.assertEquals(subtask, manager.getListOfAllSubTasks().get(0)); // Проверяем элемент списка
    }

    @Test
    public void getListOfAllEpicTestShouldBeNoEmptyListWithEpic() { // 2.1 Тестим метод для получения списка эпиков
        // a. Со стандартным поведением
        /// ЭПИЧЕСКАЯ ЗАДАЧА
        Epic epic = new Epic("Покормить кота", "Насыпать корм в миску", Status.NEW, startTime, duration);
        manager.createTask(epic); // Создали эпик
        Assertions.assertFalse(manager.getListOfAllEpic().isEmpty(), "Получаем не пустой список с Эпиком");
        Assertions.assertEquals(1, manager.getListOfAllEpic().size()); // Проверяем размер списка
        Assertions.assertEquals(epic, manager.getListOfAllEpic().get(0)); // Проверяем элемент списка
    }

    @Test
    public void deleteAllTasksTestShouldBeEmptyListsMapHistory() { // 2.2 Тестим метод для удаления всех задач
        // a. Со стандартным поведением
        /// ОБЫЧНАЯ ЗАДАЧА
        Task task = new Task("Покормить кота", "Насыпать корм в миску", Status.NEW, startTime, duration);
        // задача
        manager.createTask(task); // Создали задачу, поместили ее в мапу
        manager.getTaskById(1); // Метод, который запишет задачу в историю, потому что при создании задачи,
        // она сразу не помещается в историю просмотров
        manager.deleteAllTasks(); // Удаляем все задачи и смотрим, что список с задачами, мапа с задачами путстые
        // и история больше не содержит задач
        Assertions.assertTrue(manager.getListOfAllTasks().isEmpty(), "Список задач должен быть пустым");
        Assertions.assertTrue(manager.getSaveTask().isEmpty(), "Мапа с задачами должна быть пустая");
        Assertions.assertEquals(0, manager.getHistoryManager().getSize(), "История с задачами " +
                "должна быть пустая");
    }

    @Test
    public void deleteAllEpicTestShouldBeEmptyListsMapHistory() { // 2.2 Тестим метод для удаления всех задач
        // a. Со стандартным поведением
        /// ЭПИЧЕСКАЯ ЗАДАЧА
        Epic epic = new Epic("Покормить кота", "Насыпать корм в миску", Status.NEW, startTime, duration);
        // Эпик
        manager.createTask(epic); // Создали эпик, поместили его в мапу
        manager.getEpicById(1); // Метод, который запишет эпик в историю, потому что при создании эпика,
        // она сразу не помещается в историю просмотров
        manager.deleteAllEpic(); // Удаляем все эпики и смотрим, что список с задачами, мапа с задачами пустые
        // и история больше не содержит эпиков
        Assertions.assertTrue(manager.getListOfAllEpic().isEmpty(), "Список с эпиками должен быть пустым");
        Assertions.assertTrue(manager.getSaveEpic().isEmpty(), "Мапа с эпиками должна быть пустая");
        Assertions.assertEquals(0, manager.getHistoryManager().getSize(), "История задач должна " +
                "быть пустая");
    }

    @Test
    public void deleteAllSubTaskTestShouldBeEmptyListsMapHistoryHaveToOnly1Epic() { // 2.2 Тестим метод для удаления
        // всех задач
        // a. Со стандартным поведением
        /// ПОДЗАДАЧА
        Epic epic2 = new Epic("Генеральная уборка", "Помыть все помещения", Status.NEW,
                startTime, duration);
        Subtask subtask = new Subtask("Помыть гостинную", "приобрести моющие средства",
                Status.NEW, epic2, startTime, duration); // Подзадача
        manager.createTask(epic2); // Создали эпик, поместили ее в мапу
        manager.createTask(subtask); // Создали подзадачу, поместили ее в мапу
        manager.getEpicById(1);
        manager.getSubTaskById(2); // Метод, который запишет подзадача в историю, потому что при создании подзадачи,
        // она сразу не помещается в историю просмотров
        manager.deleteAllSubTasks(); // Удаляем все подзадачи и смотрим, что список с подзадачами, мапа с подзадачами
        // пустые и история больше не содержит подзадач, и список с историей должен содержать только эпик
        Assertions.assertTrue(manager.getListOfAllSubTasks().isEmpty(), "Список с подзадачами " +
                "должен быть пустым");
        Assertions.assertTrue(manager.getSaveSubTask().isEmpty(), "Мапа с подзадачами должна быть пустая");
        Assertions.assertEquals(1, manager.getHistoryManager().getSize(), "Длина списка истории " +
                "должна быть 1");
    }


    @Test
    public void getTaskByIdTestShouldBeNotNUllWhenDemandTaskWithId1() { // 2.3 Тестим метод для получения задачи по ID
        // a. Со стандартным поведением
        /// ЗАДАЧА
        Task task = new Task("Покормить кота", "Насыпать корм в миску", Status.NEW,
                startTime, duration); // задача
        manager.createTask(task); // Создали задачу, поместили ее в мапу
        Assertions.assertNotNull(manager.getTaskById(1), "Задача возвращена по ID");
    }

    @Test
    public void getTaskByIdTestShouldGetMessageAboutMistakeWhenGetNotCorrectTaskID() { // 2.3 Тестим метод для
        // получения задачи по ID
        // с. Неверный индетификатор задачи
        /// ЗАДАЧА
        Task task = new Task("Покормить кота", "Насыпать корм в миску", Status.NEW,
                startTime, duration); // задача
        manager.createTask(task); // Создали задачу, поместили ее в мапу
        NullPointerException exception = Assertions.assertThrows(NullPointerException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        manager.getTaskById(0); // Пробуем получить задачу по несуществующему ID
                    }
                });
        Assertions.assertEquals("Задачи с данным ID не существует", exception.getMessage());
    }

    @Test
    public void getEpicByIdTestShouldBeNotNUllWhenDemandEpicWithId1() { // 2.3 Тестим метод для получения эпика по ID
        // a. Со стандартным поведением
        /// ЭПИК
        Epic epic = new Epic("Покормить кота", "Насыпать корм в миску", Status.NEW,
                startTime, duration); // Эпик
        manager.createTask(epic); // Создали эпик, поместили его в мапу
        Assertions.assertEquals(1, manager.getEpicById(1).getId(), "Вернули эпик с ID-1");
    }

    @Test
    public void ShouldGetMessageAboutMistakeWhenGetNotCorrectEpicID() { // 2.3 Тестим метод для получения эпика по ID
        // с. Неверный индетификатор эпика
        /// ЭПИК
        Epic epic = new Epic("Покормить кота", "Насыпать корм в миску", Status.NEW,
                startTime, duration); // Эпик
        manager.createTask(epic); // Создали эпик, поместили его в мапу
        NullPointerException exception = Assertions.assertThrows(NullPointerException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        manager.getEpicById(0); // Пробуем получить эпик по несуществующему ID
                    }
                });
        Assertions.assertEquals("Эпика с данным ID не существует", exception.getMessage());
    }

    @Test
    public void getSubTaskByIdTestShouldBeNotNUllWhenDemandSubTaskWithId1() { // 2.3 Тестим метод для
        // получения подзадачи по ID
        // a. Со стандартным поведением
        /// ПОДЗАДАЧА
        Subtask subtask = new Subtask("Покормить кота", "Насыпать корм в миску", Status.NEW,
                new Epic("Test", "Test", Status.NEW, startTime, duration), startTime, duration); // Эпик
        manager.createTask(subtask); // Создали подзадачу, поместили ее в мапу
        Assertions.assertEquals(1, manager.getSubTaskById(1).getId(), "Вернули эпик с ID-1");
    }

    @Test
    public void ShouldGetMessageAboutMistakeWhenGetNotCorrectSubTaskID() { // 2.3 Тестим метод для
        // получения подзадачи по ID
        // с. Неверный индетификатор подзадачи
        /// ПОДЗАДАЧА
        Subtask subtask = new Subtask("Покормить кота", "Насыпать корм в миску", Status.NEW,
                new Epic("Test", "Test", Status.NEW, startTime, duration), startTime, duration);
        // Подзадача
        manager.createTask(subtask); // Создали эпик, поместили его в мапу
        NullPointerException exception = Assertions.assertThrows(NullPointerException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        manager.getSubTaskById(0); // Пробуем получить подзадачу по несуществующему ID
                    }
                });
        Assertions.assertEquals("Подзадачи с данным ID не существует", exception.getMessage());
    }

    @Test
    public void UpdateTaskShouldBeNewTaskInMapAndListPreviousTaskHaveToDeleteFromHistory() throws Exception {
        // 2.5 Тестим метод Обновление задачи по идентификатору
        // a. Со стандартным поведением
        Task task1 = new Task("Покормить кота", "Насыпать корм в миску", Status.NEW,
                startTime, duration); // задача
        Task task2 = new Task("Купить кролика", "Сходить на рынок", Status.NEW, 1,
                LocalDateTime.of(2023, 12, 1, 12, 0), Duration.ofHours(2)); // задача
        manager.createTask(task1); // Создали задачу, записали ее в мапу
        manager.getTaskById(1); // Добавили задачу в историю
        manager.updateTask(task2); // Обновляем задачу
        Assertions.assertTrue(manager.getSaveTask().containsValue(task2), "Задача должна быть " +
                "обновлена в мапе");
        Assertions.assertTrue(manager.getListOfAllTasks().contains(task2), "Задача должна быть " +
                "обновлена в списке");
        Assertions.assertEquals(0, manager.getHistoryManager().getSize(), "Предыдущая задача " +
                "должна быть удалена из истории");
    }

    @Test
    public void UpdateTaskWhenProvideNotCorrectIDAndNotIntersection() { // 2.5 Тестим метод Обновление задачи
        // по идентификатору
        // с. Неверный индетификатор подзадачи
        Task task1 = new Task("Покормить кота", "Насыпать корм в миску", Status.NEW,
                startTime, duration); // задача
        Task task2 = new Task("Купить кролика", "Сходить на рынок", Status.NEW, 0,
                LocalDateTime.of(2022, 02, 12, 12, 0), Duration.ofHours(2)); // задача
        manager.createTask(task1); // Создали задачу, записали ее в мапу
        Exception exception = Assertions.assertThrows(Exception.class, () -> manager.updateTask(task2));
        Assertions.assertEquals("Задача для обновления не найдена", exception.getMessage(), "Задача " +
                "не обновлена");
    }

    @Test
    public void UpdateTaskTestWhenTasksIntersect() { // 2.5 Тестим метод Обновление задачи по идентификатору
        // когда задача уже пересекается с другой из списка
        Task task1 = new Task("Покормить кота", "Насыпать корм в миску", Status.NEW,
                startTime, duration); // задача
        Task task2 = new Task("Купить кролика", "Сходить на рынок", Status.NEW,
                LocalDateTime.of(2022, 04, 01, 14, 0), Duration.ofHours(2)); // задача
        manager.createTask(task1); // Создали задачу, записали ее в мапу
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> manager.updateTask(task2));
        Assertions.assertEquals("Задача пересекается по времени", exception.getMessage(),
                "Задачи не должны пересекаться по времени");
    }

    @Test
    public void UpdateSubTaskShouldBeNewSubTaskInMapAndListPreviousSubTaskHaveToDeleteFromHistory() throws Exception {
        // 2.5 Тестим метод Обновление подзадачи по идентификатору
        // a. Со стандартным поведением, когда подзадачи не пересекаются
        Epic epic = new Epic("Найти кота", "Сходить на рынок", Status.IN_PROGRESS, startTime, duration);
        manager.createTask(epic);
        Subtask subtask1 = new Subtask("Покормить кота", "Насыпать корм в миску", Status.IN_PROGRESS,
                epic, startTime, duration); // подзадача
        Subtask subtask2 = new Subtask("Купить кролика", "Сходить на рынок", Status.DONE,
                epic, 2, LocalDateTime.of(2023, 01, 12, 12, 12), duration);
        // подзадача
        manager.createTask(subtask1); // Создали подзадачу, записали ее в мапу
        manager.getSubTaskById(2); // Добавили подзадачу в историю
        manager.updateSubTask(subtask2); // Обновляем подзадачу
        Assertions.assertTrue(manager.getSaveSubTask().containsValue(subtask2), "Подзадача должна быть " +
                "обновлена в мапе");
        Assertions.assertTrue(manager.getListOfAllSubTasks().contains(subtask2), "Подзадача должна быть " +
                "обновлена в списке");
        Assertions.assertEquals(0, manager.getHistoryManager().getSize(), "Предыдущая подзадача " +
                "должна быть удалена из истории");
        Assertions.assertEquals(Status.DONE, epic.getStatus(), "Статус должен изменится на DONE после " +
                "обновления подзадачи");
    }

    @Test
    public void UpdateSubTaskWhenProvideNotCorrectID() { // 2.5 Тестим метод Обновление подзадачи по идентификатору
        // с. Неверный индетификатор подзадачи
        Epic epic = new Epic("Найти кота", "Сходить на рынок", Status.IN_PROGRESS, startTime, duration);
        manager.createTask(epic);
        Subtask subtask1 = new Subtask("Покормить кота", "Насыпать корм в миску", Status.IN_PROGRESS,
                epic, startTime, duration); // подзадача
        Subtask subtask2 = new Subtask("Купить кролика", "Сходить на рынок", Status.DONE,
                epic, 0, LocalDateTime.of(2023, 11, 1, 9, 0), Duration.ofHours(3));
        // подзадача
        manager.createTask(subtask1); // Создали подзадачу, записали ее в мапу
        Exception exception = Assertions.assertThrows(Exception.class, () -> manager.updateSubTask(subtask2));
        Assertions.assertEquals("Подзадача для обновления не найдена",
                exception.getMessage(), "Подзадача не обновлена");
    }

    @Test
    public void UpdateEpicShouldBeNewEpicInMapAndListPreviousEpicHaveToDeleteFromHistory() throws Exception {
        // 2.5 Тестим метод Обновление эпика по идентификатору
        // a. Со стандартным поведением
        Epic epic1 = new Epic("Покормить кота", "Насыпать корм в миску", Status.NEW,
                startTime, duration); // эпик
        Epic epic2 = new Epic("Купить кролика", "Сходить на рынок", Status.NEW, 1,
                startTime, duration); // эпик
        manager.createTask(epic1); // Создали эпик, записали его в мапу
        manager.getEpicById(1); // Добавили эпик в историю
        manager.updateEpic(epic2); // Обновляем эпик
        Assertions.assertTrue(manager.getSaveEpic().containsValue(epic2), "Эпик должна быть " +
                "обновлена в мапе");
        Assertions.assertTrue(manager.getListOfAllEpic().contains(epic2), "Эпик должна быть " +
                "обновлена в списке");
        Assertions.assertEquals(0, manager.getHistoryManager().getSize(), "Предыдущий эпик должн быть" +
                " удален из истории");
    }

    @Test
    public void UpdateEpicWhenProvideNotCorrectID() { // 2.5 Тестим метод Обновление задачи по идентификатору
        // с. Неверный индетификатор подзадачи
        Epic epic1 = new Epic("Покормить кота", "Насыпать корм в миску", Status.NEW,
                startTime, duration); // эпик
        Epic epic2 = new Epic("Купить кролика", "Сходить на рынок", Status.NEW,
                startTime, duration); // эпик
        manager.createTask(epic1); // Создали задачу, записали ее в мапу
        Exception exception = Assertions.assertThrows(Exception.class, () -> manager.updateEpic(epic2));
        Assertions.assertEquals("Эпик для обновления не найден", exception.getMessage(), "Эпик " +
                "не обновлен");
    }

    @Test
    public void DeleteTaskByIDTestWithCorrectID() { // 2.6 Удаление задачи по идентификатору
        // a. Со стандартным поведением
        Task task1 = new Task("Покормить кота", "Насыпать корм в миску", Status.NEW,
                startTime, duration); // задача
        manager.createTask(task1); // Создали задачу, записали ее в мапу
        manager.getTaskById(1); // Добавили задачу в историю
        manager.deleteTaskById(1); // Удаляем задачу
        Assertions.assertFalse(manager.getSaveTask().containsValue(task1), "Задача должна быть удалена в мапе");
        Assertions.assertFalse(manager.getListOfAllTasks().contains(task1), "Задача должна быть " +
                "удалена из списка");
        Assertions.assertEquals(0, manager.getHistoryManager().getSize(), "Задача должна быть " +
                "удалена из истории");
    }

    @Test
    public void DeleteTaskByIDWhenProvideNotCorrectID() { // // 2.6 Удаление задачи по идентификатору
        // с. Неверный индетификатор подзадачи
        Task task1 = new Task("Покормить кота", "Насыпать корм в миску", Status.NEW,
                startTime, duration); // задача
        manager.createTask(task1); // Создали задачу, записали ее в мапу
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> manager.deleteTaskById(0));
        Assertions.assertEquals("Задача для удаления по данному ID не была найдена",
                exception.getMessage(), "Задача не должна быть удалена");
    }

    @Test
    public void DeleteSubTaskByIDTestWithCorrectID() { // 2.6 Удаление подзадачи по идентификатору
        // a. Со стандартным поведением
        Epic epic = new Epic("Найти кота", "Сходить на рынок", Status.NEW, startTime, duration);
        manager.createTask(epic); // Эпик, без подзадачи нельзя создать подзадачу
        Subtask subtask1 = new Subtask("Покормить кота", "Насыпать корм в миску", Status.IN_PROGRESS,
                epic, startTime, duration); // подзадача
        manager.createTask(subtask1); // Создали подзадачу, записали ее в мапу
        manager.getSubTaskById(2); // Добавили подзадачу в историю
        manager.deleteSubTaskById(2); // Удаляем подзадачу
        Assertions.assertFalse(manager.getSaveSubTask().containsValue(subtask1), "Подзадача должна быть " +
                "удалена из мапы");
        Assertions.assertFalse(manager.getListOfAllSubTasks().contains(subtask1), "Подзадача должна быть " +
                "удалена из списка");
        Assertions.assertEquals(0, manager.getHistoryManager().getSize(), "Подзадача должна быть " +
                "удалена из истории");
        Assertions.assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен поменятся на NEW");
    }

    @Test
    public void DeleteSubTaskByIDWhenProvideNotCorrectID() { // 2.6 Удаление подзадачи по идентификатору
        // с. Неверный индетификатор подзадачи
        Epic epic = new Epic("Найти кота", "Сходить на рынок", Status.IN_PROGRESS, startTime, duration);
        manager.createTask(epic);
        Subtask subtask1 = new Subtask("Купить кролика", "Сходить на рынок", Status.DONE,
                epic, startTime, duration); // подзадача
        manager.createTask(subtask1); // Создали подзадачу, записали ее в мапу
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> manager.deleteSubTaskById(0));
        Assertions.assertEquals("Подзадача для удаления по данному ID не была найдена",
                exception.getMessage(), "Подзадача не удалена");
    }

    @Test
    public void DeleteEpicByIDTestWithCorrectID() throws Exception { // 2.6 Удаление эпика по идентификатору
        // a. Со стандартным поведением
        Epic epic = new Epic("Покормить кота", "Насыпать корм в миску", Status.NEW,
                startTime, duration); // эпик
        Subtask subtask = new Subtask("Купить кролика", "Сходить на рынок", Status.NEW,
                epic, startTime, duration); // подзадача
        manager.createTask(epic); // Создали эпик, записали его в мапу
        manager.createTask(subtask);
        manager.getEpicById(1); // Добавили эпик в историю
        manager.deleteEpicById(1); // Обновляем эпик
        Assertions.assertFalse(manager.getSaveEpic().containsValue(epic), "Эпик должен быть удален из мапы");
        Assertions.assertFalse(manager.getListOfAllEpic().contains(epic), "Эпик должна быть удален из списка");
        Assertions.assertEquals(0, manager.getHistoryManager().getSize(), "Эпик должен быть " +
                "удален из истории");
        Assertions.assertTrue(manager.getSaveSubTask().isEmpty(), "Мапа с подзадачами должна быть пустой");
        Assertions.assertTrue(manager.getListOfAllSubTasks().isEmpty(), "Список с подзадачами должен быть" +
                " пустой");
    }

    @Test
    public void DeleteEpicByIDWhenProvideNotCorrectID() { // 2.6 Удаление эпика по идентификатору
        // с. Неверный индетификатор подзадачи
        Epic epic1 = new Epic("Покормить кота", "Насыпать корм в миску", Status.NEW,
                startTime, duration); // эпик
        manager.createTask(epic1); // Создали задачу, записали ее в мапу
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> manager.deleteEpicById(0));
        Assertions.assertEquals("Эпик для удаления по данному ID не был найден",
                exception.getMessage(), "Эпик не удален");
    }
}
