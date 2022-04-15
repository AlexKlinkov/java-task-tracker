package tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Epic epic; // Подзадача приходит к конкретному эпику
    private int EpicId; // Для нового конструктора, что при воссоздании сабтаска из файла привязать его к эпику
    int id = 0; // идентификатор устанавливается менеджером
    private TypeOfTask TYPE = TypeOfTask.SUBTASK; // Поле с типом задачи

    public Subtask(String name, String description, Status status, Epic epic, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, status, startTime, duration);
        this.epic = epic;
    }

    public Subtask(String name, String description, Status status, int EpicI, int id, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, status, startTime, duration);
        this.EpicId = EpicId;
        this.id = id;
    }

    // конструктор необходимый для обновления конкретной подзадачи, конкретного эпика по ID
    public Subtask(String name, String description, Status status, Epic epic, int id, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, status, startTime, duration);
        this.epic = epic;
        this.id = id;
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
                "id=" + id +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }
}


