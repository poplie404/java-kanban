import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    public static HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public static void setTasks(HashMap<Integer, Task> tasks) {
        TaskManager.tasks = tasks;
    }

    public static HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public static void setSubTasks(HashMap<Integer, SubTask> subTasks) {
        TaskManager.subTasks = subTasks;
    }

    public static HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public static void setEpics(HashMap<Integer, Epic> epics) {
        TaskManager.epics = epics;
    }

    static HashMap<Integer, Task> tasks = new HashMap<>();
    static HashMap<Integer, SubTask> subTasks = new HashMap<>();
    static HashMap<Integer, Epic> epics = new HashMap<>();
    static int currentId = 0;

    public static void printAllTasks(){
        Task.printAllTasks(tasks);
        Epic.printAllEpics(epics);
        SubTask.printAllSubTasks(subTasks);

    }

    public static void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subTasks.clear();
    }

    public static Task getTaskById(int id){
        if (tasks.containsKey(id)){
            return tasks.get(id);
        }else if (epics.containsKey(id)) {
             return epics.get(id);
        }else if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        } else {
            return null;
        }
    }

    public void deleteTaskById(int id){
        tasks.remove(id);
    }

    public void deleteSubtaskById(int id) {
        SubTask subtask = subTasks.get(id);
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);
            if (epic != null) {
                epic.getSubTaskIds().remove(Integer.valueOf(id));
                epic.updateEpicStatus(epic); // пересчёт статуса эпика
            }
            subTasks.remove(id);
        }
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubTaskIds()) {
                subTasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    public void addTask(Task task) {
        task.setId(currentId++);
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epic.setId(currentId++);
        epic.setSubTaskIds(new ArrayList<>());
        epics.put(epic.getId(), epic);
    }

    public void addSubTask(SubTask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            System.out.println("Эпик с ID " + epicId + " не найден.");
            return;
        }
        subtask.setId(currentId++);
        subTasks.put(subtask.getId(), subtask);
        epic.getSubTaskIds().add(subtask.getId());
        epic.updateEpicStatus(epic);
    }

    public static void changeTaskStatus(int id, TaskStatus status){
        if (tasks.containsKey(id)){
            Task task = tasks.get(id);
            task.setStatus(status);
        }else if (epics.containsKey(id)) {
            System.out.println("Нельзя менять статус эпика вручную");
        }else if (subTasks.containsKey(id)) {
            SubTask subtask = subTasks.get(id);
            Epic epic = epics.get(subtask.getEpicId());
            subtask.setStatus(status);
            epic.updateEpicStatus(epic);
        }else {
            System.out.println("Нет такого id");
        }
    }

}
