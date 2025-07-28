import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int counterTaskId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    
    public void addTask(Task task) {
        final int taskId = ++counterTaskId;
        task.setId(taskId);
        tasks.put(taskId, task);
    }
    
    public void addEpic(Epic epic) {
        final int epicId = ++counterTaskId;
        epic.setId(epicId);
        epics.put(epicId, epic);
    }
    
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
    
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }
    
    public void updateEpic(Epic epic) {
        tasks.put(epic.getId(), epic);
    }
    
    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(subTask.getEpicId());
    }
    
    public Task getTask(int taskId) {
        return tasks.get(taskId);
    }
    
    public Epic getEpic(int epicId) {
        return epics.get(epicId);
    }
    
    public SubTask getSubTask(int subTaskId) {
        return subTasks.get(subTaskId);
    }
    
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }
    
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }
    
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }
    
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
    
    public void removeTask(int taskId) {
        tasks.remove(taskId);
    }
    
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
    
    public void removeTasks() {
        for (int taskId : tasks.keySet()) {
            removeTask(taskId);
        }
    }
    
    public void removeEpics() {
        for (int epicId : new ArrayList<>(epics.keySet())) {
            removeEpic(epicId);
        }
    }
    
    public void removeSubTasks() {
        for (int taskId : subTasks.keySet()) {
            removeSubTask(taskId);
        }
    }
    
    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic.getSubTaskIds().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        
        int statusNew = 0;
        int statusDone = 0;
        for (int subTaskId : epic.getSubTaskIds()) {
            TaskStatus taskStatus = subTasks.get(subTaskId).getStatus();
            if (taskStatus == TaskStatus.DONE) {
                statusDone++;
            } else if (taskStatus == TaskStatus.NEW) {
                statusNew++;
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
