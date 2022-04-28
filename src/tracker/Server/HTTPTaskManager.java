package tracker.Server;

import tracker.controllers.FileBackedTasksManager;
import tracker.controllers.HistoryManager;
import tracker.controllers.Managers;
import tracker.controllers.TaskManager;
import tracker.model.Task;

import java.io.File;
import java.util.*;

public class HTTPTaskManager extends FileBackedTasksManager {

    private final String urlString; // Поле адрес KVServer, к которому будем обращаться
    KVTaskClient kvTaskClient; // Связываем менеджера с клиентом отправляющим запрос на сервер (KVServer)
    final String APY_KEY; // Уникальный ключ для доступа к серверу
    // Поле для возврата задач, подзадач, эпиков с KVServer
    Map<String, String> forReturnConditionManagerTaskAndSubtaskAndEpic = new LinkedHashMap<>();
    // Поле для возврата истории с KVServer
    List<String> forReturnConditionManagerHistory = new LinkedList<>();
    // Объект класса содержащий все требуемые типы Json
    MyJsonForDifferentTypeOfTasks gson = new MyJsonForDifferentTypeOfTasks();

    public HTTPTaskManager(HistoryManager historyManager, File file, KVTaskClient kvTaskClient, String urlString) {
        super(historyManager, file);
        this.kvTaskClient = kvTaskClient;
        APY_KEY = kvTaskClient.getAPI_KEY();
        this.urlString = urlString;
    }

    @Override
    public void save() {
        if (!getSaveTask().isEmpty()) {
            kvTaskClient.put("tasks", gson.gsonForTask.toJson(getSaveTask().values())); // Постим на сервер через
            // клиента задачи
        } else if (!getSaveSubTask().isEmpty()) {
            // Постим на сервер через
            kvTaskClient.put("subtasks", gson.gsonForSubTask.toJson(getSaveTask().values()));
            // клиента подзадачи
        } else if (!getSaveEpic().isEmpty()) {
            kvTaskClient.put("epics", gson.gsonForEpic.toJson(getSaveEpic().values())); // Постим на сервер через
            // клиента эпики
        }
        // Постим на сервер историю через клиента
        String response = ""; // Собирательная строка для всех видом задач
        Set<String> stringForResponse = new LinkedHashSet<>();
        if (!getHistoryManager().getHistory().isEmpty()) {
            for (Task task : getHistoryManager().getHistory()) {
                if (String.valueOf(task.getTYPE()).equals("EPIC")) {
                    String responseEpic = String.join("-----", gson.gsonForEpic.toJson(task));
                    stringForResponse.add(responseEpic);
                } else if (String.valueOf(task.getTYPE()).equals("SUBTASK")) {
                    String responseSubTask = String.join("-----", gson.gsonForSubTask.toJson(task));
                    stringForResponse.add(responseSubTask);
                } else if (String.valueOf(task.getTYPE()).equals("TASK")) {
                    String responseTask = String.join("-----", gson.gsonForTask.toJson(task));
                    stringForResponse.add(responseTask);
                }
            }
            int count = 0;
            for (String line : stringForResponse) {
                line = line + ",\n";
                response = response + line;
                count += 1;
                if (count == stringForResponse.size()) {
                    response = response.substring(0, response.length() - 2); // Удаляем последний символ строки,
                    // ненужную запятую для конкретного сбора строки в формате JSON
                }
            }
            response = "[\n" + response + "]";
            kvTaskClient.put("history", response);
        }
    }

    public HTTPTaskManager loadFromKVServer(String key) {
        TaskManager httpTaskManager = Managers.getDefault();
        KVTaskClient kvtaskClient = new KVTaskClient(urlString);
        System.out.println("Читаем данные с KVServer");
        // Стучимся на сервер через клиента с нужным ключом
        if (key.equals("tasks") || key.equals("subtasks") || key.equals("epics")) {
            forReturnConditionManagerTaskAndSubtaskAndEpic.put(key, kvtaskClient.load(key));
        } else if (key.equals("history")) {
            forReturnConditionManagerHistory.add(kvtaskClient.load(key));
        }
        return (HTTPTaskManager) httpTaskManager; // возвращаем состояние менеджера
    }

    public Map<String, String> getForReturnConditionManagerTaskAndSubtaskAndEpic() {
        return forReturnConditionManagerTaskAndSubtaskAndEpic;
    }

    public void setForReturnConditionManagerTaskAndSubtaskAndEpic(Map<String,
            String> forReturnConditionManagerTaskAndSubtaskAndEpic) {
        this.forReturnConditionManagerTaskAndSubtaskAndEpic = forReturnConditionManagerTaskAndSubtaskAndEpic;
    }

    public List<String> getForReturnConditionManagerHistory() {
        return forReturnConditionManagerHistory;
    }

    public void setForReturnConditionManagerHistory(List<String> forReturnConditionManagerHistory) {
        this.forReturnConditionManagerHistory = forReturnConditionManagerHistory;
    }

    public String getAPY_KEY() {
        return APY_KEY;
    }
}
