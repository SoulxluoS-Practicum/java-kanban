package server;

import com.sun.net.httpserver.HttpServer;
import managers.FileBackedTaskManager;
import managers.TaskManager;
import server.handlers.*;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    public static final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress("localhost", PORT),0);
        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/subtasks", new SubTaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(FileBackedTaskManager.loadFromFile(new File("task_storage.txt")));
        httpTaskServer.start();
    }
}
