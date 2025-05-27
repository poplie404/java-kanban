import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    public void shouldBeEqualBySubTaskId(){
        SubTask sub1 = new SubTask("Sub1", "Подзадача1", 2);
        SubTask sub2 = new SubTask("Sub1", "Подзадача1", 2);
        sub2.setId(sub1.id);
        assertEquals(sub1, sub2);
    }
    @Test
    public void shouldNotBeAddedAsEpic(){
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        SubTask sub1 = new SubTask("n1", "d1", 42);
        sub1.setId(42);
        taskManager.addSubTask(sub1);
        assertTrue(taskManager.getSubTasks().isEmpty());
    }
}