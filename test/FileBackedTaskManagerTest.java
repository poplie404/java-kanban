import managers.file.FileBackedTaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
        public void setup() throws IOException {
            // создаём временный файл
            tempFile = File.createTempFile("tasks", ".csv");
            manager = new FileBackedTaskManager(tempFile);

        }


    @AfterEach
        public void clean() {
            if (tempFile.exists()){
                tempFile.delete();
            }
        }


    @Test
    public void shouldAddAndLoadSingleTask() {
        Task task = new Task("name", "desc");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(10));

        manager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> loadedTasks = loadedManager.getTasks();

        assertEquals(1, loadedTasks.size());

        Task loadedTask = loadedTasks.get(0);
        assertEquals(task.getName(), loadedTask.getName());
        assertEquals(task.getId(), loadedTask.getId());
        assertEquals(task.getDescription(), loadedTask.getDescription());
        assertEquals(task.getStatus(), loadedTask.getStatus());

    }
    @Test
    public void shouldAddAndLoadEpicWithSubTask() {

        Epic epic = new Epic("name", "desc");
        manager.addEpic(epic); // менеджер присвоит ID

        int epicId = epic.getId();


        SubTask subTask = new SubTask("subName", "subDesc", epicId);
        subTask.setId(1);
        subTask.setStartTime(LocalDateTime.now());
        subTask.setDuration(Duration.ofMinutes(10));
        manager.addSubTask(subTask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Epic> loadedEpics = loadedManager.getEpics();
        List<SubTask> loadedSubTasks = loadedManager.getSubTasks();

        assertEquals(1, loadedEpics.size());
        assertEquals(1, loadedSubTasks.size());

        Epic loadedEpic = loadedEpics.get(0);
        SubTask loadedSubTask = loadedSubTasks.get(0);

        assertEquals(subTask.getName(), loadedSubTask.getName());
        assertEquals(subTask.getId(), loadedSubTask.getId());
        assertEquals(subTask.getDescription(), loadedSubTask.getDescription());
        assertEquals(subTask.getStatus(), loadedSubTask.getStatus());
        assertEquals(subTask.getEpicId(), loadedSubTask.getEpicId());

        assertEquals(epic.getName(), loadedEpic.getName());
        assertEquals(epic.getId(), loadedEpic.getId());
        assertEquals(epic.getDescription(), loadedEpic.getDescription());
        assertEquals(epic.getStatus(), loadedEpic.getStatus());
        assertArrayEquals(epic.getSubTaskIds().toArray(), loadedEpic.getSubTaskIds().toArray());
    }

    @Test
    public void shouldReturnEmptyManager() {
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loaded.getSubTasks().isEmpty());
        assertTrue(loaded.getTasks().isEmpty());
        assertTrue(loaded.getEpics().isEmpty());
    }

    @Test
    public void shouldReturnUpdatedTasks() {
        Task task = new Task("name", "desc");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(10));
        manager.addTask(task);

        task.setName("New name");
        manager.updateTask(task);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loaded.getTaskById(task.getId());

        assertEquals(task.getName(), loadedTask.getName());
        assertEquals(task.getId(), loadedTask.getId());
        assertEquals(task.getDescription(), loadedTask.getDescription());
        assertEquals(task.getStatus(), loadedTask.getStatus());
    }

    @Test
    public void shouldRestoreCurrentId() {
        Task task1 = new Task("Task1", "Desc1");
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(10));
        manager.addTask(task1);

        Epic epic = new Epic("model.Epic", "EpicDesc");
        manager.addEpic(epic);

        SubTask sub = new SubTask("Sub", "SubDesc", epic.getId());
        sub.setStartTime(LocalDateTime.now().plusMinutes(20));
        sub.setDuration(Duration.ofMinutes(10));
        manager.addSubTask(sub);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        Task task2 = new Task("Task2", "Desc2");
        task2.setStartTime(LocalDateTime.now().plusDays(20));
        task2.setDuration(Duration.ofMinutes(10));
        loaded.addTask(task2);

        assertEquals(3, task2.getId(), "ID после загрузки должен продолжаться с правильного значения");
    }
}

