package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import server.adapters.TaskSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {

    protected final Gson gson = TaskSerializer.getGsonFull();

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        send(exchange, text, 200);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        send(exchange, "Задача не найдена!", 404);
    }

    protected void sendHasOverlaps(HttpExchange exchange) throws IOException {
        send(exchange, "Задача пересекается с существующими!", 406);
    }

    protected void sendBadRequest(HttpExchange exchange) throws IOException {
        send(exchange, "Неизвестный запрос!", 404);
    }

    private void send(HttpExchange exchange, String message, int code) throws IOException {
        byte[] resp = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(code, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected Integer getIdFromPath(String requestPath) {
        try {
            return Integer.parseInt(requestPath.split("/")[2]);
        } catch (Exception e) {
            return null;
        }
    }

    protected String readText(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
}
