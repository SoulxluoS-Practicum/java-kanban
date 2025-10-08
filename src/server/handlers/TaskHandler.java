package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import exceptions.TaskOverlapException;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final Integer requestTaskId = getIdFromPath(exchange.getRequestURI().getPath());

        switch (exchange.getRequestMethod()) {
            case "GET" -> {
                if (requestTaskId == null) {
                    List<Task> tasks = taskManager.getTasks();
                    System.out.println("Задачи запрошены");
                    String response = gson.toJson(tasks);
                    sendText(exchange, response);
                    return;
                }
                try {
                    Task task = taskManager.getTask(requestTaskId);
                    System.out.printf("Задача запрошена id=%s%n", task.getId());
                    String response = gson.toJson(task);
                    sendText(exchange, response);
                } catch (NotFoundException e) {
                    System.out.println(e.getMessage());
                    sendNotFound(exchange);
                }
            }
            case "POST" -> {
                String taskJson = readText(exchange);
                Task task = gson.fromJson(taskJson, Task.class);
                if (task.getId() > 0) {
                    taskManager.updateTask(task);
                    System.out.printf("Задача обновлена id=%s%n", task.getId());
                    exchange.sendResponseHeaders(201,0);
                    exchange.close();
                } else {
                    try {
                        taskManager.addTask(task);
                        System.out.printf("Задача создана id=%s%n", task.getId());
                        sendText(exchange, gson.toJson(task));
                    } catch (TaskOverlapException e) {
                        System.out.println(e.getMessage());
                        sendHasOverlaps(exchange);
                    }
                }
            }
            case "DELETE" -> {
                taskManager.removeTask(requestTaskId);
                System.out.printf("Задача удалена id=%s%n", requestTaskId);
                exchange.sendResponseHeaders(201, 0);
                exchange.close();
            }
            default -> {
                System.out.printf("Получен неизвестный запрос %s%n", exchange.getRequestMethod());
                sendBadRequest(exchange);
            }
        }
    }

}
