package tasks;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testEquals() {
        Task task1 = new Task("Task-1 Name", "Task-1 Description");
        Task task2 = new Task("Task-2 Name", "Task-2 Description");
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2,"Задачи не равны по Id, странно...");
    }

    @Test
    void hasOverlap() {
        Task task1 = new Task("Task-1 Name", "Task-1 Description",
            LocalDateTime.of(2025, 5, 5, 5, 5), Duration.ofHours(1));
        Task task2 = new Task("Task-2 Name", "Task-2 Description",
            LocalDateTime.of(2025, 5, 5, 3, 5), Duration.ofMinutes(1));
        assertFalse(task1.hasOverlapWith(task2), "Задачи пересекаются при не пересекающихся отрезках времени!");
        Task task3 = new Task("Task-3 Name", "Task-3 Description",
            LocalDateTime.of(2025, 5, 5, 4, 5), Duration.ofHours(2));
        assertTrue(task1.hasOverlapWith(task3), "Задачи не пересекаются при пересекающихся отрезках времени!");
    }
}