package tasks;

/**
 * Подзадача, которая является частью Большой задачи({@link Epic})
 *
 */
public class SubTask extends Task {

    private final int epicId;

    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", status=" + status +
            ", epicId=" + epicId +
            '}';
    }
}
