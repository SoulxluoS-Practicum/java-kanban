package managers;

import exceptions.ManagerLoadException;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class CSVTaskFormat {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm_dd:MM:yyyy");

    public static String getHeader() {
        return "id,type,name,status,description,startTime,duration,epic";
    }

    public static String toString(Task task) {
        String result = strJoinObj(",",
            task.getId(),
            task.getType(),
            task.getName(),
            task.getStatus(),
            task.getDescription(),
            task.getStartTime().isPresent() ? task.getStartTime().get().format(DATE_TIME_FORMATTER) : "null",
            task.getDuration().isPresent() ? task.getDuration().get().toMinutes() : "null"
        );
        if (task instanceof SubTask subTask) {
            result = strJoinObj(",",
                result,
                subTask.getEpicId()
            );
        }
        return result;
    }

    private static String strJoinObj(CharSequence delimiter, Object... obj) {
        return String.join(delimiter, Arrays.stream(obj).map(Object::toString).toArray(String[]::new));
    }

    public static Task fromString(String str) {
        String[] fields = str.split(",");
        try {
            int id = Integer.parseInt(fields[0]);
            TaskType type = TaskType.valueOf(fields[1]);
            String name = fields[2];
            TaskStatus status = TaskStatus.valueOf(fields[3]);
            String description = fields[4];
            LocalDateTime startTime = fields[5].equals("null") ? null : (LocalDateTime) DATE_TIME_FORMATTER.parse(fields[5]);
            Duration duration = fields[6].equals("null") ? null : Duration.ofMinutes(Long.parseLong(fields[6]));

            return switch (type) {
                case EPIC -> {
                    Epic epic = new Epic(name, description);
                    epic.setId(id);
                    epic.setStatus(status);
                    yield epic;
                }
                case SUBTASK -> {
                    int epicId = Integer.parseInt(fields[7]);
                    SubTask subTask = new SubTask(name, description, startTime, duration, epicId);
                    subTask.setId(id);
                    subTask.setStatus(status);
                    yield subTask;
                }
                case TASK -> {
                    Task task = new Task(name, description, startTime, duration);
                    task.setId(id);
                    task.setStatus(status);
                    yield task;
                }
            };
        } catch (Exception e) {
            throw new ManagerLoadException("Ошибка при чтении задачи из строки: %s%n".formatted(str), e);
        }
    }

}
