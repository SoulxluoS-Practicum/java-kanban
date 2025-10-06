package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Задача
 *
 */
public class Task implements Comparable<Task> {

    protected int id;
    protected TaskStatus status = TaskStatus.NEW;
    protected String name;
    protected String description;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this(name, description);
        this.startTime = startTime;
        this.duration = duration;
        if (startTime != null && duration != null) {
            this.endTime = startTime.plus(duration);
        }
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }

    public Optional<LocalDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public Optional<LocalDateTime> getEndTime() {
        if (endTime == null && startTime != null && duration != null) {
            return Optional.of(endTime = startTime.plus(duration));
        }
        return Optional.ofNullable(endTime);
    }

    public boolean hasOverlapWith(Task task2) {
        if (this.getEndTime().isEmpty() || task2.getEndTime().isEmpty()) {
            return false;
        }
        LocalDateTime start1 = this.startTime;
        LocalDateTime start2 = task2.startTime;
        LocalDateTime end1 = this.getEndTime().get();
        LocalDateTime end2 = task2.getEndTime().get();
        return !end1.isBefore(start2) && !start1.isAfter(end2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id;
    }

    @Override
    public String toString() {
        return "Task{id=%d, name='%s', description='%s', status=%s, startTime=%s, duration=%s, endTime=%s}"
            .formatted(id, name, description, status, startTime, duration, endTime);
    }

    @Override
    public int compareTo(Task otherTask) {
        return startTime != null ? startTime.compareTo(otherTask.startTime) : -1;
    }
}
