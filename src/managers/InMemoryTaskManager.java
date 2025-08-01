package managers;

import managers.history.HistoryManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    private int counterTaskId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    
    @Override
    public void addTask(Task task) {
        final int taskId = ++counterTaskId;
        task.setId(taskId);
        tasks.put(taskId, task);
    }
    
    @Override
    public void addEpic(Epic epic) {
        final int epicId = ++counterTaskId;
        epic.setId(epicId);
        epics.put(epicId, epic);
    }
    
    @Override
    public void addSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicId());
        if (epic == null) {
            return;
        }
        final int subTaskId = ++counterTaskId;
        subTask.setId(subTaskId);
        subTasks.put(subTaskId, subTask);
        epic.addSubTaskID(subTaskId);
        updateEpicStatus(epic.getId());
    }
    
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }
    
    @Override
    public void updateEpic(Epic epic) {
        tasks.put(epic.getId(), epic);
    }
    
    @Override
    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(subTask.getEpicId());
    }
    
    @Override
    public Task getTask(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }
    
    @Override
    public Epic getEpic(int epicId) {
        Epic epic = epics.get(epicId);
        historyManager.add(epic);
        return epic;
    }
    
    @Override
    public SubTask getSubTask(int subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        historyManager.add(subTask);
        return subTask;
    }
    
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
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
            return null;
        }
        ArrayList<SubTask> subTasksList = new ArrayList<>();
        for (int subTaskId : epic.getSubTaskIds()) {
            subTasksList.add(subTasks.get(subTaskId));
        }
        return subTasksList;
    }
    
    @Override
    public void removeTask(int taskId) {
        tasks.remove(taskId);
    }
    
    @Override
    public void removeEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        for (int subTaskId : epic.getSubTaskIds()) {
            subTasks.remove(subTaskId);
        }
        epics.remove(epicId);
    }
    
    @Override
    public void removeSubTask(int subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        if (subTask == null) {
            return;
        }
        subTasks.remove(subTaskId);
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
    
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic.getSubTaskIds().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        
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
    }
    
}
