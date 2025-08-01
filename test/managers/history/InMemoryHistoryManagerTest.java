package managers.history;

import managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.util.stream.IntStream;

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
        task1.setStatus(TaskStatus.IN_PROGRESS);
        historyManager.add(task1);
        assertEquals(TaskStatus.NEW, historyManager.getHistory().getFirst().getStatus(), "HistoryManager не сохранил предыдущую версию задачи");
        IntStream.rangeClosed(2, 11).forEach(i -> {
            Task task = new Task("Task-%s Name".formatted(i), "Task-%s Description".formatted(i));
            historyManager.add(task);
        });
        assertFalse(historyManager.getHistory().size() > 10, "HistoryManager сохраняет больше допустимого кол-ва задач");
    }
}