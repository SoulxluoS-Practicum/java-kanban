package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import exceptions.TaskOverlapException;
import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;

import java.io.IOException;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final Integer requestTaskId = getIdFromPath(exchange.getRequestURI().getPath());

        switch (exchange.getRequestMethod()) {
            case "GET" -> {
                if (requestTaskId == null) {
                    List<Epic> tasks = taskManager.getEpics();
                    System.out.println("Задачи запрошены");
                    String response = gson.toJson(tasks);
                    sendText(exchange, response);
                    return;
                }
                try {
                    Epic task = taskManager.getEpic(requestTaskId);
                    if (exchange.getRequestURI().getPath().equals("/epics/" + requestTaskId + "/subtasks")) {
                        List<SubTask> tasks = taskManager.getEpicSubtasks(requestTaskId);
                        System.out.println("Подзадачи Epic запрошены");
                        String response = gson.toJson(tasks);
                        sendText(exchange, response);
                    } else {
                        System.out.printf("Задача запрошена id=%s%n", task.getId());
                        String response = gson.toJson(task);
                        sendText(exchange, response);
                    }
                } catch (NotFoundException e) {
                    System.out.println(e.getMessage());
                    sendNotFound(exchange);
                }
            }
            case "POST" -> {
                String taskJson = readText(exchange);
                Epic task = gson.fromJson(taskJson, Epic.class);
                if (task.getId() == 0) {
                    try {
                        taskManager.addEpic(task);
                        System.out.printf("Задача создана id=%s%n", task.getId());
                        sendText(exchange, gson.toJson(task));
                    } catch (TaskOverlapException e) {
                        System.out.println(e.getMessage());
                        sendHasOverlaps(exchange);
                    }
                } else {
                    System.out.printf("Получен неизвестный запрос %s%n", exchange.getRequestMethod());
                    sendBadRequest(exchange);
                }
            }
            case "DELETE" -> {
                taskManager.removeEpic(requestTaskId);
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
