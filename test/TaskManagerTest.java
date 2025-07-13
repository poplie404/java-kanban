import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        manager = createTaskManager();
    }

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
    void addToHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Test Task", "Test Description");

        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }

    @Test
    public void shouldRemoveTaskById() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = new Task("Task", "Simple task");
        manager.addTask(task);
        manager.deleteTaskById(task.getId());
        assertNull(manager.getTaskById(task.getId()), "Задача должна быть удалена");
    }

    @Test
    public void shouldRemoveSubtaskById() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic", "Main Epic");
        manager.addEpic(epic);

        SubTask subTask1 = new SubTask("Sub1", "Desc", epic.getId());
        subTask1.setId(10);
        SubTask subTask2 = new SubTask("Sub2", "Desc", epic.getId());
        subTask2.setId(20);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        manager.deleteSubtaskById(subTask1.getId());
        assertNull(manager.getSubTaskById(subTask1.getId()), "Подзадача должна быть удалена");

        Epic updatedEpic = manager.getEpicById(epic.getId());
        List<Integer> subtaskIds = updatedEpic.getSubTaskIds();
        assertFalse(subtaskIds.contains(subTask1.getId()), "ID подзадачи должен быть удалён из эпика");
    }

    @Test
    public void shouldRemoveEpicByIdAndAllItsSubtasks() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic", "Main Epic");
        manager.addEpic(epic);

        SubTask subTask1 = new SubTask("Sub1", "Desc", epic.getId());
        subTask1.setId(10);
        SubTask subTask2 = new SubTask("Sub2", "Desc", epic.getId());
        subTask2.setId(20);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        manager.deleteEpicById(epic.getId());

        assertNull(manager.getEpicById(epic.getId()), "Эпик должен быть удалён");
        assertNull(manager.getSubTaskById(subTask1.getId()), "Подзадача 1 должна быть удалена вместе с эпиком");
        assertNull(manager.getSubTaskById(subTask2.getId()), "Подзадача 2 должна быть удалена вместе с эпиком");
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

    @Test
    public void shouldThrowExceptionWhenAddingOverlappingTask() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Task 1", "Description 1");
        task1.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        task1.setDuration(Duration.ofMinutes(60));
        task1.setId(100);
        manager.addTask(task1);

        Task task2 = new Task("Task 1", "Description 1");
        task2.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 30));
        task2.setDuration(Duration.ofMinutes(60));
        task2.setId(200);

        assertThrows(TaskValidationException.class,
                () -> manager.addTask(task2),
                "Должно быть выброшено исключение при пересечении задач");
    }
}




