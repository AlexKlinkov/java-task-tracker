package tracker.Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.controllers.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;


public class MyHandler implements HttpHandler {
    TaskManager taskManager;

    public MyHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Объект класса содержащий все требуемые типы Json
        MyJsonForDifferentTypeOfTasks gson = new MyJsonForDifferentTypeOfTasks();
        ////
        String method = exchange.getRequestMethod(); // Узнаем метод которым стучатся на сервер
        String[] path = exchange.getRequestURI().getPath().split("/"); // Парсим путь для получения каталога
        String[] pathForId = null; // Массив для названий и критериев параметров
        if (exchange.getRequestURI().getQuery() != null) { // Если у запроса есть параметры
            pathForId = exchange.getRequestURI().getQuery().split("="); // парсим путь, параметры запроса
        }
        // boolean isRequestWithParameter = false; // Переменная для определения с параметром запрос или нет
        /// Метод возвращающий все задачи и подзадачи в приорететном порядке
        if (method.equals("GET") && path[path.length - 1].equals("tasks")) {
            String response = gson.gsonForSubTask.toJson(taskManager.getPrioritizedTasks()); // Преобразовываем значения
            // списка в Json
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа сервера,
            // Вывожу тело запроса (задачи, подзадачи) в консоль в формате Json
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
            /// Метод возвращающий все задачи
        } else if (method.equals("GET") && path[path.length - 1].equals("task") && pathForId == null) {
            // Преобразовываем значения мапы в Json
            String response = gson.gsonForTask.toJson(taskManager.getSaveTask().values());
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
            /// Метод возвращающий все эпики
        } else if (method.equals("GET") && path[path.length - 1].equals("epic") && pathForId == null) {
            // Преобразовываем значения мапы в Json
            String response = gson.gsonForEpic.toJson(taskManager.getSaveEpic().values());
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
            /// Метод возвращающий все подзадачи
        } else if (method.equals("GET") && path[path.length - 1].equals("subtask") && pathForId == null) {
            // Преобразовываем значения мапы в Json
            String response = gson.gsonForSubTask.toJson(taskManager.getSaveSubTask().values());
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
            /// Метод возвращающий задачу по ID
        } else if (method.equals("GET") && path[path.length - 1].equals("task") && pathForId != null) {
            // Преобразовываем значения мапы в Json
            String response = gson.gsonForTask.toJson(taskManager.getTaskById(Integer.parseInt(pathForId[1])));
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
            /// Метод возвращающий эпик по ID
        } else if (method.equals("GET") && path[path.length - 1].equals("epic") && pathForId != null) {
            // Преобразовываем значения мапы в Json
            String response = gson.gsonForEpic.toJson(taskManager.getEpicById(Integer.parseInt(pathForId[1])));
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
            /// Метод возвращающий подзадачу по ID
        } else if (method.equals("GET") && path[path.length - 1].equals("subtask") && pathForId != null) {
            // Преобразовываем значения мапы в Json
            String response = gson.gsonForSubTask.toJson(taskManager.getSubTaskById(Integer.parseInt(pathForId[1])));
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
            // Метод возвращающий историю
        } else if (method.equals("GET") && path[path.length - 1].equals("history")) {
            // Для разных типов объектов, своя обработка в Json формат, так как в истории хранятся все типы задач
            String response = ""; // Собирательная строка для всех видом задач
            Set<String> stringForResponse = new LinkedHashSet<>();
            for (Task task : taskManager.getHistoryManager().getHistory()) {
                if (String.valueOf(task.getTYPE()).equals("EPIC")) {
                    String responseEpic = String.join("-----", gson.gsonForEpic.toJson(task));
                    stringForResponse.add(responseEpic);
                } else if (String.valueOf(task.getTYPE()).equals("SUBTASK")) {
                    String responseSubTask = String.join("-----", gson.gsonForSubTask.toJson(task));
                    stringForResponse.add(responseSubTask);
                } else if (String.valueOf(task.getTYPE()).equals("TASK")) {
                    String responseTask = String.join("-----", gson.gsonForTask.toJson(task));
                    stringForResponse.add(responseTask);
                }
            }
            int count = 0;
            for (String line : stringForResponse) {
                line = line + ",\n";
                response = response + line;
                count += 1;
                if (count == stringForResponse.size()) {
                    response = response.substring(0, response.length() - 2); // Удаляем последний символ строки,
                    // ненужную запятую
                }
            }
            response = "[\n" + response + "]";
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
            // Метод удаляющий все задачи
        } else if (method.equals("DELETE") && path[path.length - 1].equals("task") && pathForId == null) {
            taskManager.deleteAllTasks(); // Удаляем все задачи
            String response = gson.gsonForTask.toJson("Все задачи по адресу " + exchange.getRequestURI().getPath() +
                    " успешно удалены");
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
            // Метод удаляющий все подзадачи
        } else if (method.equals("DELETE") && path[path.length - 1].equals("subtask") && pathForId == null) {
            taskManager.deleteAllSubTasks(); // Удаляем все задачи
            String response = gson.gsonForSubTask.toJson("Все подзадачи по адресу " +
                    exchange.getRequestURI().getPath() + " успешно удалены");
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
            // Метод удаляющий все эпики
        } else if (method.equals("DELETE") && path[path.length - 1].equals("epic") && pathForId == null) {
            taskManager.deleteAllEpic(); // Удаляем все задачи
            String response = gson.gsonForEpic.toJson("Все эпики по адресу " + exchange.getRequestURI().getPath() +
                    " успешно удалены");
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
            // Метод удаляющий задачу по ID
        } else if (method.equals("DELETE") && path[path.length - 1].equals("task") && pathForId != null) {
            taskManager.deleteTaskById(Integer.parseInt(pathForId[1])); // Удаляем задачу по ID
            // Преобразовываем значения мапы в Json
            String response = gson.gsonForTask.toJson("Задача по ID № " +
                    Integer.parseInt(pathForId[1]) + " была удалена");
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
            // Метод удаляющий подзадачу по ID
        } else if (method.equals("DELETE") && path[path.length - 1].equals("subtask") && pathForId != null) {
            taskManager.deleteSubTaskById(Integer.parseInt(pathForId[1])); // Удаляем подзадачу по ID
            // Преобразовываем значения мапы в Json
            String response = gson.gsonForSubTask.toJson("Подзадача по ID № " +
                    Integer.parseInt(pathForId[1]) + " была удалена");
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
            // Метод удаляющий Эпик по ID
        } else if (method.equals("DELETE") && path[path.length - 1].equals("epic") && pathForId != null) {
            taskManager.deleteEpicById(Integer.parseInt(pathForId[1])); // Удаляем эпик по ID
            // Преобразовываем значения мапы в Json
            String response = gson.gsonForEpic.toJson("Эпик по ID № " +
                    Integer.parseInt(pathForId[1]) + " был удален");
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
            // Метод обновляющий задачу
        } else if (method.equals("PUT") && path[path.length - 1].equals("task")) {
            // в переменную входящего потока записали байты задачи для обновления
            InputStreamReader inputStreamReader = new InputStreamReader(exchange.getRequestBody(),
                    StandardCharsets.UTF_8);
            // Байтовое представление задачи помещаем в буфер
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            // Из буфера достаем задачу ввиде строки
            String stringTask = bufferedReader.readLine();
            // Преобразуем строку в ЗАДАЧУ
            Task taskForUpdate = gson.gsonForTask.fromJson(stringTask, Task.class);
            try {
                taskManager.updateTask(taskForUpdate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Преобразовываем значения мапы в Json
            String response = gson.gsonForTask.toJson("Задача с ID № " +
                    taskForUpdate.getId() + " была обновлена");
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
            // Метод обновляющий подзадачу
        } else if (method.equals("PUT") && path[path.length - 1].equals("subtask")) {
            // в переменную входящего потока записали байты задачи для обновления
            InputStreamReader inputStreamReader = new InputStreamReader(exchange.getRequestBody(),
                    StandardCharsets.UTF_8);
            // Байтовое представление задачи помещаем в буфер
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            // Из буфера достаем подзадачу ввиде строки
            String stringSubTask = bufferedReader.readLine();
            // Преобразуем строку в ПОДЗАДАЧУ
            Subtask subtaskForUpdate = gson.gsonForSubTask.fromJson(stringSubTask, Subtask.class);
            try {
                taskManager.updateSubTask(subtaskForUpdate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Преобразовываем значения мапы в Json
            String response = gson.gsonForSubTask.toJson("Подзадача с ID № " +
                    subtaskForUpdate.getId() + " была обновлена");
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
            // Метод обновляющий эпик
        } else if (method.equals("PUT") && path[path.length - 1].equals("epic")) {
            // в переменную входящего потока записали байты задачи для обновления
            InputStreamReader inputStreamReader = new InputStreamReader(exchange.getRequestBody(),
                    StandardCharsets.UTF_8);
            // Байтовое представление задачи помещаем в буфер
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            // Из буфера достаем подзадачу ввиде строки
            String stringEpic = bufferedReader.readLine();
            // Преобразуем строку в ПОДЗАДАЧУ
            Epic epicForUpdate = gson.gsonForEpic.fromJson(stringEpic, Epic.class);
            try {
                taskManager.updateEpic(epicForUpdate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Преобразовываем значения мапы в Json
            String response = gson.gsonForEpic.toJson("ЭПИК с ID № " +
                    epicForUpdate.getId() + " был обновлен");
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
            // Метод создающий задачу
        } else if (method.equals("POST") && path[path.length - 1].equals("task")) {
            // в переменную входящего потока записали байты задачи для обновления
            InputStreamReader inputStreamReader = new InputStreamReader(exchange.getRequestBody(),
                    StandardCharsets.UTF_8);
            // Байтовое представление задачи помещаем в буфер
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            // Из буфера достаем задачу ввиде строки
            String stringTask = bufferedReader.readLine();
            // Преобразуем строку в ЗАДАЧУ
            Task taskForCreate = gson.gsonForTask.fromJson(stringTask, Task.class);
            try {
                taskManager.createTask(taskForCreate); // Создаем задачу
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Преобразовываем значения мапы в Json
            String response = gson.gsonForTask.toJson("Задача с ID № " +
                    taskForCreate.getId() + " успешно создана");
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
            // Метод создающий подзадачу
        } else if (method.equals("POST") && path[path.length - 1].equals("subtask")) {
            // в переменную входящего потока записали байты задачи для обновления
            InputStreamReader inputStreamReader = new InputStreamReader(exchange.getRequestBody(),
                    StandardCharsets.UTF_8);
            // Байтовое представление задачи помещаем в буфер
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            // Из буфера достаем подзадачу ввиде строки
            String stringSubTask = bufferedReader.readLine();
            // Преобразуем строку в ПОДЗАДАЧУ
            Subtask subtaskForCreate = gson.gsonForSubTask.fromJson(stringSubTask, Subtask.class);
            try {
                taskManager.createTask(subtaskForCreate); // Создаем подзадачу
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Преобразовываем значения мапы в Json
            String response = gson.gsonForSubTask.toJson("Подзадача с ID № " +
                    subtaskForCreate.getId() + " была успешно создана");
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
        } else if (method.equals("POST") && path[path.length - 1].equals("epic")) {
            // в переменную входящего потока записали байты задачи для обновления
            InputStreamReader inputStreamReader = new InputStreamReader(exchange.getRequestBody(),
                    StandardCharsets.UTF_8);
            // Байтовое представление задачи помещаем в буфер
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            // Из буфера достаем подзадачу ввиде строки
            String stringEpic = bufferedReader.readLine();
            // Преобразуем строку в ПОДЗАДАЧУ
            Epic epicForCreate = gson.gsonForEpic.fromJson(stringEpic, Epic.class);
            try {
                taskManager.createTask(epicForCreate); // Создаем ЭПИК
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Преобразовываем значения мапы в Json
            String response = gson.gsonForEpic.toJson("ЭПИК с ID № " +
                    epicForCreate.getId() + " был успешно создан");
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
        }
    }
}
