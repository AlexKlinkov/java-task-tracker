package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private static File file;
    // Поле класса для указания пути и имени файла (куда будет производится сохранения состояния менеджера)
    Comparator<Integer> comparatorID = new Comparator<>() { // Для правильной сортировки в мапе-дерево
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2; // если отрицательное число, то первый объект меньше
        }
    };
    // Делаем одну, отсортированную мапу по ID, из ТРЁХ типов задач
    Map<Integer, Task> sortedMapWithAllTaskSubTaskAndEpic = new TreeMap<>(comparatorID);

    public FileBackedTasksManager(HistoryManager historyManager, File file) throws IOException {
        super(historyManager);
        this.file = file;
    }

    public void save() { // сохраняет состояние менеджера в файл

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,
                false), Charset.forName("Windows-1251")))) {
            String title = String.join(",", "id", "type", "name", "status", "description", "epic");
            writer.write(title);
            writer.write("\n");
            sortedMapWithAllTaskSubTaskAndEpic.putAll(getSaveTask());
            sortedMapWithAllTaskSubTaskAndEpic.putAll(getSaveSubTask());
            sortedMapWithAllTaskSubTaskAndEpic.putAll(getSaveEpic());

            for (Task task : sortedMapWithAllTaskSubTaskAndEpic.values()) {
                writer.write(toString(task));
                writer.write("\n");
            }

            writer.write("\n");
            writer.write(toString(historyManager));// Сохраняет историю в файл
        } catch (IOException exception) {
            throw new ManagerSaveException("НЕУДАЛОСЬ СОХРАНИТЬ ДАННЫЕ В ФАЙЛ", exception.getCause());
        }
    }

    public String toString(Task task) { // метод сохранения задач в строку
        if (task != null) {
            String[] stringTask = new String[7]; // Массив с полями задачи (id,type,name,status,description,epic)
            String eventually = ""; // Итоговая строка с представлением задачи
            stringTask[0] = String.valueOf(task.getId()); // строковое представление ID задачи
            stringTask[1] = String.valueOf(task.getTYPE()); // строковое представление ТИПА задачи
            stringTask[2] = String.valueOf(task.getName()); // строковое представление ИМЕНИ задачи
            stringTask[3] = String.valueOf(task.getStatus()); // строковое представление СТАТУСА задачи
            stringTask[4] = String.valueOf(task.getDescription()); // строковое представление ОПИСАНИЯ задачи
            stringTask[5] = String.valueOf(task.getTYPE()); // строковое представление КЛАССА задачи
            if (task instanceof Subtask) {
                return eventually.join(",", stringTask[0], stringTask[1], stringTask[2], stringTask[3],
                        stringTask[4], stringTask[5] + stringTask[0],
                        String.valueOf(((Subtask) task).getEpic().getId()));
            } else {
                return eventually.join(",", stringTask[0], stringTask[1], stringTask[2], stringTask[3],
                        stringTask[4], stringTask[5] + stringTask[0], "");
            }
        } else {
            return "";
        }
    }

    public static String toString(HistoryManager manager) throws IOException { // Метод для сохранения менеджера истории
        // Переносим историю в файл для сохранения состояния менеджера
        String eventually = "";
        for (int i = 0; i < manager.getHistory().size(); i++) {
            if (manager.getHistory().get(i) != null) {
                String recordInFile = String.valueOf(manager.getHistory().get(i).getId());
                eventually = String.join(",", eventually, recordInFile);
            }
        }
        return eventually.replaceFirst(",", "");
    }

    public static FileBackedTasksManager loadFromFile(File file) { // Метод восстанавливает данные
        // менеджера из файла при запуске программы
        // для возвращения с задачами и историей из файла
        try (Scanner reader = new Scanner(new BufferedReader(new InputStreamReader(
                new FileInputStream(file), "Windows-1251")))) {
            List<String> allLineOurFile = new ArrayList<>(); // Список со всеми строками файла
            HistoryManager historyManager = new InMemoryHistoryManager();
            FileBackedTasksManager backedTasksManager = new FileBackedTasksManager(historyManager, file); // новый
                                                                                                            // менеджер
            // восстанавливаем задачи для нашего менеджера из файла
            //сначала все Эпики, иначе некорректно восстановятся подзадачи
            while (reader.hasNext()) {
                String dataOfTask = reader.nextLine(); // получаем строчку с полями задачи
                allLineOurFile.add(dataOfTask); // Сохраняем все строки в файл, для повторного прохождения
                String[] typeOfTask = dataOfTask.split(",");
                if (typeOfTask.length > 1) {
                    if (typeOfTask[1].equals("EPIC")) {
                        backedTasksManager.saveEpic.put(Integer.parseInt(typeOfTask[0]), (Epic) fromString(dataOfTask));
                    }
                }
            }
            // Теперь задачи и подзадачи восстанавливаем
            for (String line : allLineOurFile) {
                String[] split = line.split(",");
                if (line.length() > 1) {
                    if (split[1].equals("SUBTASK")) {
                        backedTasksManager.saveSubTask.put(Integer.parseInt(split[0]), (Subtask) fromString(line));
                    } else if (split[1].equals("TASK")) {
                        backedTasksManager.saveTask.put(Integer.parseInt(split[0]), (Task) fromString(line));
                    }
                }
            }
            // Теперь восстанавливаем историю просмотров из файла
            for (Integer id : fromStringHistory(allLineOurFile.get(allLineOurFile.size() - 1))) { // Берем последнюю
                // строку заполненую из файла с нашими ID

                if (backedTasksManager.getTaskById(id) != null) {
                    backedTasksManager.historyManager.addTask(backedTasksManager.getTaskById(id));
                } else if (backedTasksManager.getSubTaskById(id) != null) {
                    backedTasksManager.historyManager.addTask(backedTasksManager.getSubTaskById(id));
                } else {
                    backedTasksManager.historyManager.addTask(backedTasksManager.getEpicById(id));
                }
            }
            return backedTasksManager;

        } catch (FileNotFoundException e) {
            System.out.println("Файл " + file.getName() + " не найден");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static Task fromString(String value) { // метод создания из строки задачи
        if (!value.isEmpty()) {
            String[] attributeOfTask = value.split(","); // Получаем массив состоящие из полей задачи

            if (attributeOfTask[1].equals("SUBTASK")) {
                return new Subtask(attributeOfTask[2], attributeOfTask[4], Status.valueOf(attributeOfTask[3]),
                        Integer.parseInt(attributeOfTask[6]), Integer.parseInt(attributeOfTask[0]));
            } else if (attributeOfTask[1].equals("EPIC")) {
                return new Epic(attributeOfTask[2], attributeOfTask[4], Status.valueOf(attributeOfTask[3]),
                        Integer.parseInt(attributeOfTask[0]));
            } else {
                return new Task(attributeOfTask[2], attributeOfTask[4], Status.valueOf(attributeOfTask[3]),
                        Integer.parseInt(attributeOfTask[0]));
            }
        } else {
            return null;
        }
    }

    public static List<Integer> fromStringHistory(String value) { // Метод для восстановления менеджера истории
        if (value != null) {
            List<Integer> idOfHistory = new ArrayList<>();
            String[] split = value.split(","); // По элементно получаем ID задач
            for (int i = 0; i < split.length; i++) {
                idOfHistory.add(Integer.parseInt(split[i]));
            }
            return idOfHistory;
        } else {
            return null;
        }
    }

    @Override
    public void deleteAllTasks() throws IOException {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks() throws IOException {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllEpic() throws IOException {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void updateTask(Task task) throws IOException {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(Subtask subtask) throws IOException {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) throws IOException {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(int id) throws IOException {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) throws IOException {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) throws IOException {
        super.deleteEpicById(id);
        save();
    }

    public static void main(String[] args) throws IOException {
        ///// ТЕСТИМ ЗАПИСЬ В ФАЙЛ И ПРОЧТЕНИЕ ИЗ ФАЙЛА С ВЫВОДОМ В КОНСОЛЬ
        // 1. Создаем задачи

        FileBackedTasksManager backedTasksManager =
                Managers.getDefaultBackedTaskManager("OUTPUT.csv"); // создали менеджера отве-
        // чающего за сохранения состояния в файл
        backedTasksManager.createTask(new Task("Поздравить друга с днем рождения",
                "Подарить подарок", Status.NEW));
        backedTasksManager.createTask(new Epic("Вылечиться", "Посетить врачей", Status.NEW));
        backedTasksManager.createTask(new Subtask("Посетить врача №1", "Записаться на прием",
                Status.IN_PROGRESS, backedTasksManager.getEpicById(2), 2));
        backedTasksManager.createTask(new Subtask("Посетить врача №2", "Записаться на прием",
                Status.DONE, backedTasksManager.getEpicById(2), 2));
        // 2. Добавляем историю
        backedTasksManager.getTaskById(1);
        backedTasksManager.getEpicById(2);

        // 3. Проверяем как метод записывает поля задачи в файл
        backedTasksManager.save();

        // 4. Проверяем как метод восстанавливает в мапы задачи, подзадачи и эпики
        File newFile = new File("OUTPUT.csv");
        System.out.println();
        System.out.println("Выводим задачи из файла в консоль");
        System.out.println(loadFromFile(newFile).getSaveTask()); // Задачи
        System.out.println(loadFromFile(newFile).getSaveEpic()); // Подзадачи
        System.out.println(loadFromFile(newFile).getSaveSubTask()); // Эпики
        System.out.println();

        // 5. Проверяем как метод восстанавливает историю из файла
        System.out.println("Выводим в консоль историю просмотров из файла");
        System.out.println(loadFromFile(newFile).getHistoryManager().getHistory());
    }
}
