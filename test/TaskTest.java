import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    public void shouldBeEqualByTaskId(){
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 1", "Описание задачи 1");
        task2.setId(0);
        assertEquals(task1, task2);
    }


}