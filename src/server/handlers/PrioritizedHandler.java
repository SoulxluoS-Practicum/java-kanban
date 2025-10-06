package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.TreeSet;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET" -> {
                TreeSet<Task> tasks = taskManager.getPrioritizedTasks();
                System.out.println("Запрошены задачи по приоритету");
                String response = gson.toJson(tasks);
                sendText(exchange, response);
            }
            default -> {
                System.out.printf("Получен неизвестный запрос %s%n", exchange.getRequestMethod());
                sendBadRequest(exchange);
            }
        }
    }

}
