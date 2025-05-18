import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private int currentId = 0;



    public ArrayList<Task> getAllTasks(){
        ArrayList<Task> all = new ArrayList<>();
        all.addAll(getTasks());
        all.addAll(getEpics());
        all.addAll(getSubTasks());
        return all;
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }


    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }


    public List <Epic> getEpics() {
        return new ArrayList<>(epics.values());
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

    public void updateTask(Task task){
        if (tasks.containsKey(task.getId())){
            tasks.put(task.getId(), task);
        }
    }

    public void updateSubTask(SubTask subtask) {
        if (subTasks.containsKey(subtask.getId())) {
            subTasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic); // Обновим статус эпика на основе его подзадач
        }
    }


    public ArrayList<SubTask> getAllSubTasksInEpic(Epic epic){
        ArrayList<SubTask> result = new ArrayList<>();
        for (int i : epic.getSubTaskIds()){
            result.add(subTasks.get(i));
            System.out.println(subTasks.get(i).toString());
        }
        return result;
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtaskIds = epic.getSubTaskIds();

        if (subtaskIds == null || subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            epics.put(epic.getId(), epic);
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
        epics.put(epic.getId(), epic);
    }

}
