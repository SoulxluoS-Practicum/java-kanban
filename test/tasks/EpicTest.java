package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    void testEquals() {
        Epic epic1 = new Epic("Epic-1 Name", "Epic-1 Description");
        Epic epic2 = new Epic("Epic-2 Name", "Epic-2 Description");
        epic1.setId(1);
        epic2.setId(1);
        assertEquals(epic1, epic2,"Задачи не равны по Id, странно...");
    }
}