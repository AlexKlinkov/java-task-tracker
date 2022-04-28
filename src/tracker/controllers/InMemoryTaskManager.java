package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> saveTask = new HashMap<>(); // Коллекция для хранения отдельно стоящих задач.
    protected HashMap<Integer, Subtask> saveSubTask = new HashMap<>(); // Коллекция для хранения подзадач.
    protected HashMap<Integer, Epic> saveEpic = new HashMap<>(); // Коллекция для хранения сложных задач.
    int newId = 0; // счетчик для установки уникального значений по ID (сквозное нумерование)

    HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    // 1. Метод позволяющий хранить любые типы задач
    @Override
    public void createTask(Task task) { //
        if (task instanceof Subtask) { // Проверяем принадлежит ли объект данному классу
            if (!checkIntersection(task)) { // Проверяем на пересечение, если подзадача не пересекается
                // с уже существующими задачами/подзадачами, то спокойно создаем подзадачу
                Subtask subTaskSave = (Subtask) task; // Преобразуем объект в нужный нам тип
                newId++;                       // Получаем уникальный идентификатор для задачи
                subTaskSave.setId(newId);      // Устанавливаем уникальный идентификатор задаче
                saveSubTask.put(newId, subTaskSave); // Сохраняем объект в коллекцию
                subTaskSave.getEpic().getListWithAllSubTasks().add(subTaskSave); // сохраняем подзадачу эпика в его
                // список
                // всех подзадач
                subTaskSave.getEpic().setStatus(subTaskSave.getEpic().setStatusForEpic());// Присваиваем эпику
                // статус в соответствием с ТЗ
            } else {
                throw new IllegalArgumentException("Подзадача пересекается по времени");
            }
        } else if (task instanceof Epic) {
            Epic epicSave = (Epic) task;
            newId++;
            epicSave.setId(newId);
            saveEpic.put(epicSave.getId(), epicSave); // Сохраняем эпическую задачу с тем статусом, с которым она пришла
            epicSave.setStatus(epicSave.setStatusForEpic()); // Присваиваем эпику статус в соответствием с ТЗ
        } else if (task instanceof Task) { // Проверяем к какому классу относится объект
            if (!checkIntersection(task)) { // Проверяем на пересечение, если задача не пересекается
                // с уже существующими задачами/подзадачами, то спокойно создаем задачу
                Task taskSave = (Task) task; // Преобразуем объект в нужный нам класс
                newId++; // Увеличиваем значения счетчика, для установки уникального ID
                taskSave.setId(newId); // Присваиваем идентификационный номер новой задаче
                saveTask.put(newId, taskSave); // Сохраняем задачу в коллекцию
            } else {
                throw new IllegalArgumentException("Задача пересекается по времени");
            }
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
        for (int i = 1; i <= newId; i++) { // если нет больше вообще задач, то их не должно быть и в истории
            if (!(saveSubTask.get(i) instanceof Subtask) && !(saveEpic.get(i) instanceof Epic) &&
                    !(historyManager.getHistory().isEmpty())) {
                historyManager.remove(saveTask.get(i).getId()); // Удаляем задачи из истории
            }
        }
        saveTask.clear(); // Удаляем задачу из колекции
        getListOfAllTasks().clear(); // Удаляем все задачи из списка
    }

    // 2.2 Метод для удаления всех подзадач
    @Override
    public void deleteAllSubTasks() {
        for (int i = 1; i <= newId; i++) { // если нет больше подзадач, значит и эпиков не должно быть
            if (saveSubTask.get(i) instanceof Subtask &&
                    !(historyManager.getHistory().isEmpty())) {
                historyManager.remove(saveSubTask.get(i).getId()); // Удаляем подзадачи из истории
            }
        }
        saveSubTask.clear(); // Удаляем все подзадачи из коллекции
        getListOfAllSubTasks().clear(); // очищаем список с подзадачами
    }

    // 2.2 Метод для удаления всех сложных (эпических) задач
    @Override
    public void deleteAllEpic() {
        for (int i = 1; i <= newId; i++) { // значит эпика и подзадач не должно быть больше в истории
            if (saveSubTask.get(i) instanceof Subtask &&
                    !(historyManager.getHistory().isEmpty())) {
                historyManager.remove(saveSubTask.get(i).getId());
            } else if (saveEpic.get(i) instanceof Epic &&
                    !(historyManager.getHistory().isEmpty())) {
                historyManager.remove(saveEpic.get(i).getId());
            }
        }
        saveEpic.clear();
        saveSubTask.clear(); // Если нет больше ни одной эпической задачи, значит и не должно быть больше подзадач
        getListOfAllEpic().clear(); // Удаляем из списка все эпики
        getListOfAllSubTasks().clear(); // Удаляем все подзадачи из списка
    }

    // 2.3 Метод для получения отдельно стоящей задачи по идентификатору
    @Override
    public Task getTaskById(int id) {
        if (saveTask.get(id) != null) {
            historyManager.addTask(saveTask.get(id)); // обновляем историю просмотров
            return saveTask.get(id);
        } else {
            throw new NullPointerException("Задачи с данным ID не существует");
        }
    }

    // 2.3 Метод для получения отдельно подзадачи по идентификатору
    @Override
    public Subtask getSubTaskById(int id) {
        if (saveSubTask.get(id) != null) {
            historyManager.addTask(saveSubTask.get(id)); // обновляем историю просмотров
            return saveSubTask.get(id);
        } else {
            throw new NullPointerException("Подзадачи с данным ID не существует");
        }
    }

    // 2.3 Метод для получения отдельной (сложной) эпической задачи по идентификатору
    @Override
    public Epic getEpicById(int id) {
        if (saveEpic.get(id) != null) {
            historyManager.addTask(saveEpic.get(id)); // обновляем историю просмотров
            return saveEpic.get(id);
        } else {
            throw new NullPointerException("Эпика с данным ID не существует");
        }
    }

    // 2.5 Обновление задачи по идентификатору (отдельно стоящая задача)
    @Override
    public void updateTask(Task task) throws Exception {
        if (!checkIntersection(task)) { // Проверяем на пересечение, если задача не пересекается
            // с уже существующими задачами/подзадачами, то спокойно создаем задачу
            for (Integer findId : saveTask.keySet()) {
                if (task.getId() == findId) { // Если в мапе есть уже задача с таким ID, заменяем ее на новую
                    saveTask.put(task.getId(), task);
                    historyManager.remove(task.getId()); // если предыдущей задачи уже нет в коллекции, то ее
                    // не должно быть и в истории
                    return;
                }
            }
            throw new Exception("Задача для обновления не найдена");
        } else {
            throw new IllegalArgumentException("Задача пересекается по времени");
        }
    }

    // 2.5 Обновление подзадачи по идентификатору
    @Override
    public void updateSubTask(Subtask subtask) throws Exception {
        if (!checkIntersection(subtask)) { // Проверяем на пересечение, если подзадача не пересекается
            // с уже существующими задачами/подзадачами, то спокойно создаем подзадачу
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
                            subtask.getEpic().setListWithAllSubTasks(epic.getListWithAllSubTasks()); // новому эпику
                            // отдаем все предыдущие подзадачи, с учетом обновления
                            saveSubTask.put(subtask.getId(), subtask); // обновляем данные,
                            // после обновление данных в эпик-списке
                            subtask.getEpic().setStatus(subtask.getEpic().setStatusForEpic()); // после обновления
                            // подзадачи, обновляем статус эпика
                            historyManager.remove(subtask.getId()); // если предыдущей задачи уже нет в коллекции, то ее
                            // не должно быть и в истории
                            return;
                        }
                    }
                }
            }
            throw new Exception("Подзадача для обновления не найдена");
        } else {
            throw new IllegalArgumentException("Подзадача пересекается по времени");
        }
    }

    // 2.5 Обновление задачи (сложные) эпические по идентификатору
    @Override
    public void updateEpic(Epic epic) throws Exception {
        for (Integer findId : saveEpic.keySet()) {
            if (epic.getId() == findId) { // Если в мапе есть уже задача с таким ID, заменяем ее на новую
                saveEpic.remove(findId);
                saveEpic.put(epic.getId(), epic);
                epic.setStatus(epic.setStatusForEpic()); // Эпик новый, но статус простовляется на основе подзадач
                historyManager.remove(epic.getId());// если предыдущей задачи уже нет в коллекции, то ее
                // не должно быть и в истории
                return;
            }
        }
        throw new Exception("Эпик для обновления не найден");
    }

    // 2.6 Удаление отдельно стоящей задачи по идентификатору
    @Override
    public void deleteTaskById(int id) {
        if (saveTask.containsKey(id)) {
            saveTask.remove(id);
            if (getListOfAllTasks().contains(saveTask.get(id))) {
                getListOfAllTasks().remove(id);
            }
            historyManager.remove(id); // Убераем из истории несуществующие задачи
        } else {
            throw new IllegalArgumentException("Задача для удаления по данному ID не была найдена");
        }
    }

    // 2.6 Удаление подзадачи по идентификатору
    @Override
    public void deleteSubTaskById(int id) {
        // Если мы удаляем подзадачу, значит ее надо убрать из списка подзадач относящихся к эпику и
        // обновить статус эпика
        // Удаляем подзадачу из списка подзадач относящихся к эпику
        if (saveSubTask.containsKey(id)) {
            Epic epic = saveSubTask.get(id).getEpic(); // Эпик данной подзадачи
            ArrayList<Subtask> listOfSubTasks = epic.getListWithAllSubTasks(); // список подзадач данного эпика
            if (listOfSubTasks.contains(saveSubTask.get(id))) { // если список подзадач содержит данную задачу,
                // то удаляем его из списка эпика с подзадачами
                listOfSubTasks.remove(saveSubTask.get(id));
                saveSubTask.remove(id); // удаляем саму подзадачу из коллекции
                historyManager.remove(id); // Убераем из истории эпик, если у него не осталось подзадач
                epic.setStatus(epic.setStatusForEpic()); // обновляем статус эпика
            }
        } else {
            throw new IllegalArgumentException("Подзадача для удаления по данному ID не была найдена");
        }
    }

    // 2.6 Удаление сложной (эпической) задачи по идентификатору
    @Override
    public void deleteEpicById(int id) {
        // Если эпик будет удален, его подзадачи тоже должны быть удалены
        if (saveEpic.containsKey(id)) {
            for (Subtask subtask : saveEpic.get(id).getListWithAllSubTasks()) { // Проходимся по списку подзадач Эпика
                if (saveSubTask.containsValue(subtask)) { // Если задача еще не удалена из коллекции, удаляем ее
                    historyManager.remove(subtask.getId()); // Убераем из истории несуществующую задачу (Подзадачи)
                    saveSubTask.remove(subtask.getId());
                    getListOfAllSubTasks().remove(subtask);
                }
            }
            saveEpic.get(id).getListWithAllSubTasks().clear(); // Удаляем все подзадачи данного эпика в эпик-списке
            saveEpic.remove(id); // Удаляем сам эпик из коллекции
            historyManager.remove(id); // Убераем из истории несуществующие задачи (Сам эпик)
        } else {
            throw new IllegalArgumentException("Эпик для удаления по данному ID не был найден");
        }
    }

    // 3.1 Метод для получения списка всех подзадач определенного эпика
    @Override
    public ArrayList<Subtask> getAllSubTasksAttitudeToEpic(Epic epic) {
        return epic.getListWithAllSubTasks();
    }

    public HashMap<Integer, Task> getSaveTask() {
        return saveTask;
    }

    public HashMap<Integer, Subtask> getSaveSubTask() {
        return saveSubTask;
    }

    public HashMap<Integer, Epic> getSaveEpic() {
        return saveEpic;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    protected Comparator<Task> comparatorStartTime = new Comparator<>() { // Для правильной сортировки в списке
        @Override
        public int compare(Task o1, Task o2) {
            if (o1.getStartTime() != null && o2.getStartTime() != null) { // Оба значения не Null
                return o1.getStartTime().compareTo(o2.getStartTime()); // если отрицательное число, то
                // первый объект меньше
            } else if (o1.getStartTime() != null && o2.getStartTime() == null) { // Первое значение не Null
                return -1;
            } else if (o1.getStartTime() == null && o2.getStartTime() != null) { // Первое значение Null
                return 1;
            } else { // Оба значения Null
                return 0;
            }
        }
    };

    // Делаем одно отсортированное множество
    public Set<Task> setForSortingTaskAnsSubTask = new TreeSet(comparatorStartTime);

    public List<Task> getPrioritizedTasks() { // Метод возвращающий список задач и подзадач в заданном порядке.
        ArrayList<Task> sortedListWithTaskAnsSubtask = new ArrayList<>(); // отсортированный список для возвращения
        setForSortingTaskAnsSubTask.addAll(getSaveTask().values()); // Добавляем все задачи
        setForSortingTaskAnsSubTask.addAll(getSaveSubTask().values()); // Добавляем все подзадачи
        sortedListWithTaskAnsSubtask.addAll(setForSortingTaskAnsSubTask);
        return sortedListWithTaskAnsSubtask;
    }

    public boolean checkIntersection(Task task) {
        if (!getPrioritizedTasks().isEmpty()) { // Проверяем на пересечение создаваемую/обновляемую задачу,
            // только если есть с чем сравнивать
            for (Task taskInList : getPrioritizedTasks()) {
                // Проверяем задано ли у создаваемой/обновляемой задач начало ее выполнения, если нет,
                // то она ни с чем не пересечется
                if (task.getStartTime() == null) {
                    return false; // Не пересекаются
                    // Если задача из списка не имеет начала старта, то они тоже не пересекутся
                } else if (taskInList.getStartTime() == null) {
                    return false; // Не пересекаются
                    // Если все поля не равны Null
                } else if (taskInList.getDuration() != null && task.getDuration() != null) {
                    // Все пересекающиеся интервалы
                    if ((task.getStartTime().isBefore(taskInList.getStartTime().plus(taskInList.getDuration())) ||
                            task.getStartTime().equals(taskInList.getStartTime().plus(taskInList.getDuration()))) &&
                            (task.getStartTime().plus(task.getDuration()).isAfter(taskInList.getStartTime())) ||
                            task.getStartTime().plus(task.getDuration()).equals(task.getStartTime())) {
                        return true; // Пересекаются
                    }
                    // Если поля времени в задачи в списке не равны Null, а продолжительность
                    // создаваемой/обновляемой задачи равна Null
                } else if (taskInList.getDuration() != null && task.getDuration() == null) {
                    // Если создаваемая/обновляемая задача совпадает со временем начала задачи из списка
                    if (task.getStartTime().equals(taskInList.getStartTime())) {
                        return true; // Пересекаются
                        // Если начало задачи создаваемой/обновляемой совпадает со временем завершения задачи из списка
                    } else if (task.getStartTime().equals(taskInList.getStartTime().plus(taskInList.getDuration()))) {
                        return true; // Пересекаются
                        // Если начало создаваемой/обновляемой задачи попадает в середину выполнения задачи из списка
                    } else if (task.getStartTime().isAfter(taskInList.getStartTime()) &&
                            task.getStartTime().isBefore(taskInList.getStartTime().plus(taskInList.getDuration()))) {
                        return true; // Пересекаются
                    }
                    // Если поля времени создаваемой/обновляемой задачи не равны Null, а задача из списка не
                    // имеет продолжительности
                } else if (taskInList.getDuration() == null && task.getDuration() != null) {
                    // Если начало задачи из списка равняется началу задачи создаваемой/обновляемой
                    if (taskInList.getStartTime().equals(task.getStartTime())) {
                        return true; // Пересекаются
                        // Если начало задачи из списка совпадает с концом выполнения задачи создаваемой/обновляемой
                    } else if (taskInList.getStartTime().equals(task.getStartTime().plus(task.getDuration()))) {
                        return true; // Пересекаются
                        // Если начало задачи из списка попадает в середину выполнения создаваемой/обновляемой задачи
                    } else if (taskInList.getStartTime().isAfter(task.getStartTime()) &&
                            taskInList.getStartTime().isBefore(task.getStartTime().plus(task.getDuration()))) {
                        return true; // Пересекаются
                    }
                    // Если создаваемая/обновляемая задача и задач из списка имеют только начало не равное Null,
                    // а их продолжительности равна Null
                } else if (task.getStartTime().equals(taskInList.getStartTime()) && task.getDuration() == null &&
                        taskInList.getDuration() == null) {
                    return true; // Пересекаются
                }
            }
        }
        return false;
    }
}
