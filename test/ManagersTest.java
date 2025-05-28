import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ManagersTest {
    @Test
    public void shouldReturnInitializedTaskManager(){
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);

        Task task = new Task("Test Task", "Test Description");
        manager.addTask(task);
        assertTrue(manager.getAllTasks().contains(task));
    }
    @Test
    public void shouldReturnInitializedHistoryManager(){
        HistoryManager manager = Managers.getDefaultHistory();
        assertNotNull(manager);

        Task task = new Task("Test Task", "Test Description");
        manager.add(task);
        assertTrue(manager.getHistory().contains(task));
    }

}