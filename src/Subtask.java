public class Subtask extends Task {
    int epicId; // Приходит из другой части программы, как и вся подзадача (привязывает подзадачу к конкретному епику)
    int id = 0; // идентификатор устанавливается менеджером

    public Subtask(String name, String description, String status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    // Конструктор необходимый для обновления подзадачи конкретного эпика
    public Subtask(String name, String description, String status, int id, int epicId) {
        super(name, description, status, id);
        this.id = id;
        this.epicId = epicId;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

