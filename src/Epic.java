import java.util.ArrayList;

public class Epic extends Task {
    int id = 0;
    private String status = "NEW"; // Статус по умолчанию
    ArrayList<Integer> listWithAllIdSubTasks = new ArrayList<>(); // список всех ID подзадач относящихся к эпической

    public Epic(String name, String description, String status) {
        super(name, description, status);
    }

    // Конструктор необходимый для обновления задач по ID
    public Epic(String name, String description, String status, int id) {
        super(name, description, status);
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

    public ArrayList<Integer> getListWithAllIdSubTasks() {
        return listWithAllIdSubTasks;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
