import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @Test
    void add() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Test Task", "Test Description");

        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }
    @Test
    public void shouldKeepOnlyLastAppereanceInHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Test Task", "Test Description");
        task.setId(0);

        historyManager.add(task);
        task.setName("Test Task2");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals("Test Task2", history.get(0).name);

    }
    @Test
    public void shouldRemoveTaskFromHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Test Task", "Test Description");
        task.setId(0);

        historyManager.add(task);
        historyManager.remove(0);
        final List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size());
    }
    @Test
    public void shouldPreserveOrderInHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Test Task1", "Test Description");
        Task task2 = new Task("Test Task2", "Test Description");
        Task task3 = new Task("Test Task3", "Test Description");
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        final List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(1, history.get(0).getId());
        assertEquals(2, history.get(1).getId());
        assertEquals(3, history.get(2).getId());
    }
    @Test
    public void shouldReturnEmptyHistoryIfNothingAdded() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        final List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }
    @Test
    public void shouldRemoveFromBeginningMiddleAndEndCorrectly() {
        HistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task("Test Task1", "Test Description");
        task1.setId(1);
        Task task2 = new Task("Test Task2", "Test Description");
        task2.setId(2);
        Task task3 = new Task("Test Task3", "Test Description");
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(1);
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(2, history.get(0).getId());
        assertEquals(3, history.get(1).getId());

        historyManager.remove(2);
        history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(3, history.get(0).getId());

        historyManager.remove(3);
        history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }
}