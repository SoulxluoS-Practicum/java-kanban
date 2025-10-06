package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    abstract T getTaskManager();

    @BeforeEach
    public void beforeEach() {
        taskManager = getTaskManager();
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
        assertTrue(taskManager.getSubTasks().isEmpty(), "TaskManager содержит удалённый Task");
    }

    @Test
    void removeEpic() {
        Epic epic = new Epic("Epic-1 Name", "Epic-1 Description");
        taskManager.addEpic(epic);
        taskManager.removeEpic(epic.getId());
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubTasks().isEmpty(), "TaskManager содержит удалённый Epic");
    }

    @Test
    void removeSubTask() {
        Epic epic = new Epic("Epic-1 Name", "Epic-1 Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask-1 Name", "SubTask-1 Description", epic.getId());
        taskManager.addSubTask(subTask);
        taskManager.removeSubTask(subTask.getId());
        assertTrue(epic.getSubTaskIds().isEmpty(),"Epic содержит удалённый SubTask");
        assertTrue(taskManager.getSubTasks().isEmpty(), "TaskManager содержит удалённый SubTask");
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

    @Test
    void updateEpicStatus() {
        Epic epic = new Epic("Epic-1 Name", "Epic-1 Description");
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask("SubTask-1 Name", "SubTask-1 Description", epic.getId());
        taskManager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("SubTask-2 Name", "SubTask-2 Description", epic.getId());
        taskManager.addSubTask(subTask2);
        assertSame(TaskStatus.NEW, epic.getStatus(),
            "Неверный статус задачи Epic = %s. По статусу его подзадач должно быть: %s + %s = NEW"
                .formatted(epic.getStatus(), subTask1.getStatus(), subTask2.getStatus()));
        subTask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask1);
        subTask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask2);
        assertSame(TaskStatus.DONE, epic.getStatus(),
            "Неверный статус задачи Epic = %s. По статусу его подзадач должно быть: %s + %s = DONE"
                .formatted(epic.getStatus(), subTask1.getStatus(), subTask2.getStatus()));
        subTask1.setStatus(TaskStatus.NEW);
        taskManager.updateSubTask(subTask1);
        subTask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask2);
        assertSame(TaskStatus.IN_PROGRESS, epic.getStatus(),
            "Неверный статус задачи Epic = %s. По статусу его подзадач должно быть: %s + %s = IN_PROGRESS"
                .formatted(epic.getStatus(), subTask1.getStatus(), subTask2.getStatus()));
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTask2);
        assertSame(TaskStatus.IN_PROGRESS, epic.getStatus(),
            "Неверный статус задачи Epic = %s. По статусу его подзадач должно быть: %s + %s = IN_PROGRESS"
                .formatted(epic.getStatus(), subTask1.getStatus(), subTask2.getStatus()));
    }

    @Test
    void hasOverlaps() {
        LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 0, 0);
        Task task1 = new Task("Task-1 Name", "Task-1 Description", startTime, Duration.ofHours(1));
        taskManager.addTask(task1);
        Task task2 = new Task("Task-2 Name", "Task-2 Description", startTime, Duration.ofMinutes(1));
        taskManager.addTask(task2);
        assertFalse(taskManager.getTasks().contains(task2), "TaskManager добавил пересекающийся Task");

        Task task3 = new Task("Task-3 Name", "Task-3 Description", startTime.plusDays(3), Duration.ofMinutes(20));
        taskManager.addTask(task3);
        assertTrue(taskManager.getTasks().contains(task3), "TaskManager не добавил не пересекающийся Task");
        assertTrue(taskManager.getTasks().contains(task3), "TaskManager не добавил не пересекающийся Task");
    }

    @Test
    void getPrioritizedTasks() {
        LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 0, 0);
        Task task1 = new Task("Task-1 Name", "Task-1 Description", startTime, Duration.ofHours(1));
        taskManager.addTask(task1);
        Task task2 = new Task("Task-2 Name", "Task-2 Description", startTime.plusDays(3), Duration.ofMinutes(1));
        taskManager.addTask(task2);
        Epic epic = new Epic("Task-3 Name", "Task-3 Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask-1 Name", "SubTask-1 Description", startTime.plusDays(5), Duration.ofMinutes(20), epic.getId());
        taskManager.addSubTask(subTask);
        assertEquals(taskManager.getPrioritizedTasks().getFirst(), task1, "Задачи не корректно рассортированы по времени начала");
        assertEquals(taskManager.getPrioritizedTasks().getLast(), epic, "Задачи не корректно рассортированы по времени начала");
    }
}