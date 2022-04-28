package tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Epic epic; // Подзадача приходит к конкретному эпику
    transient int EpicId; // Для нового конструктора, что при воссоздании сабтаска из файла привязать его к эпику
    public Subtask(String name, String description, Status status, Epic epic, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, status, startTime, duration);
        this.epic = epic;
        this.TYPE = TypeOfTask.SUBTASK;
    }

    public Subtask(String name, String description, Status status, int EpicId, int id, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, status, startTime, duration);
        this.EpicId = EpicId;
        this.id = id;
        this.TYPE = TypeOfTask.SUBTASK;
    }

    // конструктор необходимый для обновления конкретной подзадачи, конкретного эпика по ID
    public Subtask(String name, String description, Status status, Epic epic, int id, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, status, startTime, duration);
        this.epic = epic;
        this.id = id;
        this.TYPE = TypeOfTask.SUBTASK;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public TypeOfTask getTYPE() {
        return TYPE;
    }

    @Override
    public void setTYPE(TypeOfTask TYPE) {
        this.TYPE = TYPE;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + this.id +
                ", name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", startTime=" + this.startTime +
                ", duration=" + this.duration +
                '}';
    }

    public int getEpicId() {
        return EpicId;
    }

    public void setEpicId(int epicId) {
        EpicId = epicId;
    }
}


