import static org.junit.jupiter.api.Assertions.*;

import managers.Managers;
import model.Task;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

class ManagersTest {
    @Test
    public void shouldReturnInitializedTaskManager(){
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);

        Task task = new Task("Test model.Task", "Test Description");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(10));
        manager.addTask(task);
        assertTrue(manager.getAllTasks().contains(task));
    }
    @Test
    public void shouldReturnInitializedHistoryManager(){
        HistoryManager manager = Managers.getDefaultHistory();
        assertNotNull(manager);

        Task task = new Task("Test model.Task", "Test Description");
        manager.add(task);
        assertTrue(manager.getHistory().contains(task));
    }

}