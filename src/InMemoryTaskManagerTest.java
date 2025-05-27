import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    @Test
    public void shouldAddAndFindTasksById() {
        InMemoryTaskManager manager = new InMemoryTaskManager();


        Task task = new Task("Обычная задача", "Описание задачи");
        manager.addTask(task);


        Epic epic = new Epic("Эпик", "Описание эпика");
        manager.addEpic(epic);


        SubTask subtask = new SubTask("Подзадача", "Описание подзадачи", epic.getId());
        manager.addSubTask(subtask);


        assertEquals(task, manager.getTaskById(task.getId()));
        assertEquals(epic, manager.getEpicById(epic.getId()));
        assertEquals(subtask, manager.getSubTaskById(subtask.getId()));
    }

    @Test
    public void shouldNotConflictBetweenManualAndGeneratedIds() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(task1);
        task1.setId(100);
        manager.updateTask(task1);


        Task task2 = new Task("Задача 2", "Описание задачи 2");
        manager.addTask(task2);

        assertEquals(task2, manager.getTaskById(task2.id));
        assertEquals(100, task1.getId());
    }



}