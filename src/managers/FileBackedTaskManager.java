package managers;

import managers.exceptions.ManagerLoadException;
import managers.exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        taskManager.addTasksFromFile();
        return taskManager;
    }

    public void addTasksFromFile() {
        try {
            List<String> linesList = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            if (linesList.isEmpty()) {
                return;
            }
            linesList.removeFirst();
            for (String line : linesList) {
                Task task = CSVTaskFormat.fromString(line);
                switch (task.getType()) {
                    case EPIC -> epics.put(task.getId(), (Epic) task);
                    case SUBTASK -> {
                        SubTask subTask = (SubTask) task;
                        Epic epic = epics.get(subTask.getEpicId());
                        if (epic == null) {
                            continue;
                        }
                        int subTaskId = task.getId();
                        subTasks.put(subTaskId, subTask);
                        epic.addSubTaskID(subTaskId);
                    }
                    case TASK -> tasks.put(task.getId(), task);
                }
                taskIdCounter = task.getId();
                sortedTasks.add(task);
            }
            epics.keySet().forEach(this::updateEpicStatus);
        } catch (Exception e) {
            throw new ManagerLoadException("Ошибка при чтении файла: %s".formatted(file.getName()), e);
        }
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(CSVTaskFormat.getHeader());
            writer.newLine();

            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                Task task = entry.getValue();
                writer.write(CSVTaskFormat.toString(task));
                writer.newLine();
            }

            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                Task task = entry.getValue();
                writer.write(CSVTaskFormat.toString(task));
                writer.newLine();
            }

            for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
                Task task = entry.getValue();
                writer.write(CSVTaskFormat.toString(task));
                writer.newLine();
            }
        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка при сохранении файла: %s".formatted(file.getName()), e);
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void removeTask(int taskId) {
        super.removeTask(taskId);
        save();
    }

    @Override
    public void removeEpic(int epicId) {
        super.removeEpic(epicId);
        save();
    }

    @Override
    public void removeSubTask(int subTaskId) {
        super.removeSubTask(subTaskId);
        save();
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubTasks() {
        super.removeSubTasks();
        save();
    }
}
