package server.handlers;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskManagerTasksTest extends HttpTasksTest {

    private static final String URL = "http://localhost:8080/tasks/";

    HttpTaskManagerTasksTest() throws IOException {
    }

    @Test
    void addTask() throws IOException, InterruptedException {
        Task task = new Task("Task-1", "Task-1 Description", LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = sendRequest(URL, builder -> builder.POST(HttpRequest.BodyPublishers.ofString(taskJson)));
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();
        
        assertNotNull(tasksFromManager, "Список Task задач у сервера равен null");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество Task задач после добавления: %s != 1".formatted(tasksFromManager.size()));
        String taskName = tasksFromManager.getFirst().getName();
        assertEquals("Task-1", taskName, "Некорректное имя %s созданной Task задачи".formatted(taskName));
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        Task task = new Task("Task-1", "Task-1 Description", LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task);
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = sendRequest(URL, builder -> builder.POST(HttpRequest.BodyPublishers.ofString(taskJson)));
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();
        
        assertNotNull(tasksFromManager, "Список Task задач у сервера равен null");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество Task задач после обновления: %s != 1".formatted(tasksFromManager.size()));
        String taskName = tasksFromManager.getFirst().getName();
        assertEquals("Task-1", taskName, "Некорректное имя %s обновлённой Task задачи".formatted(taskName));
    }

    @Test
    void deleteTask() throws IOException, InterruptedException {
        Task task = new Task("Task-1", "Task-1 Description", LocalDateTime.now(), Duration.ofMinutes(5));
        int taskId = taskManager.addTask(task);

        HttpResponse<String> response = sendRequest(URL + taskId, HttpRequest.Builder::DELETE);
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();
        
        assertNotNull(tasksFromManager, "Список Task задач у сервера равен null");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество Task задач после удаления: %s != 0".formatted(tasksFromManager.size()));
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        Task task = new Task("Task-1", "Task-1 Description", LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task);
        Task task2 = new Task("Task-2", "Task-2 Description", LocalDateTime.now().minusHours(1), Duration.ofMinutes(5));
        taskManager.addTask(task2);

        HttpResponse<String> response = sendRequest(URL, HttpRequest.Builder::GET);
        assertEquals(200, response.statusCode());

        List<Task> tasksResponse = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());
        List<Task> tasksFromManager = taskManager.getTasks();
        
        assertNotNull(tasksResponse, "Список Task задач у клиента равен null");
        assertNotNull(tasksFromManager, "Список Task задач у сервера равен null");
        assertEquals(tasksResponse, tasksFromManager, "Списки Task задач у сервера и у клиента не равны");
    }

    @Test
    void getTaskByID() throws IOException, InterruptedException {
        Task task = new Task("Task-1", "Task-1 Description", LocalDateTime.now(), Duration.ofMinutes(5));
        int taskId = taskManager.addTask(task);

        HttpResponse<String> response = sendRequest(URL + taskId, HttpRequest.Builder::GET);
        assertEquals(200, response.statusCode());

        Task taskResponse = gson.fromJson(response.body(), Task.class);
        Task taskFromManager = taskManager.getTask(taskId);

        assertNotNull(taskResponse, "Задача у клиента равна null");
        assertEquals(taskResponse, taskFromManager, "Task задача у сервера и у клиента не равны");
    }

}