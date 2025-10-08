package managers;

import managers.history.HistoryManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.TreeSet;

public interface TaskManager {
    int addTask(Task task);

    int addEpic(Epic epic);

    int addSubTask(SubTask subTask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    Task getTask(int taskId);

    Epic getEpic(int epicId);

    SubTask getSubTask(int subTaskId);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<SubTask> getSubTasks();

    ArrayList<SubTask> getEpicSubtasks(int epicId);

    TreeSet<Task> getPrioritizedTasks();

    void removeTask(int taskId);

    void removeEpic(int epicId);

    void removeSubTask(int subTaskId);

    void removeTasks();

    void removeEpics();

    void removeSubTasks();

    boolean hasOverlaps(Task task);

    HistoryManager getHistoryManager();
}
