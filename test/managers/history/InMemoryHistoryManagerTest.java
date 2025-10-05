package managers.history;

import managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertFalse(historyManager.getHistory().isEmpty(), "Задача %s не добавлена в HistoryManager".formatted(task1.getId()));
    }

    @Test
    void remove() {
        Task task2 = new Task("Task-2 Name", "Task-2 Description");
        historyManager.add(task2);
        historyManager.remove(task2.getId());
        assertTrue(historyManager.getHistory().isEmpty(), "Задача %s не удалена из HistoryManager".formatted(task2.getId()));
    }
}