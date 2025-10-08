package server.handlers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpHistoryTest extends HttpTasksTest {

    private static final String URL = "http://localhost:8080/history/";

    HttpHistoryTest() throws IOException {

    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        Task task = new Task("Task-1", "Task-1 Description", LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task);
        Epic epic = new Epic("Epic-1", "Epic-1 Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask-1", "SubTask-1 Description", LocalDateTime.now().minusHours(2), Duration.ofMinutes(5), epic.getId());
        taskManager.addSubTask(subTask);

        taskManager.getTask(task.getId());
        taskManager.getEpic(epic.getId());
        taskManager.getSubTask(subTask.getId());

        HttpResponse<String> response = sendRequest(URL, HttpRequest.Builder::GET);
        assertEquals(200, response.statusCode());

        List<Task> tasksResponse = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());
        List<Task> tasksFromManager = taskManager.getHistoryManager().getHistory();

        assertNotNull(tasksResponse, "История задач у клиента равна null");
        assertNotNull(tasksFromManager, "История задач у сервера равна null");
        assertEquals(tasksResponse, tasksFromManager, "История задач у сервера и у клиента не равны");
    }

}
