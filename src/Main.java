import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static TaskManager taskManager;
    
    public static void main(String[] args) {
        System.out.println("Поехали!");
        
        taskManager = new TaskManager();
        
        Task task1 = new Task("Task-1. Попить воды", "Проще некуда!");
        Task task2 = new Task("Task-2. Пропылесосить", "А вот это посложнее...");
        
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        
        Epic epic1 = new Epic("Epic-1. Пройти 4 спринт", "Жёсткий дедлайн 4.08");
        Epic epic2 = new Epic("Epic-2. Пройти 5 спринт", "Жёсткий дедлайн 4.08");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        
        SubTask subTask1 = new SubTask("SubTask-1. Изучить теорию","Читаем практикум, смотрим вебинары...", epic1.getId());
        SubTask subTask2 = new SubTask("SubTask-2. Отправить ТЗ","Написать код, залить на GitHub.", epic1.getId());
        SubTask subTask3 = new SubTask("SubTask-3. Изучить теорию и отправить ТЗ","Сделать в один присест - за день! Дааа, я чувствую силу!", epic2.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks());
        
        task1.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task1);
        
        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks());
        
        taskManager.removeTask(task1.getId());
        taskManager.removeSubTask(subTask2.getId());
        
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks());
        
        taskManager.removeTasks();
        taskManager.removeEpics();
        
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks());
        
        System.out.println("Приехали!");
    }
}
