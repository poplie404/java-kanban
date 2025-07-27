import http.HttpTaskServer;
import managers.memory.InMemoryTaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = new InMemoryTaskManager();
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        Epic epic = new Epic("Epic1", "Подзадача1, подзадача2");
        Epic epic2 = new Epic("Epic2", "Подзадача3");
        Epic epic3 = new Epic("Epic3", "Подзадача4");
        SubTask sub1 = new SubTask("Sub1", "Подзадача1", 2);
        SubTask sub2 = new SubTask("Sub2", "Подзадача2", 2);
        SubTask sub3 = new SubTask("Sub3", "Подзадача3", 3);
        SubTask sub4 = new SubTask("Sub4", "Подзадача4", 4);

        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(15));
        task2.setStartTime(LocalDateTime.now().plusMinutes(16));
        task2.setDuration(Duration.ofMinutes(2));
        sub1.setStartTime(LocalDateTime.now().plusMinutes(30));
        sub1.setDuration(Duration.ofMinutes(5));
        sub2.setStartTime(LocalDateTime.now().plusMinutes(40));
        sub2.setDuration(Duration.ofMinutes(10));
        sub3.setStartTime(LocalDateTime.now().plusMinutes(90));
        sub3.setDuration(Duration.ofMinutes(50));
        sub4.setStartTime(LocalDateTime.now().plusMinutes(200));
        sub4.setDuration(Duration.ofMinutes(10));

        taskManager.addTask(task1);
        taskManager.getTaskById(task1.getId());
        taskManager.addTask(task2);
        taskManager.getTaskById(task2.getId());
        taskManager.addEpic(epic);
        taskManager.getEpicById(epic.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);
        taskManager.addSubTask(sub1);
        taskManager.addSubTask(sub2);
        taskManager.addSubTask(sub3);
        taskManager.addSubTask(sub4);
        taskManager.getSubTaskById(sub3.getId());
        taskManager.getAllTasks();
        InMemoryTaskManager.printAllTasks(taskManager);
        System.out.println();
        System.out.println();

        task1.setStatus(TaskStatus.IN_PROGRESS);
        sub2.setStatus(TaskStatus.IN_PROGRESS);
        sub3.setStatus(TaskStatus.DONE);
        epic2.setDescription("Новая подзадача в этом эпике");

        taskManager.updateTask(task1);
        taskManager.updateSubTask(sub2);
        taskManager.updateSubTask(sub3);
        taskManager.updateEpic(epic2);
        taskManager.getEpicById(epic2.getId());
        taskManager.getAllTasks();
        InMemoryTaskManager.printAllTasks(taskManager);

        System.out.println();
        System.out.println();

        taskManager.deleteTaskById(1);
        taskManager.deleteEpicById(3);
        taskManager.deleteSubtaskById(5);
        taskManager.deleteEpicById(5);
        taskManager.getEpicById(epic2.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getAllTasks();
        InMemoryTaskManager.printAllTasks(taskManager);
        System.out.println();
        System.out.println();

        }


}


