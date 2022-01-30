import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    HashMap<Integer, Task> saveTask = new HashMap<>(); // Коллекция для хранения отдельно стоящих задач.
    HashMap<Integer, Subtask> saveSubTask = new HashMap<>(); // Коллекция для хранения подзадач.
    HashMap<Integer, Epic> saveEpic = new HashMap<>(); // Коллекция для хранения сложных задач.
    int newIdTask = 0; // счетчик для установки уникального значения ID для обычных задач
    int newIdSubTask = 0; // счетчик для установки уникального значения ID для обычных задач
    int newIdEpic = 0; // счетчик для установки уникального значения ID для сложных (эпических задач)

    // 1. Метод позволяющий хранить любые типы задач
    public void saveTask(Object obj) { //
        if (obj.getClass().getName().equals("Task")) { // проверяем к какому классу относится объект
            Task taskSave = (Task) obj; // Преобразуем объект в нужный нам класс
            newIdTask++; // Увеличиваем значения счетчика, для установки уникального ID
            taskSave.setId(newIdTask); // Присваиваем идентификационный номер новой задаче
            saveTask.put(newIdTask, taskSave); // Сохраняем задачу в коллекцию
        } else if (obj.getClass().getName().equals("Subtask")) {
            Subtask subTaskSave = (Subtask) obj;
            newIdSubTask++;
            subTaskSave.setId(newIdSubTask);
            int newId = subTaskSave.getId();
            int epicId = subTaskSave.getEpicId(); // ID эпической задачи, к которой привязана подзадача
            getEpicById(epicId).listWithAllIdSubTasks.add(newId); // Получаем данную эпическу задачу по идентификатору и
            // в ее поле список, записываем ID сабтасков
            saveSubTask.put(newId, subTaskSave);
            listOfAllStatusSubTasks.clear(); // Чистим список со статусами подзадач перед вызовом метода
            // для каждого нового эпика
            getEpicById(subTaskSave.epicId).setStatus(setStatusForEpic(epicId)); // Присваиваем эпику статус в
            // соответствием с ТЗ
        } else if (obj.getClass().getName().equals("Epic")) {
            Epic epicSave = (Epic) obj;
            newIdEpic++;
            epicSave.setId(newIdEpic);
            saveEpic.put(newIdEpic, epicSave); // Сохраняем эпическую задачу с тем статусом, с которым она пришла
            listOfAllStatusSubTasks.clear(); // Чистим список со статусами подзадач перед вызовом метода
            // для каждого нового эпика
            epicSave.setStatus(setStatusForEpic(newIdEpic)); // Присваиваем эпику статус в соответствием с ТЗ
        }
    }

    // 2.1 Метод для получения списка всех отедельно стоящих задач
    ArrayList<Task> listWithAllTasks = new ArrayList<>(); // список со всеми отдельно стоящими

    // задачами, который будем возвращать
    public ArrayList<Task> getListOfAllTasks() {
        listWithAllTasks.clear(); // Чистим список перед добавлением значений, так как возможно что-то было удалено
        // Чтоб значения не задваивались
        for (Integer id : saveTask.keySet()) {
            listWithAllTasks.add(saveTask.get(id));
        }
        return listWithAllTasks;
    }

    // 2.1 Метод для получения списка всех подзадач
    ArrayList<Subtask> listWithAllSubTasks = new ArrayList<>(); // список со всеми отдельно стоящими

    // задачами, который будем возвращать
    public ArrayList<Subtask> getListOfAllSubTasks() {
        listWithAllSubTasks.clear();
        for (Integer id : saveSubTask.keySet()) {
            listWithAllSubTasks.add(saveSubTask.get(id));
        }
        return listWithAllSubTasks;
    }

    // 2.1 Метод для получения списка всех сложных (эпических) задач
    ArrayList<Epic> listWithAllEpic = new ArrayList<>(); // список со всеми отдельно стоящими

    // задачами, который будем возвращать
    public ArrayList<Epic> getListOfAllEpic() {
        listWithAllEpic.clear();
        for (Integer id : saveEpic.keySet()) {
            listWithAllEpic.add(saveEpic.get(id));
        }
        return listWithAllEpic;
    }

    // 2.2 Метод для удаления всех задач (отдельно стоящие задачи)
    public void deleteAllTasks() {
        saveTask.clear();
    }

    // 2.2 Метод для удаления всех подзадач
    public void deleteAllSubTasks() {
        saveSubTask.clear();
    }

    // 2.2 Метод для удаления всех сложных (эпических) задач
    public void deleteAllEpic() {
        saveEpic.clear();
        saveSubTask.clear(); // Если нет больше ни одной эпической задачи, значит и не должно быть больше подзадач
    }

    // 2.3 Метод для получения отдельно стоящей задачи по идентификатору
    public Task getTaskById(int id) {
        return saveTask.get(id);
    }

    // 2.3 Метод для получения отдельно подзадачи по идентификатору
    public Subtask getSubTaskById(int id) {
        return saveSubTask.get(id);
    }

    // 2.3 Метод для получения отдельной (сложной) эпической задачи по идентификатору
    public Epic getEpicById(int id) {
        return saveEpic.get(id);
    }

    // 2.5 Обновление задачи по идентификатору (отдельно стоящая задача)
    public void updateTask(Task task) {
        for (Integer findId : saveTask.keySet()) {
            if (task.id == findId) { // Если в мапе есть уже задача с таким ID, заменяем ее на новую
                saveTask.put(task.getId(), task);
                return;
            }
        }
        saveTask.put(task.getId(), task); // если задача необнаружена для обновления, создается новая
    }

    // 2.5 Обновление подзадачи по идентификатору
    public void updateSubTask(Subtask subtask) {
        for (Integer findId : saveSubTask.keySet()) {
            if (subtask.id == findId) { // Если в мапе есть уже задача с таким ID, заменяем ее на новую
                saveSubTask.put(subtask.getId(), subtask);
                return;
            }
        }
        saveTask.put(subtask.getId(), subtask); // если задача необнаружена для обновления, создается новая
    }

    // 2.5 Обновление задачи (сложные) эпические по идентификатору
    public void updateEpic(Epic epic) {
        for (Integer findId : saveEpic.keySet()) {
            if (epic.id == findId) { // Если в мапе есть уже задача с таким ID, заменяем ее на новую
                saveEpic.put(epic.getId(), epic);
                return;
            }
        }
        saveTask.put(epic.getId(), epic); // если задача необнаружена для обновления, создается новая
    }

    // 2.6 Удаление отдельно стоящей задачи по идентификатору
    public void deleteTaskById(int id) {
        saveTask.remove(id);
    }

    // 2.6 Удаление подзадачи по идентификатору
    public void deleteSubTaskById(int id) {
        saveSubTask.remove(id);
    }

    // 2.6 Удаление сложной (эпической) задачи по идентификатору
    public void deleteEpicById(int id) {
        // Если эпик будет удален, его подзадачи тоже должны быть удалены
        for (int i = 0; i < saveEpic.get(id).getListWithAllIdSubTasks().size(); i++) { // проходимся по ID подзадач
            int idSub = saveEpic.get(id).getListWithAllIdSubTasks().get(i); // Получили подзадачу по ID
            deleteSubTaskById(idSub); // Удаляем все подзадачи данного эпика
        }

        saveEpic.remove(id);
    }

    // 3.1 Метод для получения списка всех подзадач определенного эпика
    ArrayList<Subtask> listOfSubTasksAttitudeToEpic = new ArrayList<>(); // Лист для всех подзадач опеределенного эпика

    public ArrayList<Subtask> getAllSubTasksAttitudeToEpic(int taskId) {
        Epic epic = saveEpic.get(taskId); // Получили определенный Эпик
        epic.getListWithAllIdSubTasks(); // Получаем весь список ID подзадач относящихся к эпику
        for (int i = 0; i < epic.getListWithAllIdSubTasks().size(); i++) {
            listOfSubTasksAttitudeToEpic.add(getSubTaskById(i));
        }
        return listOfSubTasksAttitudeToEpic;
    }

    // Метод для установки статуса Епика
    ArrayList<String> listOfAllStatusSubTasks = new ArrayList<>(); //

    public String setStatusForEpic(int id) {
        if (getEpicById(id).getListWithAllIdSubTasks().isEmpty()) {
            return "NEW";
        } else if (!getEpicById(id).getListWithAllIdSubTasks().isEmpty()) {
            // Возвращающего эпик по ID, обращаюсь к полю содержащий список всех ID подзадач этого эпика
            for (int i = 0; i < getEpicById(id).getListWithAllIdSubTasks().size(); i++) {
                Subtask subtask;
                // Вызываю метод возвращайющий полностью подзадачу по ID
                subtask = getSubTaskById(getEpicById(id).getListWithAllIdSubTasks().get(i));
                listOfAllStatusSubTasks.add(subtask.status); // Сохраняем все статусы подзадач данного эпика
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
                return "NEW";
            } else if (newNew == 0 && inProgress == 0 && done > 0) {
                return "DONE";
            } else if ((newNew > 0 && done > 0) || inProgress > 0) {
                return "IN_PROGRESS";
            }
        }
        return "";
    }
}
