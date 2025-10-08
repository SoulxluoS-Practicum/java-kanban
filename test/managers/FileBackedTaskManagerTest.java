package managers;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private static final Path path = Path.of("test_task_storage.txt");

    @Override
    FileBackedTaskManager getTaskManager() {
        try {
            if (Files.exists(path)) {
                Files.delete(path);
            }
            Files.createFile(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return FileBackedTaskManager.loadFromFile(path.toFile());
    }

    @Test
    void loadEmptyFile() {
        assertTrue(taskManager.tasks.isEmpty(), "Список задач не пуст после загрузки пустого файла!");
        taskManager.save();
        assertTrue(taskManager.tasks.isEmpty(), "Список задач не пуст после сохранения пустого файла!");
    }

    @Test
    void addTasksSaveLoad() {
        Task task = new Task("Task-1 Name", "Task-1 Description");
        taskManager.addTask(task);
        Epic epic = new Epic("Epic-1 Name", "Epic-1 Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask-1 Name", "SubTask-1 Description", epic.getId());
        taskManager.addSubTask(subTask);
        taskManager.tasks.clear();
        taskManager.subTasks.clear();
        taskManager.epics.clear();
        taskManager.addTasksFromFile();
        assertFalse(taskManager.tasks.size() != 1, "Список загруженных Task не совпадает с сохранёнными: %s/1".formatted(taskManager.tasks.size()));
        assertFalse(taskManager.epics.size() != 1, "Список загруженных Epic не совпадает с сохранёнными: %s/1".formatted(taskManager.epics.size()));
        assertFalse(taskManager.subTasks.size() != 1, "Список загруженных SubTask не совпадает с сохранёнными: %s/1".formatted(taskManager.subTasks.size()));
    }

    @Test
    void testExceptions() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), StandardCharsets.UTF_8))) {
            writer.write(CSVTaskFormat.getHeader());
            writer.newLine();
            writer.write("5,UNKNOWN,Task,UNKNOWN,description,null,o");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertThrows(ManagerLoadException.class,
            () -> taskManager.addTasksFromFile(),
            "Ошибка при чтении файла: %s".formatted(path.toFile().getName()));
        assertThrows(ManagerSaveException.class,
            () -> {
                Task brokenTask = new Task(null, null);
                taskManager.addTask(brokenTask);
            },
            "Ошибка при сохранении файла: %s".formatted(path.toFile().getName()));
    }
}