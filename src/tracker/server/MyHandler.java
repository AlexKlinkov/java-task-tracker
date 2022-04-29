package tracker.server;

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
    HttpExchange exchange;
    MyJsonForDifferentTypeOfTasks gson;
    String method;
    String[] path;
    String[] pathForId;
    String response;

    public MyHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Объект класса содержащий все требуемые типы Json
        gson = new MyJsonForDifferentTypeOfTasks();
        method = exchange.getRequestMethod(); // Узнаем метод которым стучатся на сервер
        path = exchange.getRequestURI().getPath().split("/"); // Парсим путь для получения каталога
        pathForId = null; // Массив для названий и критериев параметров
        if (exchange.getRequestURI().getQuery() != null) { // Если у запроса есть параметры
            pathForId = exchange.getRequestURI().getQuery().split("="); // парсим путь, параметры запроса
        }
        // Метод возвращающий все задачи; // Метод возвращающий все эпики; // Метод возвращающий все подзадачи
        // Метод возвращающий задачу по ID; // Метод возвращающий эпик по ID; // Метод возвращающий подзадачу по ID
        // Метод возвращающий все задачи и подзадачи в приорететном порядке
        if (method.equals("GET")) {
            easyMyHandlerGetTask();
            // Метод удаляющий все задачи; // Метод удаляющий все подзадачи; // Метод удаляющий все эпики
            // Метод удаляющий задачу по ID; // Метод удаляющий подзадачу по ID; // Метод удаляющий эпик по ID
        } else if (method.equals("DELETE")) {
            easyMyHandlerDeleteTask();
            // Метод обновляющий задачу; // Метод обновляющий подзадачу; // Метод обновляющий эпик
        } else if (method.equals("PUT")) {
            easyMyHandlerPUTTask();
            /// Метод возвращающий все задачи и подзадачи в приорететном порядке
        } else if (method.equals("POST")) {
            easyMyHandlerPOSTTask();
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
        }
    }

    private void easyMyHandlerGetTask() throws IOException {
        if (pathForId == null) {
            if (path[path.length - 1].equals("task")) {
                // Преобразовываем значения мапы в Json
                response = gson.gsonForTask.toJson(taskManager.getSaveTask().values());
            } else if (path[path.length - 1].equals("subtask")) {
                // Преобразовываем значения мапы в Json
                response = gson.gsonForSubTask.toJson(taskManager.getSaveSubTask().values());
            } else if (path[path.length - 1].equals("epic")) {
                // Преобразовываем значения мапы в Json
                response = gson.gsonForEpic.toJson(taskManager.getSaveEpic().values());
            } else if (path[path.length - 1].equals("tasks")) {
                response = gson.gsonForSubTask.toJson(taskManager.getPrioritizedTasks());
            }
        } else {
            if (path[path.length - 1].equals("task")) {
                // Преобразовываем значения мапы в Json
                response = gson.gsonForTask.toJson(taskManager.getTaskById(Integer.parseInt(pathForId[1])));
            } else if (path[path.length - 1].equals("subtask")) {
                // Преобразовываем значения мапы в Json
                response = gson.gsonForSubTask.toJson(taskManager.getSubTaskById(Integer.parseInt(pathForId[1])));
            } else if (path[path.length - 1].equals("epic")) {
                // Преобразовываем значения мапы в Json
                response = gson.gsonForEpic.toJson(taskManager.getEpicById(Integer.parseInt(pathForId[1])));
            }
        }
        exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
        // Вывожу тело запроса
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        } finally {
            exchange.close();
        }
    }

    private void easyMyHandlerDeleteTask() throws IOException {
        if (pathForId == null) {
            if (path[path.length - 1].equals("task")) {
                taskManager.deleteAllTasks(); // Удаляем все задачи
                response = gson.gsonForTask.toJson("Все задачи по адресу " + exchange.getRequestURI().getPath() +
                        " успешно удалены");
            } else if (path[path.length - 1].equals("subtask")) {
                taskManager.deleteAllSubTasks(); // Удаляем все задачи
                response = gson.gsonForSubTask.toJson("Все подзадачи по адресу " +
                        exchange.getRequestURI().getPath() + " успешно удалены");
            } else if (path[path.length - 1].equals("epic")) {
                taskManager.deleteAllEpic(); // Удаляем все задачи
                response = gson.gsonForEpic.toJson("Все эпики по адресу " + exchange.getRequestURI().getPath() +
                        " успешно удалены");
            }
        } else {
            if (path[path.length - 1].equals("task")) {
                taskManager.deleteTaskById(Integer.parseInt(pathForId[1])); // Удаляем задачу по ID
                response = gson.gsonForTask.toJson("Задача по ID № " +
                        Integer.parseInt(pathForId[1]) + " была удалена"); // Преобразовываем значения мапы в Json
            } else if (path[path.length - 1].equals("subtask")) {
                taskManager.deleteSubTaskById(Integer.parseInt(pathForId[1])); // Удаляем подзадачу по ID
                response = gson.gsonForSubTask.toJson("Подзадача по ID № " +
                        Integer.parseInt(pathForId[1]) + " была удалена"); // Преобразовываем значения мапы в Json
            } else if (path[path.length - 1].equals("epic")) {
                taskManager.deleteEpicById(Integer.parseInt(pathForId[1])); // Удаляем эпик по ID
                response = gson.gsonForEpic.toJson("Эпик по ID № " +
                        Integer.parseInt(pathForId[1]) + " был удален"); // Преобразовываем значения мапы в Json
            }
            exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            // Вывожу тело запроса
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
        }
    }

    private void easyMyHandlerPOSTTask() throws IOException {
        // в переменную входящего потока записали байты задачи для обновления
        InputStreamReader inputStreamReader = new InputStreamReader(exchange.getRequestBody(),
                StandardCharsets.UTF_8);
        // Байтовое представление задачи помещаем в буфер
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String stringTask = bufferedReader.readLine(); // Из буфера достаем задачу ввиде строки
        if (path[path.length - 1].equals("task")) {
            Task taskForCreate = gson.gsonForTask.fromJson(stringTask, Task.class); // Преобразуем строку в ЗАДАЧУ
            try {
                taskManager.createTask(taskForCreate);
                exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (path[path.length - 1].equals("subtask")) {
            // Преобразуем строку в ПОДЗАДАЧУ
            Subtask taskForCreate = gson.gsonForSubTask.fromJson(stringTask, Subtask.class);
            try {
                taskManager.createTask(taskForCreate);
                exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (path[path.length - 1].equals("epic")) {
            // Преобразуем строку в ЭПИК
            Epic taskForCreate = gson.gsonForEpic.fromJson(stringTask, Epic.class);
            try {
                taskManager.createTask(taskForCreate);
                exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void easyMyHandlerPUTTask() throws IOException {
        // в переменную входящего потока записали байты задачи для обновления
        InputStreamReader inputStreamReader = new InputStreamReader(exchange.getRequestBody(),
                StandardCharsets.UTF_8);
        // Байтовое представление задачи помещаем в буфер
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String stringTask = bufferedReader.readLine(); // Из буфера достаем задачу ввиде строки
        if (path[path.length - 1].equals("task")) {
            Task taskForCreate = gson.gsonForTask.fromJson(stringTask, Task.class); // Преобразуем строку в ЗАДАЧУ
            try {
                taskManager.updateTask(taskForCreate);
                exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (path[path.length - 1].equals("subtask")) {
            // Преобразуем строку в ПОДЗАДАЧУ
            Subtask taskForCreate = gson.gsonForSubTask.fromJson(stringTask, Subtask.class);
            try {
                taskManager.updateSubTask(taskForCreate);
                exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (path[path.length - 1].equals("epic")) {
            // Преобразуем строку в ЭПИК
            Epic taskForCreate = gson.gsonForEpic.fromJson(stringTask, Epic.class);
            try {
                taskManager.updateEpic(taskForCreate);
                exchange.sendResponseHeaders(200, 0); // Отдаю заголовок ответа
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
