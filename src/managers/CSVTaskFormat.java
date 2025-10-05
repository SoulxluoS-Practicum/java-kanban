package managers;

import tasks.*;

import java.util.Arrays;

public class CSVTaskFormat {

    public static String getHeader() {
        return "id,type,name,status,description,epic";
    }

    public static String toString(Task task) {
        String result = strJoinObj(",",
            task.getId(),
            task.getType(),
            task.getName(),
            task.getStatus(),
            task.getDescription()
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
            return switch (type) {
                case EPIC -> {
                    Epic epic = new Epic(name, description);
                    epic.setId(id);
                    epic.setStatus(status);
                    yield epic;
                }
                case SUBTASK -> {
                    int epicId = Integer.parseInt(fields[5]);
                    SubTask subTask = new SubTask(name, description, epicId);
                    subTask.setId(id);
                    subTask.setStatus(status);
                    yield subTask;
                }
                case TASK -> {
                    Task task = new Task(name, description);
                    task.setId(id);
                    task.setStatus(status);
                    yield task;
                }
            };
        } catch (Exception e) {
            System.err.printf("Ошибка при чтении задачи из строки: %s%n", str);
            e.printStackTrace();
        }
        return null;
    }

}
