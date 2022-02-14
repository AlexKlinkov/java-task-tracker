package tracker.model;

import java.util.Objects;

public class Subtask extends Task {
    private Epic epic; // Подзадача приходит к конкретному эпику
    int id = 0; // идентификатор устанавливается менеджером

    public Subtask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        this.epic = epic;
    }

    // конструктор необходимый для обновления конкретной подзадачи, конкретного эпика по ID
    public Subtask(String name, String description, Status status, Epic epic, int id) {
        super(name, description, status);
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
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}


