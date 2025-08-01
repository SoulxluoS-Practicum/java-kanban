package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    
    @Test
    void testEquals() {
        Epic epic1 = new Epic("Epic-1 Name", "Epic-1 Description");
        SubTask subTask1 = new SubTask("SubTask-1 Name", "SubTask-1 Description", epic1.getId());
        SubTask subTask2 = new SubTask("SubTask-2 Name", "SubTask-2 Description", epic1.getId());
        subTask1.setId(1);
        subTask2.setId(1);
        assertEquals(subTask1, subTask2,"Задачи не равны по Id, странно...");
    }
}