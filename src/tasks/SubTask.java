package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Подзадача, которая является частью Большой задачи({@link Epic})
 *
 */
public class SubTask extends Task {

    private final int epicId;

    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, LocalDateTime startTime, Duration duration, int epicId) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "SubTask{id=%d, name='%s', description='%s', status=%s, startTime=%s, duration=%s, endTime=%s, epicId=%d}"
            .formatted(id, name, description, status, startTime, duration, endTime, epicId);
    }
}
