package tracker.Server;

import com.sun.net.httpserver.HttpServer;
import tracker.controllers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {

    HttpServer httpServer; // Поле веб-сервер
    private static final int PORT = 8080; // Порт сервера
    TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        httpServer = HttpServer.create(); // Создаем веб-сервер
        httpServer.bind(new InetSocketAddress(PORT), 0); // Созданный веб-сервер слушает порт № 8080
        httpServer.createContext("/tasks", new MyHandler(this.taskManager)); // Связываем сервер с
        // обработчиком запроса и исполняю запрос.
        httpServer.start(); // Запускаю сервер
    }

    public void stopTaskServer() {
        httpServer.stop(0);
    }
}

