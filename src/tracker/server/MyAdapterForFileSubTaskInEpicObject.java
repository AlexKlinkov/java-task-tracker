package tracker.server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import tracker.model.Epic;
import tracker.model.Subtask;

import java.io.IOException;

import static tracker.model.Status.NEW;

public class MyAdapterForFileSubTaskInEpicObject extends TypeAdapter<Subtask> { // Для обработки полей subtask в эпике

    @Override
    public void write(final JsonWriter jsonWriter, final Subtask subtask) throws IOException {
        // Подзадачу приводим к нужному формату
        jsonWriter.value(subtask.getId());
    }

    @Override
    public Subtask read(final JsonReader jsonReader) throws IOException {
        int subtaskId = Integer.parseInt(jsonReader.nextString());
        // Для передачи ID подзадачи
        Subtask subtask = new Subtask("", "", NEW,
                new Epic("", "", NEW, null, null), null, null);
        subtask.setId(subtaskId);
        return subtask;
    }
}
