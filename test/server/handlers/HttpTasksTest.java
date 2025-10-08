package server.handlers;

import com.google.gson.Gson;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import server.HttpTaskServer;
import server.adapters.TaskSerializer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public abstract class HttpTasksTest {

    protected TaskManager taskManager = new InMemoryTaskManager();
    protected HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    protected Gson gson = TaskSerializer.getGsonFull();

    HttpTasksTest() throws IOException {}

    @BeforeEach
    void setUp() {
        taskManager.removeTasks();
        taskManager.removeSubTasks();
        taskManager.removeEpics();
        taskServer.start();
    }

    @AfterEach
    void shutDown() {
        taskServer.stop();
    }

    static HttpResponse<String> sendRequest(String URL, Consumer<HttpRequest.Builder> requestConsumer) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL);
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(url);
        requestConsumer.accept(builder);

        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

}
