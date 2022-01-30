public class Task {
    int id = 0; // идентификатор
    String name; // Кратко описывающее суть задачи (например, «Переезд»).
    String description; // Для раскрытия деталей задачи.
    String status; // 1. NEW - новая; 2. IN_PROGRESS - в процессе, 3. DONE - выполнена.

    public Task(String name, String description, String status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    // Конструктор необходимый для обновления задачи по ID
    public Task(String name, String description, String status, int id) {
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

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
