package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    
    private static TaskManager taskManager;
    
    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }
    
    @Test
    void addTask() {
        Task task = new Task("Task-1 Name", "Task-1 Description");
        taskManager.addTask(task);
        Task taskInManager = taskManager.getTask(task.getId());
        assertNotNull(taskInManager,"TaskManager не содержит добавленный Task");
        assertEquals(task, taskInManager,"Созданный Task не совпадает с хранимым Task");
    }
    
    @Test
    void addEpic() {
        Epic epic = new Epic("Epic-1 Name", "Epic-1 Description");
        taskManager.addEpic(epic);
        Epic epicInManager = taskManager.getEpic(epic.getId());
        assertNotNull(taskManager.getEpic(epic.getId()),"TaskManager не содержит добавленный Epic");
        assertEquals(epic, epicInManager,"Созданный Epic не совпадает с хранимым Epic");
    }
    
    @Test
    void addSubTask() {
        Epic epic = new Epic("Epic-1 Name", "Epic-1 Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask-1 Name", "SubTask-1 Description", epic.getId());
        taskManager.addSubTask(subTask);
        SubTask subTaskInManager = taskManager.getSubTask(subTask.getId());
        assertNotNull(subTaskInManager,"TaskManager не содержит добавленный SubTask");
        assertEquals(subTask, subTaskInManager,"Созданный SubTask не совпадает с хранимым SubTask");
        assertTrue(epic.getSubTaskIds().contains(subTask.getId()), "Epic не содержит Id добавленного в него SubTask");
    }
    
    @Test
    void updateTask() {
        Task task = new Task("Task-1 Name", "Task-1 Description");
        taskManager.addTask(task);
        Task taskUpdate = new Task("Task-1 Name Update", "Task-1 Description Update");
        taskUpdate.setId(task.getId());
        taskManager.updateTask(taskUpdate);
        assertNotNull(taskManager.getTask(task.getId()),"TaskManager не содержит обновлённый Task");
    }
    
    @Test
    void updateEpic() {
        Epic epic = new Epic("Epic-1 Name", "Epic-1 Description");
        taskManager.addEpic(epic);
        Epic epicUpdate = new Epic("Epic-1 Name Update", "Epic-1 Description Update");
        epicUpdate.setId(epic.getId());
        taskManager.updateEpic(epicUpdate);
        assertNotNull(taskManager.getEpic(epic.getId()),"TaskManager не содержит обновлённый Epic");
    }
    
    @Test
    void updateSubTask() {
        Epic epic = new Epic("Epic-1 Name", "Epic-1 Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask-1 Name", "SubTask-1 Description", epic.getId());
        taskManager.addSubTask(subTask);
        SubTask subTaskUpdate = new SubTask("SubTask-1 Name", "SubTask-1 Description", epic.getId());
        subTaskUpdate.setId(subTask.getId());
        taskManager.updateSubTask(subTaskUpdate);
        assertNotNull(taskManager.getSubTask(subTask.getId()),"TaskManager не содержит обновлённый SubTask");
    }
    
    @Test
    void getEpicSubtasks() {
        Epic epic = new Epic("Epic-1 Name", "Epic-1 Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask-1 Name", "SubTask-1 Description", epic.getId());
        taskManager.addSubTask(subTask);
        assertFalse(epic.getSubTaskIds().isEmpty(),"Epic не содержит добавленные SubTask");
    }
    
    @Test
    void removeTask() {
        Task task = new Task("Task-1 Name", "Task-1 Description");
        taskManager.addTask(task);
        taskManager.removeTask(task.getId());
        assertTrue(taskManager.getTasks().isEmpty());
    }
    
    @Test
    void removeEpic() {
        Epic epic = new Epic("Epic-1 Name", "Epic-1 Description");
        taskManager.addEpic(epic);
        taskManager.removeEpic(epic.getId());
        assertTrue(taskManager.getEpics().isEmpty());
    }
    
    @Test
    void removeSubTask() {
        Epic epic = new Epic("Epic-1 Name", "Epic-1 Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask-1 Name", "SubTask-1 Description", epic.getId());
        taskManager.addSubTask(subTask);
        taskManager.removeSubTask(subTask.getId());
        assertTrue(taskManager.getSubTasks().isEmpty());
    }
    
    @Test
    void removeTasks() {
        addTask();
        taskManager.removeTasks();
        assertTrue(taskManager.getTasks().isEmpty());
    }
    
    @Test
    void removeEpics() {
        addEpic();
        taskManager.removeEpics();
        assertTrue(taskManager.getEpics().isEmpty());
    }
    
    @Test
    void removeSubTasks() {
        addSubTask();
        taskManager.removeSubTasks();
        assertTrue(taskManager.getSubTasks().isEmpty());
    }
}