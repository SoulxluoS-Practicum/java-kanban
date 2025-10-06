package managers.history;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> nodeMap = new HashMap<>();
    private Node first;
    private Node last;

    public void add(Task task) {
        int id = task.getId();
        remove(id);
        linkTask(task);
        nodeMap.put(id, last);
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    private void linkTask(Task task) {
        Node node = new Node(task);
        if (last != null) {
            node.prev = last;
            last.next = node;
        }
        last = node;
        if (first == null) {
            first = node;
        }
    }

    private ArrayList<Task> getTasks() {
        return nodeMap.values().stream()
            .map(node -> node.task)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    private void removeNode(int id) {
        Node node = nodeMap.remove(id);
        if (node == null) {
            return;
        }

        if (first == last) {
            first = null;
            last = null;
            return;
        }

        if (node == first) {
            first = node.next;
            first.prev = null;
        } else if (node == last) {
            last = node.prev;
            last.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    public Node getFirst() {
        return first;
    }

    public Node getLast() {
        return last;
    }

    public static class Node {

        private final Task task;
        private Node next;
        private Node prev;

        public Task getTask() {
            return task;
        }

        public Node getNext() {
            return next;
        }

        public Node getPrev() {
            return prev;
        }

        private Node(Task data) {
            this.task = data;
            this.next = null;
            this.prev = null;
        }
    }

}