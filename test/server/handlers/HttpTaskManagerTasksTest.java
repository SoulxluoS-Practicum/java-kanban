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

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task-1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        Task task = new Task("Task-1", "Task-1 Description", LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task);
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = sendRequest(URL, builder -> builder.POST(HttpRequest.BodyPublishers.ofString(taskJson)));
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task-1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void deleteTask() throws IOException, InterruptedException {
        Task task = new Task("Task-1", "Task-1 Description", LocalDateTime.now(), Duration.ofMinutes(5));
        int taskId = taskManager.addTask(task);

        HttpResponse<String> response = sendRequest(URL + taskId, HttpRequest.Builder::DELETE);
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        Task task = new Task("Task-1", "Task-1 Description", LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task);

        HttpResponse<String> response = sendRequest(URL, HttpRequest.Builder::GET);
        assertEquals(200, response.statusCode());

        List<Task> tasksResponse = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());
        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksResponse, "Задачи не возвращаются");
        assertEquals(tasksResponse, tasksFromManager, "Задачи на сервере и из запроса клиента не равны");
    }

    @Test
    void getTaskByID() throws IOException, InterruptedException {
        Task task = new Task("Task-1", "Task-1 Description", LocalDateTime.now(), Duration.ofMinutes(5));
        int taskId = taskManager.addTask(task);

        HttpResponse<String> response = sendRequest(URL + taskId, HttpRequest.Builder::GET);
        assertEquals(200, response.statusCode());

        Task taskResponse = gson.fromJson(response.body(), Task.class);
        Task taskFromManager = taskManager.getTask(taskId);

        assertNotNull(taskResponse, "Задачи не возвращаются");
        assertEquals(taskResponse, taskFromManager, "Задача на сервере и из запроса клиента не равны");
    }

}