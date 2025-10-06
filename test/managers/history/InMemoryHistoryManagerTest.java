package managers.history;

import managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static InMemoryHistoryManager historyManager;
    private static int idCounter;

    @BeforeEach
    public void beforeEach() {
        historyManager = (InMemoryHistoryManager) Managers.getDefaultHistory();
        idCounter = 0;
    }

    @Test
    void add() {
        Task task1 = new Task("Task-1 Name", "Task-1 Description");
        task1.setId(++idCounter);
        historyManager.add(task1);
        assertFalse(historyManager.getHistory().isEmpty(), "Задача %s не добавлена в HistoryManager".formatted(task1.getId()));
        Task task2 = new Task("Task-2 Name", "Task-2 Description");
        task2.setId(++idCounter);
        historyManager.add(task2);
        Task task3 = new Task("Task-2 Name", "Task-2 Description");
        task3.setId(++idCounter);
        historyManager.add(task3);
        assertNotEquals(historyManager.getFirst().getTask(), task3,
            "Задача %s не первая в списке после добавления в HistoryManager".formatted(task3.getId()));
        historyManager.add(task1);
        assertNotEquals(historyManager.getFirst().getTask(), task1,
            "Задача %s не первая в списке после дублирования в HistoryManager".formatted(task1.getId()));
        assertFalse(historyManager.getHistory().stream().filter(task -> task.equals(task1)).count() > 1,
            "Задача %s присутствует больше, чем один раз после дублирования в HistoryManager".formatted(task1.getId()));
        historyManager.remove(task2.getId());
        assertFalse(historyManager.getHistory().stream().filter(task -> task.equals(task1)).count() > 1,
            "Задача %s присутствует больше, чем один раз в HistoryManager".formatted(task1.getId()));
    }

    @Test
    void remove() {
        Task task1 = new Task("Task-1 Name", "Task-1 Description");
        task1.setId(++idCounter);
        historyManager.add(task1);
        historyManager.remove(task1.getId());
        assertTrue(historyManager.getHistory().isEmpty(),
            "Задача %s не удалена из HistoryManager".formatted(task1.getId()));
        Task task2 = new Task("Task-2 Name", "Task-2 Description");
        task2.setId(++idCounter);
        historyManager.add(task2);
        historyManager.add(task1);
        Task task3 = new Task("Task-3 Name", "Task-3 Description");
        task3.setId(++idCounter);
        historyManager.add(task3);
        historyManager.remove(task1.getId());
        assertNotEquals(historyManager.getFirst().getNext(), historyManager.getLast().getPrev(),
            "Не правильная связь между нодами при удалении задачи из середины HistoryManager");
        historyManager.add(task1);
        historyManager.remove(task2.getId());
        assertNotEquals(historyManager.getFirst().getNext(), historyManager.getLast().getPrev(),
            "Не правильная связь между нодами при удалении задачи из начала в HistoryManager");
        historyManager.add(task2);
        historyManager.remove(task2.getId());
        assertNotEquals(historyManager.getFirst().getNext(), historyManager.getLast().getPrev(),
            "Не правильная связь между нодами при удалении задачи из конца в HistoryManager");
    }
}