package tracker.controllers;

import tracker.model.Task;

import java.util.*;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    protected myLinkedList<Task> historyOfTask = new myLinkedList<>(); // список для добавления задач в историю

    @Override
    public void addTask(Task task) {
        if (task != null) {
            if (historyOfTask.getTasks().contains(task)) { // проверяем содержится ли этот элемент уже в истории
                myNode<Task> deleteNode = historyOfTask.getMyNodeTask().get(task.getId()); // Получаю ноду с задачей
                historyOfTask.removeNode(deleteNode); // удаляем прошлый просмотр
                historyOfTask.linkLast(task); // Добавляем новый
            } else { // Если такого элемента в истории не было
                historyOfTask.linkLast(task); // Сразу же добавляем новый элемент в историю просмотров
            }
        }
    }

    @Override
    public void remove(int id) {
        myNode<Task> task = historyOfTask.getMyNodeTask().get(id); // Получаю ноду с задачей
        historyOfTask.removeNode(task);
    }

    @Override
    public List<Task> getHistory() { // метод возвращающий историю, без повторов
        return historyOfTask.getTasks();
    }

    public myLinkedList<Task> getHistoryOfTask() {
        return historyOfTask;
    }

    class myLinkedList<T extends Task> {
        public myNode<Task> head;
        public myNode<Task> tail;
        private int size = 0;
        Map<Integer, myNode<Task>> myNodeTask = new HashMap<>(); // хранит задачу для удаления, ключ id задачи,
        // значение узел (место) задачи

        public Map<Integer, myNode<Task>> getMyNodeTask() {
            return myNodeTask;
        }

        public void linkLast(Task task) { // метод добавляет задачу в конец списка
            if (task != null) {
                if (head == null) { // проверяем пустой ли список
                    myNode<Task> newMyNode = new myNode(null, task, null); // Добавляем новое значение
                    head = newMyNode; // единственное значение оно и есть и голова и хвост одновременно
                    tail = newMyNode;
                    myNodeTask.put(task.getId(), newMyNode); // добавляем значение (ноду-задачу) в мапу
                    this.size += 1; // Увеличиваем размер списка на единицу при добавлении нового элемента
                } else if (head != null) { // если список не пустой и в нем есть хоть одна нода-задача
                    // myNode<Task> prev = tail; // ссылка на предыдущий элемент, это бывший хвост
                    myNode<Task> newMyNode = new myNode(tail, task, null); // Добавляем новое значение
                    tail.setNext(newMyNode); // Предыдущему элементу даем ссылку на новый элемент,
                    // для предыдущего элемента, новый элемент будет next
                    tail = newMyNode; // предыдущий хвос, меняется на новый хвост
                    myNodeTask.put(task.getId(), newMyNode); // добавляем значение (ноду-задачу) в мапу
                    this.size += 1; // Увеличиваем размер списка на единицу при добавлении нового элемента
                }
            }
        }

        public List<Task> getTasks() { // метод собирает задачи в обычный ArrayList и возвращает его
            List<Task> arrayListForReturn = new ArrayList<>(); // лист для задач
            if (head != null) { // если список не пуст
                arrayListForReturn.add(head.data); // то добавляем его голову в ArrayList
                myNode<Task> nextLink = head.getNext(); // Получаем ссылку на следующий элемент
                while (nextLink != null) { // и если следующий элемент есть, добавляем его в ArrayList
                    arrayListForReturn.add(nextLink.data);
                    nextLink = myNodeTask.get(nextLink.data.getId()).getNext(); // Вставляем следующий элемент
                    if (head.getData() == null) {
                        historyOfTask.removeNode(head);
                    }
                }
            }
            return arrayListForReturn;
        }

        public void removeNode(myNode<Task> taskNode) { // метод удаляет задачу (узел) из списка
            myNode<Task> prev = taskNode.getPrevious(); // Предыдущие значение
            myNode<Task> next = taskNode.getNext(); // Следующие значение
            if (!(taskNode.equals(head)) && !(taskNode.equals(tail))) { // если нода в середине списка
                prev.setNext(next); // предыдущему ноду устанавливаю следующие
                // значение, которое было следующее для удаляемого элемента
                next.setPrevious(prev); // Следующему значение, устанавливаем ссылку
                // на предыдущее значение, которое было предыдущим для удаляемого элемента
                myNodeTask.remove(taskNode); // Удаляем из мапы, с сохранением новых связей между нодами
                this.size -= 1; // Уменьшаем размер списка на единицу при удалении нового элемента
            } else if (taskNode.equals(head) && !(taskNode.equals(tail))) { // если удаляемый элемент голова
                head = next; // Следующий элемент после удаляемого становится головой
                head.setPrevious(null); // У головы не должно быть ссылки на предыдйщий элемент, так как его нет

                if (this.size > 1) { // Проверяем, что в процессе выполнения метода осталось больше чем 1 элемент
                    head.setNext(next); // Ссылка на следующий элемент новой головы это ссылка на следующий элемент
                    // после удаляемого нода

                    this.size -= 1; // Уменьшаем размер списка на единицу при удалении нового элемента


                } else { // иначе обнуляем ссылку на следующий элемент
                    head.setNext(null);
                }

                this.size -= 1; // Уменьшаем размер списка на единицу при удалении нового элемента
                myNodeTask.remove(taskNode); // Удаляем из мапы
            } else if (taskNode.equals(tail) && !(taskNode.equals(head))) { // если удаляем хвост
                tail = prev; // Предыдущий элемент становится хвостом
                prev.setNext(null); // У хвоста нет ссылки на следующий элемент
                myNodeTask.remove(taskNode); // Удаляем из мапы
                this.size -= 1; // Уменьшаем размер списка на единицу при удалении нового элемента
            } else if (taskNode.equals(head) && taskNode.equals(tail)) { // если в списке всего один элемент
                taskNode.setPrevious(null); // Удаляем ссылку на предыдущий элемент
                taskNode.setData(null); // удаляем значение
                taskNode.setNext(null); // Удаляем ссылку на следующий элемент
                myNodeTask.remove(taskNode); // Удаляем из мапы
                this.size -= 1; // Уменьшаем размер списка на единицу при удалении нового элемента
            }
        }
    }
}

