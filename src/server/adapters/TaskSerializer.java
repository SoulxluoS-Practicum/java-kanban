package server.adapters;

import com.google.gson.*;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskSerializer<T extends Task> implements JsonDeserializer<T>, JsonSerializer<T> {

    private static Gson getGson() {
        return new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    }

    public static Gson getGsonFull() {
        return new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Task.class, new TaskSerializer<>())
            .registerTypeAdapter(Epic.class, new TaskSerializer<>())
            .registerTypeAdapter(SubTask.class, new TaskSerializer<>())
            .create();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        if (jsonObject.has("type")) {
            String taskType = jsonObject.get("type").getAsString();
            return switch (taskType) {
                case "SUBTASK" -> (T) getGson().fromJson(json, SubTask.class);
                case "EPIC" -> (T) getGson().fromJson(json, Epic.class);
                default -> (T) getGson().fromJson(json, Task.class);
            };
        } else {
            return (T) getGson().fromJson(json, Task.class);
        }
    }

    @Override
    public JsonElement serialize(T task, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonElement element = getGson().toJsonTree(task);
        element.getAsJsonObject().addProperty("type", task.getType().toString());
        return element;
    }
}