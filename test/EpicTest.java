import org.junit.jupiter.api.Test;

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

}