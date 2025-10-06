package server.handlers;

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
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpPrioritizedTest extends HttpTasksTest {

    private static final String URL = "http://localhost:8080/prioritized/";

    HttpPrioritizedTest() throws IOException {

    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        Task task = new Task("Task-1", "Task-1 Description", LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task);
        Task task2 = new Task("Task-2", "Task-2 Description", LocalDateTime.now().minusHours(4), Duration.ofMinutes(5));
        taskManager.addTask(task2);
        Epic epic = new Epic("Epic-1", "Epic-1 Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask-1", "SubTask-1 Description", LocalDateTime.now().minusHours(2), Duration.ofMinutes(5), epic.getId());
        taskManager.addSubTask(subTask);

        taskManager.getTask(task.getId());
        taskManager.getEpic(epic.getId());
        taskManager.getSubTask(subTask.getId());

        HttpResponse<String> response = sendRequest(URL, HttpRequest.Builder::GET);
        assertEquals(200, response.statusCode());

        TreeSet<Task> tasksResponse = gson.fromJson(response.body(), new TypeToken<TreeSet<Task>>() {}.getType());
        TreeSet<Task> tasksFromManager = taskManager.getPrioritizedTasks();

        assertNotNull(tasksResponse, "История задач не возвращается");
        assertEquals(tasksResponse, tasksFromManager, "История задач на сервере и из запроса клиента не равны");
    }

}
