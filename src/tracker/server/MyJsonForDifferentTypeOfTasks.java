package tracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tracker.model.Epic;
import tracker.model.Subtask;

public class MyJsonForDifferentTypeOfTasks {
    /// ДЛЯ ЗАДАЧ
    Gson gsonForTask = new GsonBuilder()
            .setPrettyPrinting() // Устанавливаем красивый вывод в консоль
            .serializeNulls() // Отображаем поля, равные Null
            .create(); // Создаем объект
    /// ДЛЯ ПОДЗАДАЧ
    Gson gsonForSubTask = new GsonBuilder()
            .setPrettyPrinting() // Устанавливаем красивый вывод в консоль
            .serializeNulls() // Отображаем поля, равные Null
            // Устанавливаем правила обработки поля epic в подзадаче
            .registerTypeAdapter(Epic.class, new MyAdapterForFileEpicInSubTaskObject())
            .create(); // Создаем объект
    /// ДЛЯ ЭПИКОВ
    Gson gsonForEpic = new GsonBuilder()
            .setPrettyPrinting() // Устанавливаем красивый вывод в консоль
            .serializeNulls() // Отображаем поля, равные Null
            // Устанавливаем правила обработки поля epic в подзадаче
            .registerTypeAdapter(Subtask.class, new MyAdapterForFileSubTaskInEpicObject())
            .create(); // Создаем объект

    public Gson getGsonForTask() {
        return gsonForTask;
    }

    public Gson getGsonForSubTask() {
        return gsonForSubTask;
    }

    public Gson getGsonForEpic() {
        return gsonForEpic;
    }
}
