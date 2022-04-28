package tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    ArrayList<Subtask> listWithAllSubTasks = new ArrayList<>(); // список всех подзадач эпика
    LocalDateTime endTime;

    public Epic(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.TYPE = TypeOfTask.EPIC;
    }

    // Конструктор для обновления подзадач, указываем айди задачи, которую нужно обновить
    public Epic(String name, String description, Status status, int id, LocalDateTime startTime, Duration duration) {
        super(name, description, status, id, startTime, duration);
        this.id = id;
        this.TYPE = TypeOfTask.EPIC;
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
        setStatusForEpic();
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + this.id +
                ", name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.status +
                ", startTime=" + this.startTime +
                ", duration=" + this.duration +
                ", endTime=" + this.endTime +
                '}';
    }

    // Метод для установки статуса Епика
    transient ArrayList <Status> listOfAllStatusSubTasks = new ArrayList<>(); // Список со всеми статусами подзадач данного эпика

    public Status setStatusForEpic() {
        if (listOfAllStatusSubTasks != null) {
            listOfAllStatusSubTasks.clear(); // очищаем список перед каждым вызовом метода
        }
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
                if (listOfAllStatusSubTasks.get(j).equals(Status.NEW)) {
                    newNew++;
                } else if (listOfAllStatusSubTasks.get(j).equals(Status.IN_PROGRESS)) {
                    inProgress++;
                } else {
                    done++;
                }
            }
            if (newNew > 0 && inProgress == 0 && done == 0) {
                setStatus(Status.NEW); // Изменяем само поле со статусом
                setTimeForEpic(); // Расчитываем новые поля старта, продолжительности и окончания эпика
                return Status.NEW;
            } else if (newNew == 0 && inProgress == 0 && done > 0) {
                setStatus(Status.DONE); // Изменяем само поле со статусом
                setTimeForEpic(); // Расчитываем новые поля старта, продолжительности и окончания эпика
                return Status.DONE;
            } else if ((newNew > 0 && done > 0) || inProgress > 0) {
                setStatus(Status.IN_PROGRESS); // Изменяем само поле со статусом
                setTimeForEpic(); // Расчитываем новые поля старта, продолжительности и окончания эпика
                return Status.IN_PROGRESS;
            }
        }
        return Status.NEW;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public TypeOfTask getTYPE() {
        return TYPE;
    }

    @Override
    public void setTYPE(TypeOfTask TYPE) {
        this.TYPE = TYPE;
    }


    public void setTimeForEpic() { // Метод для расчета полей времени для эпика на основе его подзадач
        if (!listWithAllSubTasks.isEmpty()) {
            LocalDateTime startFirstSubTask = LocalDateTime.of(3000, 1, 1, 0, 0);
            // Время начала задачи (значение по умолчанию)
            LocalDateTime endTimeOfAllSubTask = LocalDateTime.of(1970, 1, 1, 0, 0);
            // Время завершение задачи (значение по умолчанию)
            boolean turnOn = false; // Значение по умолчанию
            for (Subtask subtask : listWithAllSubTasks) {
                if (subtask.getStartTime() != null && subtask.getDuration() != null) {
                    if (subtask.getStartTime().isBefore(startFirstSubTask)) {
                        startFirstSubTask = subtask.getStartTime(); // Старт самой ранней подзадачи
                    }
                    if (subtask.getStartTime().plus(subtask.getDuration()).isAfter(endTimeOfAllSubTask)) {
                        endTimeOfAllSubTask = subtask.getStartTime().plus(subtask.getDuration()); // Конец самой поздней
                        // подзадачи
                    }
                    turnOn = true; // если есть хоть одна подзадача у эпика, у которой не пустые поля времени
                }
            }
            if (turnOn) { // Изменяем поля эпика на основе подзадач. Если нет подходящих подзадач или их нет вовсе,
                // то оставляем поля, которые были присвоены эпику при его создании.
                this.startTime = startFirstSubTask;
                this.duration = Duration.between(startFirstSubTask, endTimeOfAllSubTask);
                this.endTime = endTimeOfAllSubTask;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        Epic epic = (Epic) o;
        return id == epic.id && Objects.equals(((Epic) o).getName(), epic.getName()) &&
                Objects.equals(((Epic) o).getDescription(), epic.getDescription()) &&
                Objects.equals(status, epic.status);
    }

}

