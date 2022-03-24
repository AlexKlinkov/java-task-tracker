package tracker.model;

import java.util.Objects;

public class Task {
    private int id = 0; // идентификатор
    private String name; // Кратко описывающее суть задачи (например, «Переезд»).
    private String description; // Для раскрытия деталей задачи.
    private Status status; // 1. NEW - новая; 2. IN_PROGRESS - в процессе, 3. DONE - выполнена.
    private TypeOfTask TYPE = TypeOfTask.TASK; // Поле с типом задачи

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    // Конструктор необходимый для обновления задачи по ID
    public Task(String name, String description, Status status, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    public TypeOfTask getTYPE() {
        return TYPE;
    }

    public void setTYPE(TypeOfTask TYPE) {
        this.TYPE = TYPE;
    }
}
