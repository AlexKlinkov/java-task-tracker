package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    HashMap<Integer, Task> saveTask = new HashMap<>(); // Коллекция для хранения отдельно стоящих задач.
    HashMap<Integer, Subtask> saveSubTask = new HashMap<>(); // Коллекция для хранения подзадач.
    HashMap<Integer, Epic> saveEpic = new HashMap<>(); // Коллекция для хранения сложных задач.
    int newIdTask = 0; // счетчик для установки уникального значения ID для обычных задач
    int newIdSubTask = 0; // счетчик для установки уникального значения ID для обычных задач
    int newIdEpic = 0; // счетчик для установки уникального значения ID для сложных (эпических задач)

    HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    // 1. Метод позволяющий хранить любые типы задач
    @Override
    public void createTask(Task task) { //
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

    // 2.1 Метод для получения списка всех задач (отдельно стоящих)
    ArrayList<Task> listWithAllTasks = new ArrayList<>(); // список со всеми отдельно стоящими

    // задачами, который будем возвращать
    @Override
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
    @Override
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
    @Override
    public ArrayList<Epic> getListOfAllEpic() {
        listWithAllEpic.clear();
        for (Integer id : saveEpic.keySet()) {
            listWithAllEpic.add(saveEpic.get(id));
        }
        return listWithAllEpic;
    }

    // 2.2 Метод для удаления всех задач (отдельно стоящие задачи)
    @Override
    public void deleteAllTasks() {
        saveTask.clear();
        deleteNoneExistTask(); // если нет больше вообще задач, то их не должно быть и в истории
    }

    // 2.2 Метод для удаления всех подзадач
    @Override
    public void deleteAllSubTasks() {
        saveSubTask.clear();
        deleteNoneExistTask(); // если нет больше вообще задач, то их не должно быть и в истории
    }

    // 2.2 Метод для удаления всех сложных (эпических) задач
    @Override
    public void deleteAllEpic() {
        saveEpic.clear();
        saveSubTask.clear(); // Если нет больше ни одной эпической задачи, значит и не должно быть больше подзадач
        deleteNoneExistTask(); // если нет больше вообще задач, то их не должно быть и в истории
    }

    // 2.3 Метод для получения отдельно стоящей задачи по идентификатору
    @Override
    public Task getTaskById(int id) {
        historyManager.addTask(saveTask.get(id)); // обновляем историю просмотров
        return saveTask.get(id);
    }

    // 2.3 Метод для получения отдельно подзадачи по идентификатору
    @Override
    public Subtask getSubTaskById(int id) {
        historyManager.addTask(saveSubTask.get(id)); // обновляем историю просмотров
        return saveSubTask.get(id);
    }

    // 2.3 Метод для получения отдельной (сложной) эпической задачи по идентификатору
    @Override
    public Epic getEpicById(int id) {
        historyManager.addTask(saveEpic.get(id)); // обновляем историю просмотров
        return saveEpic.get(id);
    }

    // 2.5 Обновление задачи по идентификатору (отдельно стоящая задача)
    @Override
    public void updateTask(Task task) {
        for (Integer findId : saveTask.keySet()) {
            if (task.getId() == findId) { // Если в мапе есть уже задача с таким ID, заменяем ее на новую
                saveTask.put(task.getId(), task);
                return;
            }
        }
        deleteNoneExistTask(); // если предыдущей задачи уже нет в коллекции, то ее не должно быть и в истории
    }

    // 2.5 Обновление подзадачи по идентификатору
    @Override
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
        deleteNoneExistTask(); // если предыдущей задачи уже нет в коллекции, то ее не должно быть и в истории
    }

    // 2.5 Обновление задачи (сложные) эпические по идентификатору
    @Override
    public void updateEpic(Epic epic) {
        for (Integer findId : saveEpic.keySet()) {
            if (epic.getId() == findId) { // Если в мапе есть уже задача с таким ID, заменяем ее на новую
                saveEpic.remove(findId);
                saveEpic.put(epic.getId(), epic);
                epic.setStatus(epic.setStatusForEpic()); // Эпик новый, но статус простовляется на основе подзадач
                return;
            }
        }
        deleteNoneExistTask(); // если предыдущей задачи уже нет в коллекции, то ее не должно быть и в истории
    }

    // 2.6 Удаление отдельно стоящей задачи по идентификатору
    @Override
    public void deleteTaskById(int id) {
        saveTask.remove(id);
        deleteNoneExistTask(); // Убераем из истории несуществующие задачи
    }

    // 2.6 Удаление подзадачи по идентификатору
    @Override
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
        deleteNoneExistTask(); // Убераем из истории несуществующие задачи
    }

    // 2.6 Удаление сложной (эпической) задачи по идентификатору
    @Override
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
        deleteNoneExistTask(); // Убераем из истории несуществующие задачи
    }

    // 3.1 Метод для получения списка всех подзадач определенного эпика
    @Override
    public ArrayList<Subtask> getAllSubTasksAttitudeToEpic(Epic epic) {
        return epic.getListWithAllSubTasks();
    }

    boolean taskIsExist; // Значение по умолчанию

    void deleteNoneExistTask() {
        List<Task> newListForAllDelete = new ArrayList<>(); // Список с элементом для удаления, чтоб разом удалить все
        // повторения несуществующих задач из истории
        for (Task task : historyManager.getHistory()) {
            if (saveTask.containsValue(task)) {
                taskIsExist = true;
            } else if (saveSubTask.containsValue(task)) {
                taskIsExist = true;
            } else if (saveEpic.containsValue(task)) {
                taskIsExist = true;
            } else if (taskIsExist == false) {
                newListForAllDelete.add(task); // добавляем в список на удаление
            }
            taskIsExist = false;
        }
        historyManager.getHistory().removeAll(newListForAllDelete); // Удаляем все объекты несуществующей задачи
                                                                                                        // из истории
        newListForAllDelete.clear(); // отчищаем список для удаления
    }
}
