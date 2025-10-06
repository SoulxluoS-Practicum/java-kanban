package managers;

import exceptions.NotFoundException;
import exceptions.TaskOverlapException;
import managers.history.HistoryManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    protected final TreeSet<Task> sortedTasks = new TreeSet<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int taskIdCounter = 0;

    @Override
    public int addTask(Task task) {
        if (hasOverlaps(task)) {
            throw new TaskOverlapException("Задача %s не добавлена - пересечение по времени с другой задачей!".formatted(task.getName()));
        }
        final int taskId = ++taskIdCounter;
        task.setId(taskId);
        tasks.put(taskId, task);
        if (task.getStartTime().isPresent()) {
            sortedTasks.add(task);
        }
        return taskId;
    }

    @Override
    public int addEpic(Epic epic) {
        final int epicId = ++taskIdCounter;
        epic.setId(epicId);
        epics.put(epicId, epic);
        if (epic.getStartTime().isPresent()) {
            sortedTasks.add(epic);
        }
        return epicId;
    }

    @Override
    public int addSubTask(SubTask subTask) {
        if (hasOverlaps(subTask)) {
            throw new TaskOverlapException("Подзадача %s не добавлена - пересечение по времени с другой задачей!".formatted(subTask.getName()));
        }
        Epic epic = epics.get(subTask.getEpicId());
        if (epic == null) {
            throw new NotFoundException("Подзадача %s не добавлена - не найдена главная задача!".formatted(subTask.getName()));
        }
        final int subTaskId = ++taskIdCounter;
        subTask.setId(subTaskId);
        subTasks.put(subTaskId, subTask);
        epic.addSubTaskID(subTaskId);
        updateEpicStatus(epic.getId());
        if (subTask.getStartTime().isPresent()) {
            sortedTasks.add(subTask);
        }
        return subTaskId;
    }

    @Override
    public void updateTask(Task task) {
        if (hasOverlaps(task)) {
            throw new TaskOverlapException("Задача %s не обновлена - пересечение по времени с другой задачей!".formatted(task.getName()));
        }
        tasks.put(task.getId(), task);
        if (task.getStartTime().isPresent()) {
            sortedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        tasks.put(epic.getId(), epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (hasOverlaps(subTask)) {
            throw new TaskOverlapException("Подзадача %s не обновлена - пересечение по времени с другой задачей!".formatted(subTask.getName()));
        }
        subTasks.put(subTask.getId(), subTask);
        if (subTask.getStartTime().isPresent()) {
            sortedTasks.add(subTask);
        }
        updateEpicStatus(subTask.getEpicId());
    }

    @Override
    public Task getTask(int taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new NotFoundException("Задача %s не найдена!".formatted(taskId));
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Задача %s не найдена!".formatted(epicId));
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTask(int subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        if (subTask == null) {
            throw new NotFoundException("Задача %s не найдена!".formatted(subTask));
        }
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return sortedTasks;
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public ArrayList<SubTask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Задача %s не найдена!".formatted(epicId));
        }
        return epic.getSubTaskIds().stream()
            .map(subTasks::get)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void removeTask(int taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            return;
        }
        tasks.remove(taskId);
        historyManager.remove(taskId);
        if (task.getStartTime().isPresent()) {
            sortedTasks.remove(task);
        }
    }

    @Override
    public void removeEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        for (int subTaskId : epic.getSubTaskIds()) {
            SubTask subTask = subTasks.get(subTaskId);
            if (subTask == null) {
                continue;
            }
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
            sortedTasks.remove(subTask);
        }
        epics.remove(epicId);
        historyManager.remove(epicId);
        if (epic.getStartTime().isPresent()) {
            sortedTasks.remove(epic);
        }
    }

    @Override
    public void removeSubTask(int subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        if (subTask == null) {
            return;
        }
        subTasks.remove(subTaskId);
        historyManager.remove(subTaskId);
        if (subTask.getStartTime().isPresent()) {
            sortedTasks.remove(subTask);
        }
        Epic epic = epics.get(subTask.getEpicId());
        epic.removeSubTaskID(subTaskId);
        updateEpicStatus(subTask.getEpicId());
    }

    @Override
    public void removeTasks() {
        for (int taskId : tasks.keySet()) {
            removeTask(taskId);
        }
    }

    @Override
    public void removeEpics() {
        for (int epicId : new ArrayList<>(epics.keySet())) {
            removeEpic(epicId);
        }
    }

    @Override
    public void removeSubTasks() {
        for (int taskId : subTasks.keySet()) {
            removeSubTask(taskId);
        }
    }

    protected void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic.getSubTaskIds().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            if (epic.getStartTime().isPresent()) {
                sortedTasks.remove(epic);
            }
            epic.updateDateTimes(Collections.emptyList());
            return;
        }
        epic.updateDateTimes(epic.getSubTaskIds().stream().map(subTasks::get).toList());

        int statusNew = 0;
        int statusDone = 0;
        for (int subTaskId : epic.getSubTaskIds()) {
            TaskStatus taskStatus = subTasks.get(subTaskId).getStatus();
            switch (taskStatus) {
                case DONE -> statusDone++;
                case NEW -> statusNew++;
            }
        }
        if (statusNew == epic.getSubTaskIds().size()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (statusDone == epic.getSubTaskIds().size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
        sortedTasks.add(epic);
    }

    public boolean hasOverlaps(Task task) {
        return sortedTasks.stream().anyMatch(task::hasOverlapWith);
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

}
