import org.junit.jupiter.api.Test;

import java.util.List;

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
        manager.addTask(task1);
        task1.setId(100);
        manager.updateTask(task1);


        Task task2 = new Task("Задача 2", "Описание задачи 2");
        manager.addTask(task2);

        assertEquals(task2, manager.getTaskById(task2.id));
        assertEquals(100, task1.getId());
    }




}