package tracker.controllers;

import tracker.model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    List<Task> historyOfTask = new ArrayList<>(10); // список для добавления задач в историю
    @Override
    public void addTask(Task task) {
        if (historyOfTask.size() < 10) {
            historyOfTask.add(task);
        } else {
            historyOfTask.remove(0);
            historyOfTask.add(task);
        }
    }

    @Override
    public List<Task> getHistory() { // метод возвращающий историю, последнии 10 элеметов, существующих задач
        return historyOfTask;
    }

}
