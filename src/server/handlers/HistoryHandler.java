package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET" -> {
                List<Task> tasks = taskManager.getHistoryManager().getHistory();
                System.out.println("Запрошена история просмотра задач");
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
