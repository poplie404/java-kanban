import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private int currentId = 0;
    private HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    @Override
    public ArrayList<Task> getAllTasks(){
        ArrayList<Task> all = new ArrayList<>();
        all.addAll(getTasks());
        all.addAll(getEpics());
        all.addAll(getSubTasks());
        return all;
    }

    public static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getAllSubTasksInEpic((Epic)epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }


    @Override
    public List <Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }


    @Override
    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Task getTaskById(int id){
        if (tasks.containsKey(id)){
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        }else {
            return null;
        }
    }
    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);

        }else {
            return null;
        }
    }

    @Override
    public SubTask getSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            historyManager.add(subTasks.get(id));
            return subTasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public void deleteTaskById(int id){
        tasks.remove(id);
    }

    @Override
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

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubTaskIds()) {
                subTasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    @Override
    public void addTask(Task task) {
        task.setId(currentId++);
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(currentId++);
        epic.setSubTaskIds(new ArrayList<>());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubTask(SubTask subtask) {
        int epicId = subtask.getEpicId();

        // Защита от самоссылки
        if (subtask.getId() == epicId) {
            System.out.println("Ошибка: Подзадача не может быть своим же эпиком.");
            return;
        }

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

    @Override
    public void updateTask(Task task){
        if (tasks.containsKey(task.getId())){
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        if (subTasks.containsKey(subtask.getId()) && subtask.getId() != subtask.getEpicId()) {
            subTasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic); // Обновим статус эпика на основе его подзадач
        }
    }


    @Override
    public ArrayList<SubTask> getAllSubTasksInEpic(Epic epic){
        ArrayList<SubTask> result = new ArrayList<>();
        for (int i : epic.getSubTaskIds()){
            result.add(subTasks.get(i));
            System.out.println(subTasks.get(i).toString());
        }
        return result;
    }

    void updateEpicStatus(Epic epic) {
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
