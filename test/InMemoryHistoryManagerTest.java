import managers.memory.InMemoryTaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    public void historyShouldUpdateWhenTaskIsDeleted() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = new Task("name", "desc");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(10));
        manager.addTask(task);

        Task savedTask = manager.getTaskById(task.getId()); // теперь попадёт в историю
        assertTrue(manager.getHistory().contains(savedTask), "Задача должна быть в истории");

        manager.deleteTaskById(task.getId());
        assertFalse(manager.getHistory().contains(savedTask), "Задача должна быть удалена из истории");
    }


    @Test
    public void historyShouldUpdateWhenSubtaskIsDeleted() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("name", "desc");
        manager.addEpic(epic);
        int epicId = epic.getId();

        SubTask subTask1 = new SubTask("name", "desc", epicId);
        subTask1.setId(10);
        subTask1.setStartTime(LocalDateTime.now());
        subTask1.setDuration(Duration.ofMinutes(10));
        manager.addSubTask(subTask1);
        int subTaskId = subTask1.getId();

        manager.getSubTaskById(subTaskId);
        assertTrue(manager.getHistory().contains(subTask1), "Подзадача должна быть в истории");

        manager.deleteSubtaskById(subTaskId);
        assertFalse(manager.getHistory().contains(subTask1), "Подзадача должна быть удалена из истории");
    }


    @Test
    public void historyShouldUpdateWhenEpicIsDeleted() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("name", "desc");
        SubTask subTask1 = new SubTask("name", "desc", epic.getId());
        subTask1.setId(10);
        subTask1.setStartTime(LocalDateTime.now());
        subTask1.setDuration(Duration.ofMinutes(10));
        SubTask subTask2 = new SubTask("name2", "desc2", epic.getId());
        subTask2.setId(20);
        subTask2.setStartTime(LocalDateTime.now().plusDays(20));
        subTask2.setDuration(Duration.ofMinutes(10));

        manager.addEpic(epic);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        manager.getEpicById(epic.getId());
        manager.getSubTaskById(subTask1.getId());
        manager.getSubTaskById(subTask2.getId());

        assertTrue(manager.getHistory().contains(epic), "Эпик должен быть в истории");
        assertTrue(manager.getHistory().contains(subTask1), "Подзадача 1 должна быть в истории");
        assertTrue(manager.getHistory().contains(subTask2), "Подзадача 2 должна быть в истории");

        manager.deleteEpicById(epic.getId());

        assertFalse(manager.getHistory().contains(epic), "Эпик должен быть удалён из истории");
        assertFalse(manager.getHistory().contains(subTask1), "Подзадача 1 должна быть удалена из истории");
        assertFalse(manager.getHistory().contains(subTask2), "Подзадача 2 должна быть удалена из истории");
    }

}