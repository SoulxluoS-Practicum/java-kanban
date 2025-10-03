package managers.history;

import managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    
    private static HistoryManager historyManager;
    
    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
    }
    
    @Test
    void add() {
        Task task1 = new Task("Task-1 Name", "Task-1 Description");
        historyManager.add(task1);
        assertFalse(historyManager.getHistory().isEmpty(), "Задача не добавлена в HistoryManager");
    }
    
    @Test
    void remove() {
        Task task1 = new Task("Task-1 Name", "Task-1 Description");
        historyManager.add(task1);
        assertFalse(historyManager.getHistory().isEmpty(), "Задача не добавлена в HistoryManager");
        historyManager.remove(task1.getId());
        assertTrue(historyManager.getHistory().isEmpty(), "Задача не удалена из HistoryManager");
    }
}