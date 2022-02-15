package tracker.model;

import java.util.ArrayList;

public class Epic extends Task {
    int id = 0;
    private Status status = Status.NEW; // Статус по умолчанию
    ArrayList<Subtask> listWithAllSubTasks = new ArrayList<>(); // список всех подзадач эпика

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    // Конструктор для обновления подзадач, указываем айди задачи, которую нужно обновить
    public Epic(String name, String description, Status status, int id) {
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

    public ArrayList<Subtask> getListWithAllSubTasks() {
        return listWithAllSubTasks;
    }

    public void setListWithAllSubTasks(ArrayList<Subtask> listWithAllSubTasks) {
        this.listWithAllSubTasks = listWithAllSubTasks;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    // Метод для установки статуса Епика
    ArrayList<Status> listOfAllStatusSubTasks = new ArrayList<>(); // Список со всеми статусами подзадач данного эпика

    public Status setStatusForEpic() {
        listOfAllStatusSubTasks.clear(); // Удаляем значения от предыдущего эпика
        if (getListWithAllSubTasks().isEmpty()) {
            return Status.NEW;
        } else {
            // Возвращаем в цикле все подзадачи эпика
            for (int i = 0; i < getListWithAllSubTasks().size(); i++) {
                Subtask subtask;
                // Проверяем по очередно статусы подзадач
                subtask = listWithAllSubTasks.get(i);
                listOfAllStatusSubTasks.add(subtask.getStatus()); // Сохраняем все статусы подзадач данного эпика
            }
            int newNew = 0;
            int inProgress = 0;
            int done = 0;
            for (int j = 0; j < listOfAllStatusSubTasks.size(); j++) {
                if (listOfAllStatusSubTasks.get(j).equals("NEW")) {
                    newNew++;
                } else if (listOfAllStatusSubTasks.get(j).equals("IN_PROGRESS")) {
                    inProgress++;
                } else {
                    done++;
                }
            }
            if (newNew > 0 && inProgress == 0 && done == 0) {
                return Status.NEW;
            } else if (newNew == 0 && inProgress == 0 && done > 0) {
                return Status.DONE;
            } else if ((newNew > 0 && done > 0) || inProgress > 0) {
                return Status.IN_PROGRESS;
            }
        }
        return Status.NEW;
    }

}

