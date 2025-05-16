import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, SubTask> subTasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    int currentId = 0;



    public ArrayList<Task> printAllTasks(){
        ArrayList<Task> all = new ArrayList<>();
        all.addAll(printAllSimpleTasks(tasks));
        all.addAll(printAllEpics(epics));
        all.addAll(printAllSubTasks(subTasks));
        return all;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }


    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }


    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }


    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subTasks.clear();
    }

    public Task getTaskById(int id){
        if (tasks.containsKey(id)){
            return tasks.get(id);
        }else {
            return null;
        }
    }
    public  Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);

        }else {
            return null;
        }
    }

    public  SubTask getSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
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
                updateEpicStatus(epic); // пересчёт статуса эпика
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
        updateEpicStatus(epic);
    }

    public void changeTaskStatus(int id, TaskStatus status){
        if (tasks.containsKey(id)){
            Task task = tasks.get(id);
            task.setStatus(status);
        }else {
            System.out.println("Нет такого id");
        }
    }

    public void changeSubTaskStatus(int id, TaskStatus status){
        if (subTasks.containsKey(id)) {
            SubTask subtask = subTasks.get(id);
            Epic epic = epics.get(subtask.getEpicId());
            subtask.setStatus(status);
            updateEpicStatus(epic);
        }else {
            System.out.println("Нет такого id");
        }
    }

    public ArrayList<Epic> printAllEpics(HashMap<Integer, Epic> epics){//кроме печати они возвращают
        ArrayList<Epic> result = new ArrayList<>();
        for (Epic epic : epics.values()){
            result.add(epic);
            System.out.println(epic.toString());
        }
        return result;
    }

    public ArrayList<SubTask> printAllSubTasksInEpic(Epic epic){
        ArrayList<SubTask> result = new ArrayList<>();
        for (int i : epic.getSubTaskIds()){
            result.add(subTasks.get(i));
            System.out.println(subTasks.get(i).toString());
        }
        return result;
    }

    public void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtaskIds = epic.getSubTaskIds();

        if (subtaskIds == null || subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (int subTaskId : subtaskIds) {
            TaskStatus subTaskStatus = subTasks.get(subTaskId).getStatus();

            if (subTaskStatus != TaskStatus.DONE) {
                allDone = false;
            }
            if (subTaskStatus != TaskStatus.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }


    public static ArrayList<SubTask> printAllSubTasks(HashMap<Integer, SubTask> subtasks){
        ArrayList<SubTask> result = new ArrayList<>();
        for (SubTask subtask : subtasks.values()){
            result.add(subtask);
            System.out.println(subtask.toString());
        }
        return result;
    }

    public static ArrayList<Task> printAllSimpleTasks(HashMap<Integer, Task> tasks){
        ArrayList<Task> result = new ArrayList<>();
        for (Task task : tasks.values()){
            System.out.println(task.toString());
            result.add(task);
        }
        return result;
    }



}
