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

class HttpEpicTaskManagerTasksTest extends HttpTasksTest {

    private static final String URL = "http://localhost:8080/epics/";

    HttpEpicTaskManagerTasksTest() throws IOException {
    }

    @Test
    void addTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic-1", "Epic-1 Description");
        String taskJson = gson.toJson(epic);

        HttpResponse<String> response = sendRequest(URL, builder -> builder.POST(HttpRequest.BodyPublishers.ofString(taskJson)));
        assertEquals(200, response.statusCode());

        List<Epic> tasksFromManager = taskManager.getEpics();
        
        assertNotNull(tasksFromManager, "Список Epic задач у сервера равен null");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество Epic задач после добавления: %s != 1".formatted(tasksFromManager.size()));
        String taskName = tasksFromManager.getFirst().getName();
        assertEquals("Epic-1", taskName, "Некорректное имя %s созданной Epic задачи".formatted(taskName));
    }

    @Test
    void deleteTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic-1", "Epic-1 Description");
        int taskId = taskManager.addEpic(epic);

        HttpResponse<String> response = sendRequest(URL + taskId, HttpRequest.Builder::DELETE);
        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = taskManager.getEpics();
        
        assertNotNull(tasksFromManager, "Список Epic задач у сервера равен null");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество Epic задач после удаления: %s != 0".formatted(tasksFromManager.size()));
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic-1", "Epic-1 Description");
        taskManager.addEpic(epic);

        HttpResponse<String> response = sendRequest(URL, HttpRequest.Builder::GET);
        assertEquals(200, response.statusCode());

        List<Epic> tasksResponse = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {}.getType());
        List<Epic> tasksFromManager = taskManager.getEpics();
        
        assertNotNull(tasksResponse, "Список Epic задач у клиента равен null");
        assertNotNull(tasksFromManager, "Список Epic задач у сервера равен null");
        assertEquals(tasksResponse, tasksFromManager, "Списки Epic задач у сервера и у клиента не равны");
    }

    @Test
    void getTaskByID() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic-1", "Epic-1 Description");
        int taskId = taskManager.addEpic(epic);

        HttpResponse<String> response = sendRequest(URL + taskId, HttpRequest.Builder::GET);
        assertEquals(200, response.statusCode());

        Epic taskResponse = gson.fromJson(response.body(), Epic.class);
        Epic taskFromManager = taskManager.getEpic(taskId);
        
        assertNotNull(taskResponse, "Epic задача у клиента равна null");
        assertEquals(taskResponse, taskFromManager, "Epic задача у сервера и у клиента не равны");
    }

    @Test
    void getEpicSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic-1", "Epic-1 Description");
        int taskId = taskManager.addEpic(epic);
        SubTask task = new SubTask("SubTask-1", "SubTask-1 Description", LocalDateTime.now(), Duration.ofMinutes(5), epic.getId());
        taskManager.addSubTask(task);

        HttpResponse<String> response = sendRequest(URL + taskId + "/subtasks", HttpRequest.Builder::GET);
        assertEquals(200, response.statusCode());

        List<SubTask> tasksResponse = gson.fromJson(response.body(), new TypeToken<List<SubTask>>() {}.getType());
        List<SubTask> tasksFromManager = taskManager.getEpicSubtasks(taskId);
        
        assertNotNull(tasksResponse, "Список Epic задач у клиента равен null");
        assertNotNull(tasksFromManager, "Список Epic задач у сервера равен null");
        assertEquals(tasksResponse, tasksFromManager, "Списки SubTask задач в Epic у сервера и у клиента не равны");
    }

}