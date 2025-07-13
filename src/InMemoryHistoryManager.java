import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager, TaskManager {
    private Node<Task> head;
    private Node<Task> tail;
    private final Map<Integer, Node<Task>> nodeMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) return;
        remove(task.getId());
        Node newNode = new Node<>(task);
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        nodeMap.put(task.getId(), newNode);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            history.add(current.data);
            current = current.next;
        }
        return history;
    }

    @Override
    public List<Task> getAllTasks() {
        return List.of();
    }

    @Override
    public List<Task> getTasks() {
        return List.of();
    }

    @Override
    public List<SubTask> getSubTasks() {
        return List.of();
    }

    @Override
    public List<Epic> getEpics() {
        return List.of();
    }

    @Override
    public void deleteAllTasks() {

    }

    @Override
    public Task getTaskById(int id) {
        return null;
    }

    @Override
    public Epic getEpicById(int id) {
        return null;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        return null;
    }

    @Override
    public void deleteTaskById(int id) {

    }

    @Override
    public void deleteSubtaskById(int id) {

    }

    @Override
    public void deleteEpicById(int id) {

    }

    @Override
    public void addTask(Task task) {

    }

    @Override
    public void addEpic(Epic epic) {

    }

    @Override
    public void addSubTask(SubTask subtask) {

    }

    @Override
    public void updateTask(Task task) {

    }

    @Override
    public void updateSubTask(SubTask subtask) {

    }

    @Override
    public void updateEpic(Epic epic) {

    }

    @Override
    public List<SubTask> getAllSubTasksInEpic(Epic epic) {
        return List.of();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return List.of();
    }

    @Override
    public void remove(int id) {
        Node<Task> node = nodeMap.remove(id);
        if (node == null) return;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }

    private static class Node<T> {
        T data;
        Node<T> prev;
        Node<T> next;

        public Node(T data) {
            this.data = data;
        }
    }
}


