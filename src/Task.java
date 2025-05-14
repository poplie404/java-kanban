import java.util.HashMap;
import java.util.Objects;
import java.util.Arrays;

public class Task {
    String name;
    String description;
    int id;
    TaskStatus status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = status.NEW;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }


    public static void printAllTasks(HashMap<Integer, Task> tasks){
        for (Task task : tasks.values()){
            System.out.println(task.toString());
        }
    }


    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, id, description, status);
        result = 31 * result;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(status, task.status);

    }


}
