import managers.memory.InMemoryTaskManager;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    public void shouldNotConflictBetweenManualAndGeneratedIds() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(10));
        manager.addTask(task1);
        task1.setId(100);
        manager.updateTask(task1);


        Task task2 = new Task("Задача 2", "Описание задачи 2");
        task2.setStartTime(LocalDateTime.now().plusDays(20));
        task2.setDuration(Duration.ofMinutes(10));
        manager.addTask(task2);

        assertEquals(task2, manager.getTaskById(task2.getId()));
        assertEquals(100, task1.getId());
    }




}