import com.google.gson.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.Server.HttpTaskServer;
import tracker.Server.MyAdapterForFileEpicInSubTaskObject;
import tracker.Server.MyAdapterForFileSubTaskInEpicObject;
import tracker.controllers.Managers;
import tracker.controllers.TaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServerTest {
    HttpTaskServer taskServer;  // Поле сервера
    HttpClient client; // Клиент для отправления запроса на сервер
    TaskManager taskManager; // Менеджен для связывания сервера с его обработчиком
    private static final File FILE = new File("SERVER-TEST.csv");  // Поле необходимое для менеджера
    Task task; // Задача
    Epic epic; // Эпик
    Subtask subtask; // Подзадача

    @BeforeEach
    public void launchServer() throws IOException {
        // Создаем задачу, эпик и подзадачу для наших тестов
        taskManager = Managers.getDefaultBackedTaskManager(FILE); // Создали менеджера
        task = new Task("Test1", "Test1", Status.NEW, null, null);
        epic = new Epic("Test2", "Test2", Status.NEW, null, null);
        subtask = new Subtask("Test3", "Test3", Status.NEW, epic,
                LocalDateTime.of(2022, 4, 25, 12, 0), Duration.ofHours(2));
        taskManager.createTask(task); // Создали задачу и записали ее в мапу
        taskManager.createTask(epic); // Создали эпик и записали его в мапу
        taskManager.createTask(subtask); // Создали подзадачу и записали ее в мапу
        taskServer = new HttpTaskServer(taskManager); // создали и запустили сервер
        client = HttpClient.newHttpClient(); // Создали клиента
    }

    @AfterEach
    void deleteFile() throws IOException {
        if (FILE.exists()) {
            Files.deleteIfExists(FILE.toPath());
        }
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubTasks();
        taskManager.deleteAllEpic();
        client = null;
        taskServer.stopTaskServer(); // останавливаем работу сервера, чтоб не получить ошибку, что порт уже занят
    }

    @Test
    // Тестим метод, который возвращает задачи и подзадачи в порядке приоретета
    public void getAllTaskAndSubTaskTest() throws IOException, InterruptedException {
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            // Передаем парсеру тело ответа в виде строки, содержащей данные в формате JSON
            JsonElement jsonElement = JsonParser.parseString(response.body());
            // Преобразуем в JSON-массив
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            // Получаем ID задачи
            int id = jsonArray.get(0).getAsJsonObject().get("id").getAsInt();
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по получению приорететных ЗАДАЧ И ПОДЗАДАЧ" +
                    " возникла ошибка.");
        }
    }

    @Test
    // Тестим метод, который возвращает все задачи
    public void getTaskTest() throws IOException, InterruptedException {
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            // Передаем парсеру тело ответа в виде строки, содержащей данные в формате JSON
            JsonElement jsonElement = JsonParser.parseString(response.body());
            // Преобразуем в JSON-массив
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            // Получаем ID задачи
            int id = jsonArray.get(0).getAsJsonObject().get("id").getAsInt();
            Assertions.assertEquals(1, id, "Клиент получил задачу c ID = 1");
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по получению всех ЗАДАЧ возникла ошибка.");
        }
    }

    @Test
    // Тестим метод, который возвращает все эпики
    public void getEpicTest() throws IOException, InterruptedException {
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            // Передаем парсеру тело ответа в виде строки, содержащей данные в формате JSON
            JsonElement jsonElement = JsonParser.parseString(response.body());
            // Преобразуем в JSON-массив
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            // Получаем ID эпика
            int id = jsonArray.get(0).getAsJsonObject().get("id").getAsInt();
            Assertions.assertEquals(2, id, "Клиент получил задачу c ID = 2");
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по получению всех ЭПИКОВ возникла ошибка.");
        }
    }

    @Test
    // Тестим метод, который возвращает все подзадачи
    public void getSubTaskTest() throws IOException, InterruptedException {
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            // Передаем парсеру тело ответа в виде строки, содержащей данные в формате JSON
            JsonElement jsonElement = JsonParser.parseString(response.body());
            // Преобразуем в JSON-массив
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            // Получаем ID эпика
            int id = jsonArray.get(0).getAsJsonObject().get("id").getAsInt();
            Assertions.assertEquals(3, id, "Клиент получил подзадачу c ID = 3");
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по получению всех ПОДЗАДАЧ возникла ошибка.");
        }
    }

    @Test
    // Тестим метод, который возвращает задачу по ID
    public void getTaskByIdTest() throws IOException, InterruptedException {
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            // Передаем парсеру тело ответа в виде строки, содержащей данные в формате JSON
            JsonElement jsonElement = JsonParser.parseString(response.body());
            // Преобразуем в JSON-массив
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            // Получаем ID задачи
            int id = jsonObject.get("id").getAsInt();
            Assertions.assertEquals(1, id, "Клиент получил задачу c ID = 1");
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по получению ЗАДАЧИ ПО ID возникла ошибка.");
        }
    }

    @Test
    // Тестим метод, который возвращает подзадачу по ID
    public void getSubTaskByIdTest() throws IOException, InterruptedException {
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            // Передаем парсеру тело ответа в виде строки, содержащей данные в формате JSON
            JsonElement jsonElement = JsonParser.parseString(response.body());
            // Преобразуем в JSON-массив
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            // Получаем ID задачи
            int id = jsonObject.get("id").getAsInt();
            Assertions.assertEquals(3, id, "Клиент получил подзадачу c ID = 3");
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по получению ПОДЗАДАЧИ ПО ID возникла ошибка.");
        }
    }

    @Test
    // Тестим метод, который возвращает ЭПИК по ID
    public void getEpicByIdTest() throws IOException, InterruptedException {
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            // Передаем парсеру тело ответа в виде строки, содержащей данные в формате JSON
            JsonElement jsonElement = JsonParser.parseString(response.body());
            // Преобразуем в JSON-массив
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            // Получаем ID задачи
            int id = jsonObject.get("id").getAsInt();
            Assertions.assertEquals(2, id, "Клиент получил эпик c ID = 2");
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по получению ЭПИКА ПО ID возникла ошибка.");
        }
    }

    @Test
    // Тестим метод, который возвращает ИСТОРИЮ
    public void getHistoryTest() throws IOException, InterruptedException {
        // Добавляем задачи в историю вызывая их по ID
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubTaskById(3);
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            // Передаем парсеру тело ответа в виде строки, содержащей данные в формате JSON
            JsonElement jsonElement = JsonParser.parseString(response.body());
            // Преобразуем в JSON-массив
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            // Получаем ID задачи
            int size = jsonArray.size();
            Assertions.assertEquals(3, size, "Клиент получил историю содержащую 3-м элементам");
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по получению ИСТОРИИ возникла ошибка.");
        }
    }

    @Test
    // Тестим метод, удаляющий все задачи
    public void deleteTaskTest() throws IOException, InterruptedException {
        // Добавляем задачи в историю
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            Assertions.assertEquals(0, taskManager.getSaveTask().size(), "Задачи были все удалены");
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по удалению ВСЕХ ЗАДАЧ возникла ошибка.");
        }
    }

    @Test
    // Тестим метод, удаляющий все подзадачи
    public void deleteSubTaskTest() throws IOException, InterruptedException {
        // Добавляем задачи в историю
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            Assertions.assertEquals(0, taskManager.getSaveSubTask().size(), "Подзадачи были " +
                    "все удалены");
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по удалению ВСЕХ ПОДЗАДАЧ возникла ошибка.");
        }
    }

    @Test
    // Тестим метод, удаляющий все эпиики
    public void deleteEpicTest() throws IOException, InterruptedException {
        // Добавляем задачи в историю
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            Assertions.assertEquals(0, taskManager.getSaveSubTask().size(), "Подзадачи были " +
                    "все удалены");
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по удалению ВСЕХ ПОДЗАДАЧ возникла ошибка.");
        }
    }

    @Test
    // Тестим метод, который удаляет ЗАДАЧУ по ID
    public void deleteTaskByIdTest() throws IOException, InterruptedException {
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            Assertions.assertEquals(0, taskManager.getSaveTask().size(), "Задача по ID-1 удалена");
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по удалению ЗАДАЧИ ПО ID возникла ошибка.");
        }
    }

    @Test
    // Тестим метод, который удаляет ПОДЗАДАЧУ по ID
    public void deleteSubTaskByIdTest() throws IOException, InterruptedException {
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            Assertions.assertEquals(0, taskManager.getSaveSubTask().size(), "Подзадача с ID-3 удалена");
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по удалению ПОДЗАДАЧИ ПО ID возникла ошибка.");
        }
    }

    @Test
    // Тестим метод, который удаляет ЭПИК по ID
    public void deleteEpicByIdTest() throws IOException, InterruptedException {
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            Assertions.assertEquals(0, taskManager.getSaveEpic().size(), "Эпик с ID-2 удален");
            Assertions.assertEquals(0, taskManager.getSaveSubTask().size(), "После удаления Эпика" +
                    " нет больше подзадач");
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по удалению ЭПИКА ПО ID возникла ошибка.");
        }
    }

    @Test
    // Тестим метод, который ОБНОВЛЯЕТ ЗАДАЧУ
    public void updateTaskTest() throws Exception {
        //Создаем задачу для обновления
        Task task1 = new Task("UpdateTest1", "UpdateTest1", Status.NEW,
                1, null, null);
        Gson gson = new Gson();
        String json = gson.toJson(task1); // Преобразовали задачу для обновления в Json формат
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .headers("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .PUT(body)
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            Assertions.assertTrue(taskManager.getSaveTask().containsValue(task1), "ЗАДАЧА с ID-1 " +
                    "успешно обновлена");
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по обновлению ЗАДАЧИ С ID-1 возникла ошибка.");
        }
    }

    @Test
    // Тестим метод, который ОБНОВЛЯЕТ ПОДЗАДАЧУ
    public void updateSubTaskTest() throws Exception {
        //Создаем задачу для обновления
        Subtask subtask1 = new Subtask("SUBTASK", "SUBTASK", Status.NEW, epic, 3,
                null, null);
        // ДЛЯ ПОДЗАДАЧ
        Gson gsonForSubTask = new GsonBuilder()
                .serializeNulls() // Отображаем поля, равные Null
                // Устанавливаем правила обработки поля epic в подзадаче
                .registerTypeAdapter(Epic.class, new MyAdapterForFileEpicInSubTaskObject())
                .create(); // Создаем объект
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gsonForSubTask.toJson(subtask1));
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .headers("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .PUT(body)
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            Assertions.assertTrue(taskManager.getSaveSubTask().containsValue(subtask1), "ПОДЗАДАЧА с ID-3 " +
                    "успешно обновлена");
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по обновлению ПОДЗАДАЧИ С ID-3 возникла ошибка.");
        }
    }

    @Test
    // Тестим метод, который ОБНОВЛЯЕТ ЭПИК
    public void updateEpicTest() throws Exception {
        //Создаем задачу для обновления
        Epic epic1 = new Epic("EPIC", "EPIC", Status.NEW, 2, null, null);
        /// ДЛЯ ЭПИКОВ
        Gson gsonForEpic = new GsonBuilder()
                .serializeNulls() // Отображаем поля, равные Null
                // Устанавливаем правила обработки поля epic в подзадаче
                .registerTypeAdapter(Subtask.class, new MyAdapterForFileSubTaskInEpicObject())
                .create(); // Создаем объект
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gsonForEpic.toJson(epic1));
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .headers("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .PUT(body)
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            Assertions.assertTrue(taskManager.getSaveEpic().containsValue(epic1), "ЭПИК с ID-2 " +
                    "успешно обновлен");
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по обновлению ЭПИКА С ID-2 возникла ошибка.");
        }
    }

    @Test
    // Тестим метод, который СОЗДАЕТ ЗАДАЧУ
    public void createTaskTest() throws Exception {
        //Удаляем задачу, так как перед каждым тестом она создается
        taskManager.deleteTaskById(1);
        Gson gson = new Gson();
        String json = gson.toJson(task); // Преобразовали задачу для обновления в Json формат
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .headers("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .POST(body)
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            Assertions.assertNotNull(taskManager.getSaveTask(), "ЗАДАЧА успешно создана");
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по СОЗДАНИЮ ЗАДАЧИ возникла ошибка.");
        }
    }


    @Test
    // Тестим метод, который СОЗДАЕТ ПОДЗАДАЧУ
    public void createSubTaskTest() throws Exception {
        //Удаляем подзадачу, так как перед каждым тестом она создается
        taskManager.deleteAllSubTasks();
        // ДЛЯ ПОДЗАДАЧ
        Gson gsonForSubTask = new GsonBuilder()
                .serializeNulls() // Отображаем поля, равные Null
                // Устанавливаем правила обработки поля epic в подзадаче
                .registerTypeAdapter(Epic.class, new MyAdapterForFileEpicInSubTaskObject())
                .create(); // Создаем объект
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gsonForSubTask.toJson(subtask));
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .headers("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .POST(body)
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            Assertions.assertNotNull(taskManager.getSaveSubTask(), "ПОДЗАДАЧА успешно СОЗДАНА");
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по созданию ПОДЗАДАЧИ возникла ошибка.");
        }
    }

    @Test
    // Тестим метод, который создает ЭПИК
    public void createEpicTest() throws Exception {
        // Удаляем Эпик, потому что он создается перед каждым тестом
        taskManager.deleteEpicById(2);
        /// ДЛЯ ЭПИКОВ
        Gson gsonForEpic = new GsonBuilder()
                .serializeNulls() // Отображаем поля, равные Null
                // Устанавливаем правила обработки поля epic в подзадаче
                .registerTypeAdapter(Subtask.class, new MyAdapterForFileSubTaskInEpicObject())
                .create(); // Создаем объект
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gsonForEpic.toJson(epic));
        // Создаем запрос и отправляем его на наш сервер
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder() // Создал запрос
                .uri(url)
                .headers("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .POST(body)
                .build();

        try {
            // Отправляем запрос на сервер
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояния: " + response.statusCode()); // Статус код ответа сервера
            System.out.println("Ответ: " + response.body());
            Assertions.assertNotNull(taskManager.getSaveEpic(), "ЭПИК УСПЕШНО СОЗДАН");
            Assertions.assertEquals(200, response.statusCode(), "Запрос успешно обработан");
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса по созданию ЭПИКА возникла ошибка.");
        }
    }
}
