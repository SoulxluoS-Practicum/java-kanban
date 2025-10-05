package managers;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {

    @Test
    void loadEmptyFile() throws IOException {
        Path path = Path.of("./test_task_storage.txt");
        if (Files.exists(path)) {
            Files.delete(path);
        }
        Files.createFile(path);
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(path.toFile());
        assertTrue(taskManager.tasks.isEmpty(), "Список задач не пуст после загрузки пустого файла!");
        taskManager.save();
        assertTrue(taskManager.tasks.isEmpty(), "Список задач не пуст после сохранения пустого файла!");
    }

    @Test
    void addTasksSaveLoad() throws IOException {
        Path path = Path.of("./test_task_storage.txt");
        if (Files.exists(path)) {
            Files.delete(path);
        }
        Files.createFile(path);
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(path.toFile());
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
}