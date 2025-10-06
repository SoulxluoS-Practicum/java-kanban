package tasks;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Большая задача, которая делится на подзадачи{@link SubTask}
 *
 */
public class Epic extends Task {

    private final ArrayList<Integer> subTaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubTaskID(Integer taskId) {
        subTaskIds.add(taskId);
    }

    public void removeSubTaskID(Integer taskId) {
        subTaskIds.remove(taskId);
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void updateDateTimes(Collection<SubTask> subTasks) {
        if (subTasks.isEmpty()) {
            startTime = null;
            endTime = null;
            duration = null;
        } else {
            startTime = subTasks.stream()
                .min(Task::compareTo)
                .flatMap(Task::getStartTime)
                .orElse(null);
            endTime = subTasks.stream()
                .max(Task::compareTo)
                .flatMap(Task::getEndTime)
                .orElse(null);
            duration = Duration.ZERO;
            subTasks.forEach(subTask -> {
                if (subTask.duration != null) {
                    duration = duration.plus(subTask.duration);
                }
            });
        }
    }

    @Override
    public String toString() {
        return "EpicTask{id=%d, name='%s', description='%s', status=%s, startTime=%s, duration=%s, endTime=%s, subTasksIds.size=%d}"
            .formatted(id, name, description, status, startTime, duration, endTime, subTaskIds.size());
    }
}
