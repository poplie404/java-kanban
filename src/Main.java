

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        Epic epic = new Epic("Epic1", "Подзадача1, подзадача2");
        Epic epic2 = new Epic("Epic2", "Подзадача3");
        SubTask sub1 = new SubTask("Sub1", "Подзадача1", 2);
        SubTask sub2 = new SubTask("Sub2", "Подзадача2", 2);
        SubTask sub3 = new SubTask("Sub3", "Подзадача3", 3);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic);
        taskManager.addEpic(epic2);
        taskManager.addSubTask(sub1);
        taskManager.addSubTask(sub2);
        taskManager.addSubTask(sub3);
        taskManager.getAllTasks();
        System.out.println();
        System.out.println();

        taskManager.updateTaskStatus(task1, TaskStatus.IN_PROGRESS);
        taskManager.updateSubTaskStatus(sub2, TaskStatus.DONE);
        taskManager.updateSubTaskStatus(sub3, TaskStatus.DONE);
        taskManager.getAllTasks();
        System.out.println();
        System.out.println();

        taskManager.deleteTaskById(1);
        taskManager.deleteEpicById(3);
        taskManager.deleteSubtaskById(5);
        taskManager.getAllTasks();
        System.out.println();
        System.out.println();

        }


}


