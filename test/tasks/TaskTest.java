package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    void testEquals() {
        Task task1 = new Task("Task-1 Name", "Task-1 Description");
        Task task2 = new Task("Task-2 Name", "Task-2 Description");
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2,"Задачи не равны по Id, странно...");
    }
}