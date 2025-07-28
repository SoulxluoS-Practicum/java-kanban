package tasks;

import java.util.ArrayList;

/**
* Большая задача, которая делится на подзадачи{@link SubTask}
* */
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
    public String toString() {
        return "EpicTask{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", status=" + status +
            ", subTasksIds.size=" + subTaskIds.size() +
            '}';
    }
}
