package tracker.Server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import tracker.model.Epic;

import java.io.IOException;

import static tracker.model.Status.NEW;

public class MyAdapterForFileEpicInSubTaskObject extends TypeAdapter<Epic> { // Для обработки поля "epic" в подзадачах

    @Override
    public void write(JsonWriter jsonWriter, Epic epic) throws IOException {
        jsonWriter.value(epic.getId());
    }

    @Override
    public Epic read(JsonReader jsonReader) throws IOException {
        int epicId = Integer.parseInt(jsonReader.nextString());
        Epic epic = new Epic("", "", NEW, null, null); // Для передачи эпик ID
        epic.setId(epicId);
        return epic;
    }
}