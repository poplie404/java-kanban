import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, SubTask> subTasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected int currentId = 0;
    private HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime,
                            Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparingInt(Task::getId)
    );

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> all = new ArrayList<>();
        all.addAll(getTasks());
        all.addAll(getEpics());
        all.addAll(getSubTasks());
        return all;
    }

    public static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        manager.getTasks().stream()
                        .peek(System.out::println)
                        .toList();
        System.out.println("Эпики:");
        manager.getEpics().stream()
                .peek(System.out::println)
                .flatMap(epic -> manager.getAllSubTasksInEpic(epic).stream()
                        .peek(subtask -> System.out.println("--> " + subtask)))
                .toList();
        System.out.println("Подзадачи:");
        manager.getSubTasks().stream()
                        .peek(System.out::println)
                                .toList();

        System.out.println("История:");
        manager.getHistory().stream()
                .peek(System.out::println)
                .toList();
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
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }


    @Override
    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);

        } else {
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
    public void deleteTaskById(int id) {
        historyManager.remove(id);
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
                updateEpic(epic); // пересчёт статуса эпика
            }
            subTasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            epic.getSubTaskIds().stream()
                    .map(subTaskId -> {
                        prioritizedTasks.remove(subTasks.get(subTaskId));
                        historyManager.remove(subTaskId);
                        return subTasks.remove(subTaskId);
                    })
                    .toList();
            prioritizedTasks.remove(epic);
            historyManager.remove(id);
            epics.remove(id);
        }
    }


    @Override
    public void addTask(Task task) {
        if (isIntersection(task)) {
            throw new TaskValidationException("Ошибка! Время не может пересекаться у задач");
        }

        task.setId(currentId++);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(currentId++);
        epic.setSubTaskIds(new ArrayList<>());
        updateEpicTime(epic);
        epics.put(epic.getId(), epic);
        prioritizedTasks.add(epic);
    }

    @Override
    public void addSubTask(SubTask subtask)  {
        if (isIntersection(subtask)) {
            throw new TaskValidationException("Ошибка! Время не может пересекаться у задач");
        }

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
        updateEpic(epic);
        prioritizedTasks.remove(epic);
        updateEpicTime(epic);
        prioritizedTasks.add(epic);
        prioritizedTasks.add(subtask);

    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            prioritizedTasks.remove(tasks.get(task.getId()));

            if (isIntersection(task)) {
                prioritizedTasks.add(tasks.get(task.getId()));
                throw new TaskValidationException("Ошибка! Время не может пересекаться у задач");
            }

            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }
    }


    @Override
    public void updateSubTask(SubTask subtask) {
        if (subTasks.containsKey(subtask.getId()) && subtask.getId() != subtask.getEpicId()) {
            prioritizedTasks.remove(subTasks.get(subtask.getId()));

            if (!isIntersection(subtask)) {
                prioritizedTasks.add(subTasks.get(subtask.getId()));
                throw new TaskValidationException("Ошибка! Время не может пересекаться у задач");
            }

            subTasks.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpic(epic);
                prioritizedTasks.remove(epic);
                updateEpicTime(epic);
                prioritizedTasks.add(epic);
            }
        }
    }


    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic); // Обновим статус эпика на основе его подзадач
            updateEpicTime(epic);
            prioritizedTasks.remove(epic);
            prioritizedTasks.add(epic);
        }
    }


    @Override
    public List<SubTask> getAllSubTasksInEpic(Epic epic) {
        return epic.getSubTaskIds().stream()
                .map(subTasks::get)
                .collect(Collectors.toList());
    }

    private void updateEpicStatus(Epic epic) {
        List<Integer> subtaskIds = epic.getSubTaskIds();

        if (subtaskIds == null || subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            epics.put(epic.getId(), epic);
            return;
        }

        boolean allDone = subtaskIds.stream()
                .map(id -> subTasks.get(id).getStatus())
                .allMatch(status -> status == TaskStatus.DONE);
        boolean allNew = subtaskIds.stream()
                .map(id -> subTasks.get(id).getStatus())
                .allMatch(status -> status == TaskStatus.NEW);



        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
        epics.put(epic.getId(), epic);

    }

    private void updateEpicTime(Epic epic) {
        List<SubTask> subtasks = epic.getSubTaskIds().stream()
                .map(subTasks::get)
                .filter(Objects::nonNull)
                .filter(s -> s.getStartTime() != null && s.getDuration() != null)
                .toList();

        if (subtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            return;
        }

        LocalDateTime earliestStart = subtasks.stream()
                .map(SubTask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime latestEnd = subtasks.stream()
                .map(SubTask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setStartTime(earliestStart);
        epic.setDuration(Duration.between(earliestStart, latestEnd));
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        prioritizedTasks.clear();

        prioritizedTasks.addAll(tasks.values());
        prioritizedTasks.addAll(subTasks.values());
        prioritizedTasks.addAll(epics.values());

        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isIntersection(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getEndTime() == null) {
            return false; // Не сравниваем задачи без времени
        }

        return prioritizedTasks.stream()
                .filter(task -> task.getId() != newTask.getId())
                .filter(task -> task.getStartTime() != null && task.getEndTime() != null)
                .anyMatch(task ->
                        !(newTask.getEndTime().isBefore(task.getStartTime()) ||
                                newTask.getStartTime().isAfter(task.getEndTime()))
                );
    }


}
