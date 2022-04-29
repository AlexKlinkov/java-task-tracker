package tracker.server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient { // Класс для отправки запросов на KVServer
    private final String url; // Поле путь к серверу хранилища (KVServer)
    private final String API_KEY; // Поле ключ апи, для каждого клиента индивидуальный

    public KVTaskClient(String url) {
        this.url = url;
        this.API_KEY = register();
    }

    // Метод должен сохранять состояние менеджера задач через запрос POST /save/<ключ>?API_KEY= на KVServer
    public void put(String key, String json) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST((HttpRequest.BodyPublishers.ofString(json)))
                .uri(URI.create(url + "/save/" + key + "?" + API_KEY))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (response.statusCode() != 200) {
            System.out.println("Ошибка сохранения значения ключа" + key + " на KVServer");
        }
    }

    // Метод должен возвращать состояние менеджера задач через запрос GET /load/<ключ>?API_KEY= с KVServer
    public String load(String key) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/load/" + key + "?" + API_KEY))
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (response.statusCode() != 200) {
            System.out.println("Ошибка возвращения значения ключа" + key + " с KVServer");
        }
        return "";
    }

    public String register() { // Метод устанавливающий уникальный API_KEY для клиента
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/register"))
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .headers("Content-Type", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (response.statusCode() != 200) {
            System.out.println("Ошибка получения уникального APY_KEY от KVServer");
        }
        return "";
    }

    public String getAPI_KEY() { // Метод возвращающий уникальный API_KEY для менеджера
        return this.API_KEY;
    }
}
