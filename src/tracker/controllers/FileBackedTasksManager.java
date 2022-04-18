package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
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

    public FileBackedTasksManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public void save() { // сохраняет состояние менеджера в файл
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,
                false), Charset.forName("Windows-1251")))) {
            String title = String.join(",", "id", "type", "name", "status", "description", "epic",
                    "startOfTask", "duration");
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

            if (sortedMapWithAllTaskSubTaskAndEpic.isEmpty()) { // если мапа со всеми задачами пуста, то и
                // файл должен быть пустым
                writer.close(); // Закрываем поток
                Files.deleteIfExists(file.toPath()); // Удаляем файл раз в него нечего записывать
                throw new ManagerSaveException("НЕТ ДАННЫХ ДЛЯ СОХРАНЕНИЯ В ФАЙЛ");
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("НЕУДАЛОСЬ СОХРАНИТЬ ДАННЫЕ В ФАЙЛ", exception.getCause());
        }
    }

    public String toString(Task task) { // метод сохранения задач в строку
        if (task != null) {
            String[] stringTask = new String[9]; // Массив с полями задачи (id,type,name,status,description,epic)
            String eventually = ""; // Итоговая строка с представлением задачи
            stringTask[0] = String.valueOf(task.getId()); // строковое представление ID задачи
            stringTask[1] = String.valueOf(task.getTYPE()); // строковое представление ТИПА задачи
            stringTask[2] = String.valueOf(task.getName()); // строковое представление ИМЕНИ задачи
            stringTask[3] = String.valueOf(task.getStatus()); // строковое представление СТАТУСА задачи
            stringTask[4] = String.valueOf(task.getDescription()); // строковое представление ОПИСАНИЯ задачи
            stringTask[5] = String.valueOf(task.getTYPE()); // строковое представление КЛАССА задачи
            stringTask[6] = String.valueOf(task.getStartTime()); // строковое представление СТАРТА задачи
            stringTask[7] = String.valueOf(task.getDuration()); // строковое представление ПРОДОЛЖИТЕЛЬНОСТИ задачи
            if (task instanceof Subtask) {
                return eventually.join(",", stringTask[0], stringTask[1], stringTask[2], stringTask[3],
                        stringTask[4], stringTask[5] + stringTask[0],
                        String.valueOf(((Subtask) task).getEpic().getId()), stringTask[6],
                        stringTask[7]);
            } else {
                return eventually.join(",", stringTask[0], stringTask[1], stringTask[2], stringTask[3],
                        stringTask[4], stringTask[5] + stringTask[0], " ", stringTask[6],
                        stringTask[7]);
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
                        Epic epic = (Epic) fromString(dataOfTask);
                        backedTasksManager.saveEpic.put(Integer.parseInt(typeOfTask[0]), epic);
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
                        backedTasksManager.saveTask.put(Integer.parseInt(split[0]), fromString(line));
                    }
                }
            }
            // Теперь восстанавливаем историю просмотров из файла
            if (fromStringHistory(allLineOurFile.get(allLineOurFile.size() - 1)) != null) {
                for (Integer id : fromStringHistory(allLineOurFile.get(allLineOurFile.size() - 1))) { // Берем последнюю
                    // строку заполненую из файла с нашими ID

                    if (backedTasksManager.getSaveTask().get(id) != null) {
                        backedTasksManager.historyManager.addTask(backedTasksManager.getTaskById(id));
                    } else if (backedTasksManager.getSaveSubTask().get(id) != null) {
                        backedTasksManager.historyManager.addTask(backedTasksManager.getSubTaskById(id));
                    } else {
                        backedTasksManager.historyManager.addTask(backedTasksManager.getEpicById(id));
                    }
                }
            }
            return backedTasksManager;

        } catch (FileNotFoundException e) {
            System.out.println("Файл " + file.getName() + " не найден");
            return null;
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static Task fromString(String value) { // метод создания из строки задачи
        if (!value.isEmpty()) {
            String[] attributeOfTask = value.split(","); // Получаем массив состоящие из полей задачи

            if (attributeOfTask[1].equals("SUBTASK")) {
                return new Subtask(attributeOfTask[2], attributeOfTask[4], Status.valueOf(attributeOfTask[3]),
                        Integer.parseInt(attributeOfTask[6]), Integer.parseInt(attributeOfTask[0]),
                        LocalDateTime.parse(attributeOfTask[7]), Duration.parse(attributeOfTask[8]));
            } else if (attributeOfTask[1].equals("EPIC")) {
                return new Epic(attributeOfTask[2], attributeOfTask[4], Status.valueOf(attributeOfTask[3]),
                        Integer.parseInt(attributeOfTask[0]), LocalDateTime.parse(attributeOfTask[7]),
                        Duration.parse(attributeOfTask[8]));
            } else {
                return new Task(attributeOfTask[2], attributeOfTask[4], Status.valueOf(attributeOfTask[3]),
                        Integer.parseInt(attributeOfTask[0]), LocalDateTime.parse(attributeOfTask[7]),
                        Duration.parse(attributeOfTask[8]));
            }
        } else {
            return null;
        }
    }

    public static List<Integer> fromStringHistory(String value) { // Метод для восстановления менеджера истории
        if (value != null) {
            List<Integer> idOfHistory = new ArrayList<>();
            String[] split = value.split(","); // По элементно получаем ID задач

            boolean allValueIsNumbers = true; // На случай, если истории в файле нет
            try {
                for (int i = 0; i < split.length; i++) {
                    Integer.parseInt(split[i]);
                }
            } catch (NumberFormatException ex) {
                allValueIsNumbers = false;
            }

            if (allValueIsNumbers) {
                for (int i = 0; i < split.length; i++) {
                    idOfHistory.add(Integer.parseInt(split[i]));
                }
                return idOfHistory;
            }
        }
        return null;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        if (!sortedMapWithAllTaskSubTaskAndEpic.isEmpty()) {
            save();
        }
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        if (!sortedMapWithAllTaskSubTaskAndEpic.isEmpty()) {
            save();
        }
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        if (!sortedMapWithAllTaskSubTaskAndEpic.isEmpty()) {
            save();
        }
    }

    @Override
    public void updateTask(Task task) throws Exception {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(Subtask subtask) throws Exception {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) throws Exception {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        if (!sortedMapWithAllTaskSubTaskAndEpic.isEmpty()) {
            save();
        }
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        if (!sortedMapWithAllTaskSubTaskAndEpic.isEmpty()) {
            save();
        }
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        if (!sortedMapWithAllTaskSubTaskAndEpic.isEmpty()) {
            save();
        }
    }

    public static void main(String[] args) throws IOException {
        ///// ТЕСТИМ ЗАПИСЬ В ФАЙЛ И ПРОЧТЕНИЕ ИЗ ФАЙЛА С ВЫВОДОМ В КОНСОЛЬ

        LocalDateTime startTime = LocalDateTime.of(2022, 04, 01, 12, 0);
        Duration duration = Duration.ofHours(2);
        LocalDateTime endTime = startTime.plus(duration);
        // 1. Создаем задачи

        File file = new File("OUTPUT.csv");
        HistoryManager historyManager = Managers.getDefaultHistory();
        FileBackedTasksManager backedTasksManager = new FileBackedTasksManager(historyManager, file);
        // создали менеджера отвечающего за сохранения состояния в файл
        backedTasksManager.createTask(new Task("Поздравить друга с днем рождения",
                "Подарить подарок", Status.NEW, startTime, duration));
        backedTasksManager.createTask(new Epic("Вылечиться", "Посетить врачей", Status.NEW,
                LocalDateTime.of(2020,11,12,12,0),
                Duration.ofHours(3)));
        backedTasksManager.createTask(new Subtask("Посетить врача №1", "Записаться на прием",
                Status.IN_PROGRESS, backedTasksManager.getEpicById(2), 2,
                LocalDateTime.of(2021,12,1,00,0), Duration.ofHours(4)));
        backedTasksManager.createTask(new Subtask("Посетить врача №2", "Записаться на прием",
                Status.DONE, backedTasksManager.getEpicById(2), 2,
                LocalDateTime.of(2019,12,1,5,0), Duration.ofHours(12)));
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
