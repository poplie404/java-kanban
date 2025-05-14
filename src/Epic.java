import java.util.HashMap;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task{
    ArrayList<Integer> subTaskIds;

    public Epic(String name, String description) {
        super(name, description);

    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(ArrayList<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }



    public static void updateEpicStatus(Epic epic){
        ArrayList<Integer> subtaskIds = epic.getSubTaskIds();
        if (subtaskIds.isEmpty()){
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;
        for(int subTaskId : subtaskIds){
            TaskStatus subTaskStatus = TaskManager.subTasks.get(subTaskId).getStatus();

            if (subTaskStatus != TaskStatus.DONE){
                allDone = false;
            }
            if (subTaskStatus != TaskStatus.NEW){
                allNew = false;
            }
        }

        if (allDone){
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        }
        else{
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public static void printAllEpics(HashMap<Integer, Epic> epics){
        for (Epic epic : epics.values()){
            System.out.println(epic.toString());
        }
    }

    public static void printAllSubTasksInEpic(Epic epic){
        for (int i : epic.subTaskIds){
            System.out.println(TaskManager.subTasks.get(i).toString());
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTaskIds=" + subTaskIds +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, id, description, status);
        result = 31 * result + Objects.hashCode(subTaskIds);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return id == epic.id &&
                Objects.equals(name, epic.name) &&
                Objects.equals(description, epic.description) &&
                Objects.equals(status, epic.status) &&
                Objects.equals(subTaskIds, epic.subTaskIds);
    }

}
