import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    List<Task> getHistory();

    List<Task> getAllTasks();

    List<Task> getTasks();

    List<SubTask> getSubTasks();

    List<Epic> getEpics();

    void deleteAllTasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubTaskById(int id);

    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(SubTask subtask);

    void updateTask(Task task);

    void updateSubTask(SubTask subtask);

    void updateEpic(Epic epic);

    List<SubTask> getAllSubTasksInEpic(Epic epic);

}
