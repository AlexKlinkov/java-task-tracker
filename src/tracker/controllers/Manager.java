package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

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
    public void saveTask(Task task) { //
        if (task instanceof Subtask) { // Проверяем принадлежит ли объект данному классу
            Subtask subTaskSave = (Subtask) task; // Преобразуем объект в нужный нам тип
            newIdSubTask++;                       // Получаем уникальный идентификатор для задачи
            subTaskSave.setId(newIdSubTask);      // Устанавливаем уникальный идентификатор задаче
            saveSubTask.put(newIdSubTask, subTaskSave); // Сохраняем объект в коллекцию
            subTaskSave.getEpic().getListWithAllSubTasks().add(subTaskSave); // сохраняем подзадачу эпика в его список
            // всех подзадач
            subTaskSave.getEpic().setStatus(subTaskSave.getEpic().setStatusForEpic());// Присваиваем эпику
            // статус в соответствием с ТЗ
        } else if (task instanceof Epic) {
            Epic epicSave = (Epic) task;
            newIdEpic++;
            epicSave.setId(newIdEpic);
            saveEpic.put(newIdEpic, epicSave); // Сохраняем эпическую задачу с тем статусом, с которым она пришла
            epicSave.setStatus(epicSave.setStatusForEpic()); // Присваиваем эпику статус в соответствием с ТЗ
        } else if (task instanceof Task) { // проверяем к какому классу относится объект
            Task taskSave = (Task) task; // Преобразуем объект в нужный нам класс
            newIdTask++; // Увеличиваем значения счетчика, для установки уникального ID
            taskSave.setId(newIdTask); // Присваиваем идентификационный номер новой задаче
            saveTask.put(newIdTask, taskSave); // Сохраняем задачу в коллекцию
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
            if (task.getId() == findId) { // Если в мапе есть уже задача с таким ID, заменяем ее на новую
                saveTask.put(task.getId(), task);
                return;
            }
        }
    }

    // 2.5 Обновление подзадачи по идентификатору
    public void updateSubTask(Subtask subtask) {
        for (Subtask sub : saveSubTask.values()) {
            if (subtask.getId() == sub.getId()) { // Если в мапе есть уже задача с таким ID, заменяем ее на новую
                // Перед обновления подзадачи, предыдущую нужно удалить из эпик-списка и добавить туда новый вариант
                Epic epic = saveSubTask.get(subtask.getId()).getEpic(); // Эпик предыдущей (до обновления) подзадачи
                ArrayList<Subtask> listOfSubTasks = epic.getListWithAllSubTasks(); // список подзадач этого эпика
                for (int i = 0; i < listOfSubTasks.size(); i++) { // проходимся по списку подзадач
                    if (listOfSubTasks.get(i).getId() == subtask.getId()) { // и при нахождение удаляем ее
                        // из списка подзадач
                        epic.getListWithAllSubTasks().remove(i); // удаляем старый вариант подзадачи
                        epic.getListWithAllSubTasks().add(i, subtask); // добавляем новый вариант в эпик-список
                        subtask.getEpic().setListWithAllSubTasks(epic.getListWithAllSubTasks()); // новому эпику отдаем
                        // все предыдущие подзадачи, с учетом обновления
                        saveSubTask.put(subtask.getId(), subtask); // обновляем данные,
                        // после обновление данных в эпик-списке
                        subtask.getEpic().setStatus(subtask.getEpic().setStatusForEpic()); // после обновления
                        // подзадачи, обновляем статус эпика
                        return;
                    }
                }
            }
        }
    }

    // 2.5 Обновление задачи (сложные) эпические по идентификатору
    public void updateEpic(Epic epic) {
        for (Integer findId : saveEpic.keySet()) {
            if (epic.getId() == findId) { // Если в мапе есть уже задача с таким ID, заменяем ее на новую
                saveEpic.remove(findId);
                saveEpic.put(epic.getId(), epic);
                epic.setStatus(epic.setStatusForEpic()); // Эпик новый, но статус простовляется на основе подзадач
                return;
            }
        }
    }

    // 2.6 Удаление отдельно стоящей задачи по идентификатору
    public void deleteTaskById(int id) {
        saveTask.remove(id);
    }

    // 2.6 Удаление подзадачи по идентификатору
    public void deleteSubTaskById(int id) {
        // Если мы удаляем подзадачу, значит ее надо убрать из списка подзадач относящихся к эпику и
        // обновить статус эпика
        // Удаляем подзадачу из списка подзадач относящихся к эпику
        Epic epic = saveSubTask.get(id).getEpic(); // Эпик данной подзадачи
        ArrayList<Subtask> listOfSubTasks = epic.getListWithAllSubTasks(); // список подзадач данного эпика
        for (int i = 0; i < listOfSubTasks.size(); i++) { // проходимся по списку подзадач
            if (listOfSubTasks.get(i).equals(saveSubTask.get(id))) { // и при нахождение удаляем ее из списка подзадач
                epic.getListWithAllSubTasks().remove(saveSubTask.get(id));
            }
        }
        epic.setStatus(epic.setStatusForEpic()); // обновляем статус эпика
        saveSubTask.remove(id); // удаляем саму подзадачу
    }

    // 2.6 Удаление сложной (эпической) задачи по идентификатору
    public void deleteEpicById(int id) {
        // Если эпик будет удален, его подзадачи тоже должны быть удалены
        ArrayList<Subtask> listofAllSubTasks = saveEpic.get(id).getListWithAllSubTasks(); // список подзадач
        for (int i = 0; i < listofAllSubTasks.size(); i++) {
            for (Subtask subtask : saveSubTask.values()) {
                if (listofAllSubTasks.get(i).getId() == subtask.getId()) {
                    saveSubTask.remove(subtask.getId()); // Удаляем все подзадачи из коллекции, перед удалением эпика
                }
            }
        }
        saveEpic.get(id).getListWithAllSubTasks().clear(); // Удаляем все подзадачи данного эпика в эпик-списке
        saveEpic.remove(id); // Удаляем сам эпик
    }

    // 3.1 Метод для получения списка всех подзадач определенного эпика
    public ArrayList<Subtask> getAllSubTasksAttitudeToEpic(Epic epic) {
        return epic.getListWithAllSubTasks();
    }
}
