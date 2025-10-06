package server.handlers;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpSubTaskManagerTasksTest extends HttpTasksTest {

    private static final String URL = "http://localhost:8080/subtasks/";

    HttpSubTaskManagerTasksTest() throws IOException {
    }

    @Test
    void addTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic-1", "Epic-1 Description");
        taskManager.addEpic(epic);
        SubTask task = new SubTask("SubTask-1", "SubTask-1 Description", LocalDateTime.now(), Duration.ofMinutes(5), epic.getId());
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = sendRequest(URL, builder -> builder.POST(HttpRequest.BodyPublishers.ofString(taskJson)));
        assertEquals(200, response.statusCode());

        List<SubTask> tasksFromManager = taskManager.getSubTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("SubTask-1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic-1", "Epic-1 Description");
        taskManager.addEpic(epic);
        SubTask task = new SubTask("SubTask-1", "SubTask-1 Description", LocalDateTime.now(), Duration.ofMinutes(5), epic.getId());
        taskManager.addSubTask(task);
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = sendRequest(URL, builder -> builder.POST(HttpRequest.BodyPublishers.ofString(taskJson)));
        assertEquals(201, response.statusCode());

        List<SubTask> tasksFromManager = taskManager.getSubTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("SubTask-1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void deleteTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic-1", "Epic-1 Description");
        taskManager.addEpic(epic);
        SubTask task = new SubTask("SubTask-1", "SubTask-1 Description", LocalDateTime.now(), Duration.ofMinutes(5), epic.getId());
        int taskId = taskManager.addSubTask(task);

        HttpResponse<String> response = sendRequest(URL + taskId, HttpRequest.Builder::DELETE);
        assertEquals(201, response.statusCode());

        List<SubTask> tasksFromManager = taskManager.getSubTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic-1", "Epic-1 Description");
        taskManager.addEpic(epic);
        SubTask task = new SubTask("SubTask-1", "SubTask-1 Description", LocalDateTime.now(), Duration.ofMinutes(5), epic.getId());
        taskManager.addSubTask(task);

        HttpResponse<String> response = sendRequest(URL, HttpRequest.Builder::GET);
        assertEquals(200, response.statusCode());

        List<SubTask> tasksResponse = gson.fromJson(response.body(), new TypeToken<List<SubTask>>() {}.getType());
        List<SubTask> tasksFromManager = taskManager.getSubTasks();

        assertNotNull(tasksResponse, "Задачи не возвращаются");
        assertEquals(tasksResponse, tasksFromManager, "Задачи на сервере и из запроса клиента не равны");
    }

    @Test
    void getTaskByID() throws IOException, InterruptedException {Epic epic = new Epic("Epic-1", "Epic-1 Description");
        taskManager.addEpic(epic);
        SubTask task = new SubTask("SubTask-1", "SubTask-1 Description", LocalDateTime.now(), Duration.ofMinutes(5), epic.getId());
        int taskId = taskManager.addSubTask(task);

        HttpResponse<String> response = sendRequest(URL + taskId, HttpRequest.Builder::GET);
        assertEquals(200, response.statusCode());

        SubTask taskResponse = gson.fromJson(response.body(), SubTask.class);
        SubTask taskFromManager = taskManager.getSubTask(taskId);

        assertNotNull(taskResponse, "Задачи не возвращаются");
        assertEquals(taskResponse, taskFromManager, "Задача на сервере и из запроса клиента не равны");
    }

}