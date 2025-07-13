import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    public void shouldBeEqualByEpicId(){
        Epic epic = new Epic("Epic1", "Подзадача1, подзадача2");
        Epic epic2 = new Epic("Epic1", "Подзадача1, подзадача2");
        epic2.setId(epic.id);
        assertEquals(epic, epic2);
    }
    @Test
    public void shouldNotBeAddedAsSubtask(){
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic1", "Подзадача1, подзадача2");
        SubTask sub1 = new SubTask("name", "desc", epic.getId());
        taskManager.addEpic(epic);
        taskManager.addSubTask(sub1);
        assertTrue(epic.getSubTaskIds().isEmpty());
    }

    @Test
    public void  shouldBeNewWhenAdded() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic1", "Подзадача1, подзадача2");
        taskManager.addEpic(epic);
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void shouldBeNewWhenSubTasksAreNew() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic1", "Подзадача1, подзадача2");
        SubTask sub1 = new SubTask("name", "desc", epic.getId());
        taskManager.addEpic(epic);
        taskManager.addSubTask(sub1);
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }
    @Test
    public void epicShouldBeDoneIfAllSubtasksAreDone() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);

        SubTask sub1 = new SubTask("Sub1", "desc", epic.getId());
        sub1.setId(20);
        SubTask sub2 = new SubTask("Sub2", "desc", epic.getId());
        sub2.setId(30);

        manager.addSubTask(sub1);
        manager.addSubTask(sub2);

        sub1.setStatus(TaskStatus.DONE);
        sub2.setStatus(TaskStatus.DONE);
        manager.updateSubTask(sub1);
        manager.updateSubTask(sub2);

        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void epicShouldBeInProgressIfAllSubtasksAreMixed() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);

        SubTask sub1 = new SubTask("Sub1", "desc", epic.getId());
        sub1.setId(20);
        SubTask sub2 = new SubTask("Sub2", "desc", epic.getId());
        sub2.setId(30);

        manager.addSubTask(sub1);
        manager.addSubTask(sub2);

        sub1.setStatus(TaskStatus.NEW);
        sub2.setStatus(TaskStatus.DONE);
        manager.updateSubTask(sub1);
        manager.updateSubTask(sub2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void epicShouldBeInProgressIfAllSubtasksAreInProgress() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);

        SubTask sub1 = new SubTask("Sub1", "desc", epic.getId());
        sub1.setId(20);
        SubTask sub2 = new SubTask("Sub2", "desc", epic.getId());
        sub2.setId(30);

        manager.addSubTask(sub1);
        manager.addSubTask(sub2);

        sub1.setStatus(TaskStatus.IN_PROGRESS);
        sub2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubTask(sub1);
        manager.updateSubTask(sub2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void epicTimeShouldBeCalculatedFromSubtasks() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic", "With time");
        manager.addEpic(epic);

        SubTask sub1 = new SubTask("Sub1", "desc", epic.getId());
        sub1.setStartTime(LocalDateTime.of(2025, 6, 1, 10, 0));
        sub1.setDuration(Duration.ofMinutes(60));
        sub1.setId(10);

        SubTask sub2 = new SubTask("Sub2", "desc", epic.getId());
        sub2.setStartTime(LocalDateTime.of(2025, 6, 1, 12, 0));
        sub2.setDuration(Duration.ofMinutes(30));
        sub2.setId(20);

        manager.addSubTask(sub1);
        manager.addSubTask(sub2);

        assertEquals(sub1.getStartTime(), epic.getStartTime());
        assertEquals(sub2.getEndTime(), epic.getEndTime());
        assertEquals(Duration.ofMinutes(150), epic.getDuration());
    }


}