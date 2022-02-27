package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    HashMap<Integer, Task> saveTask = new HashMap<>(); // Коллекция для хранения отдельно стоящих задач.
    HashMap<Integer, Subtask> saveSubTask = new HashMap<>(); // Коллекция для хранения подзадач.
    HashMap<Integer, Epic> saveEpic = new HashMap<>(); // Коллекция для хранения сложных задач.
    int newId = 0; // счетчик для установки уникального значений по ID (сквозное нумерование)

    HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    // 1. Метод позволяющий хранить любые типы задач
    @Override
    public void createTask(Task task) { //
        if (task instanceof Subtask) { // Проверяем принадлежит ли объект данному классу
            Subtask subTaskSave = (Subtask) task; // Преобразуем объект в нужный нам тип
            newId++;                       // Получаем уникальный идентификатор для задачи
            subTaskSave.setId(newId);      // Устанавливаем уникальный идентификатор задаче
            saveSubTask.put(newId, subTaskSave); // Сохраняем объект в коллекцию
            subTaskSave.getEpic().getListWithAllSubTasks().add(subTaskSave); // сохраняем подзадачу эпика в его список
            // всех подзадач
            subTaskSave.getEpic().setStatus(subTaskSave.getEpic().setStatusForEpic());// Присваиваем эпику
            // статус в соответствием с ТЗ
        } else if (task instanceof Epic) {
            Epic epicSave = (Epic) task;
            newId++;
            epicSave.setId(newId);
            saveEpic.put(newId, epicSave); // Сохраняем эпическую задачу с тем статусом, с которым она пришла
            epicSave.setStatus(epicSave.setStatusForEpic()); // Присваиваем эпику статус в соответствием с ТЗ
        } else if (task instanceof Task) { // проверяем к какому классу относится объект
            Task taskSave = (Task) task; // Преобразуем объект в нужный нам класс
            newId++; // Увеличиваем значения счетчика, для установки уникального ID
            taskSave.setId(newId); // Присваиваем идентификационный номер новой задаче
            saveTask.put(newId, taskSave); // Сохраняем задачу в коллекцию
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
        for (int i = 0; i <= newId; i++) { // если нет больше вообще задач, то их не должно быть и в истории
            if (!(saveSubTask.get(i) instanceof Subtask) && !(saveEpic.get(i) instanceof Epic)) {
                historyManager.remove(saveTask.get(i).getId()); // Удаляем задачи из истории
            }
        }
        saveTask.clear(); // Удаляем задачу из колекции
    }

    // 2.2 Метод для удаления всех подзадач
    @Override
    public void deleteAllSubTasks() {
        for (int i = 0; i <= newId; i++) { // если нет больше подзадач, значит и эпиков не должно быть
            if (saveSubTask.get(i) instanceof Subtask) {
                historyManager.remove(saveSubTask.get(i).getId()); // Удаляем подзадачи из истории
            } else if (saveEpic.get(i) instanceof Epic) {
                historyManager.remove(saveEpic.get(i).getId()); // Удаляем эпики из истории
            }
        }
        saveSubTask.clear(); // Удаляем все подзадачи из коллекции
        saveEpic.clear(); // Удаляем все эпики из коллекции
    }

    // 2.2 Метод для удаления всех сложных (эпических) задач
    @Override
    public void deleteAllEpic() {
        for (int i = 0; i <= newId; i++) { // значит эпика и подзадач не должно быть больше в истории
            if (saveSubTask.get(i) instanceof Subtask) {
                historyManager.remove(saveSubTask.get(i).getId());
            } else if (saveEpic.get(i) instanceof Epic) {
                historyManager.remove(saveEpic.get(i).getId());
            }
        }
        saveEpic.clear();
        saveSubTask.clear(); // Если нет больше ни одной эпической задачи, значит и не должно быть больше подзадач
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
        historyManager.remove(task.getId()); // если предыдущей задачи уже нет в коллекции, то ее не должно быть и
                                                                                                            // в истории
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
        historyManager.remove(subtask.getId()); // если предыдущей задачи уже нет в коллекции, то ее не должно быть и
                                                                                                            // в истории
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
        historyManager.remove(epic.getId());
        ; // если предыдущей задачи уже нет в коллекции, то ее не должно быть и в истории
    }

    // 2.6 Удаление отдельно стоящей задачи по идентификатору
    @Override
    public void deleteTaskById(int id) {
        saveTask.remove(id);
        historyManager.remove(id); // Убераем из истории несуществующие задачи
    }

    // 2.6 Удаление подзадачи по идентификатору
    @Override
    public void deleteSubTaskById(int id) {
        // Если мы удаляем подзадачу, значит ее надо убрать из списка подзадач относящихся к эпику и
        // обновить статус эпика
        // Удаляем подзадачу из списка подзадач относящихся к эпику
        Epic epic = saveSubTask.get(id).getEpic(); // Эпик данной подзадачи
        ArrayList<Subtask> listOfSubTasks = epic.getListWithAllSubTasks(); // список подзадач данного эпика
        if (listOfSubTasks.contains(saveSubTask.get(id))) { // если список подзадач содержит данную задачу,
            // то удаляем его из списка эпика с подзадачами
            listOfSubTasks.remove(saveSubTask.get(id));
            saveSubTask.remove(id); // удаляем саму подзадачу из коллекции
        }
        if (listOfSubTasks.isEmpty()) { // Если не осталось больше подзадач у эпика, удаляем сам эпик
            saveEpic.remove(id); // Удаляем сам эпик из коллекции
            historyManager.remove(epic.getId()); // Убераем из истории эпик, если у него не осталось подзадач
        } else { // иначе обновляем статус эпика
            epic.setStatus(epic.setStatusForEpic()); // обновляем статус эпика
        }
        historyManager.remove(id); // Убераем из истории несуществующую задачу (ПОДЗАДАЧУ)
    }

    // 2.6 Удаление сложной (эпической) задачи по идентификатору
    @Override
    public void deleteEpicById(int id) {
        // Если эпик будет удален, его подзадачи тоже должны быть удалены
        for (Subtask subtask : saveEpic.get(id).getListWithAllSubTasks()) { // Проходимся по списку подзадач Эпика
            if (saveSubTask.containsValue(subtask)) { // Если задача еще не удалена из коллекции, удаляем ее
                historyManager.remove(subtask.getId()); // Убераем из истории несуществующую задачу (Подзадачи)
                saveSubTask.remove(subtask);
            }
        }
        saveEpic.get(id).getListWithAllSubTasks().clear(); // Удаляем все подзадачи данного эпика в эпик-списке
        saveEpic.remove(id); // Удаляем сам эпик из коллекции
        historyManager.remove(id); // Убераем из истории несуществующие задачи (Сам эпик)
    }

    // 3.1 Метод для получения списка всех подзадач определенного эпика
    @Override
    public ArrayList<Subtask> getAllSubTasksAttitudeToEpic(Epic epic) {
        return epic.getListWithAllSubTasks();
    }
}