package managers.history;

import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    
    private final ArrayList<Task> historyList = new ArrayList<>(10);
    
    public void add(Task taskOld) {
        if (historyList.size() == 10) {
            historyList.removeFirst();
        }
        Task taskNew = new Task(taskOld.getName(), taskOld.getDescription());
        taskNew.setId(taskOld.getId());
        historyList.add(taskNew);
    }
    
    @Override
    public ArrayList<Task> getHistory() {
        return historyList;
    }
    
}
