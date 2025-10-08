package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import exceptions.TaskOverlapException;
import managers.TaskManager;
import tasks.SubTask;

import java.io.IOException;
import java.util.List;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public SubTaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final Integer requestTaskId = getIdFromPath(exchange.getRequestURI().getPath());

        switch (exchange.getRequestMethod()) {
            case "GET" -> {
                if (requestTaskId == null) {
                    List<SubTask> tasks = taskManager.getSubTasks();
                    System.out.println("Задачи запрошены");
                    String response = gson.toJson(tasks);
                    sendText(exchange, response);
                    return;
                }
                try {
                    SubTask task = taskManager.getSubTask(requestTaskId);
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
                SubTask task = gson.fromJson(taskJson, SubTask.class);
                if (task.getId() > 0) {
                    taskManager.updateSubTask(task);
                    System.out.printf("Задача обновлена id=%s%n", task.getId());
                    exchange.sendResponseHeaders(201,0);
                    exchange.close();
                } else {
                    try {
                        taskManager.addSubTask(task);
                        System.out.printf("Задача создана id=%s%n", task.getId());
                        sendText(exchange, gson.toJson(task));
                    } catch (TaskOverlapException e) {
                        System.out.println(e.getMessage());
                        sendHasOverlaps(exchange);
                    }
                }
            }
            case "DELETE" -> {
                taskManager.removeSubTask(requestTaskId);
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
