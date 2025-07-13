
import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy, HH:mm");

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subtask) {
        super.addSubTask(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    private String toString(Task task) {
        String epicID = "";
        if (task.getType() == TaskType.SUBTASK) {
            SubTask subTask = (SubTask) task;
            epicID = String.valueOf(subTask.getEpicId());
        }

        String start = task.getStartTime() == null ? "" : task.getStartTime().format(formatter);
        String duration = task.getDuration() == null ? "" : String.valueOf(task.getDuration().toMinutes());

        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                start,
                duration,
                epicID
        );
    }


    static Task fromString(String value) {
        String[] fields = value.split(",", -1); // ← -1 сохраняет пустые поля

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        String startTimeStr = fields[5];
        String durationStr = fields[6];
        String epicIdStr = fields.length > 7 ? fields[7] : "";

        LocalDateTime startTime = startTimeStr.isEmpty() ? null : LocalDateTime.parse(startTimeStr);
        Duration duration = durationStr.isEmpty() ? null : Duration.ofMinutes(Long.parseLong(durationStr));

        Task task;
        switch (type) {
            case TASK:
                task = new Task(name, description);
                break;
            case EPIC:
                task = new Epic(name, description);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(epicIdStr);
                task = new SubTask(name, description, epicId);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }

        task.setId(id);
        task.setStatus(status);
        task.setStartTime(startTime);
        task.setDuration(duration);
        return task;
    }


    public void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,startTime,duration,epic\n");

            String tasksString =  getTasks().stream()
                    .filter(task -> task.getType() != TaskType.SUBTASK)
                    .map(this::toString)
                    .collect(Collectors.joining("\n"));

            String epicsString = getEpics().stream()
                    .map(this::toString)
                    .collect(Collectors.joining("\n"));

            String subtasksString = getSubTasks().stream()
                    .map(this::toString)
                    .collect(Collectors.joining("\n"));

            writer.write(tasksString + "\n" + epicsString + "\n" + subtasksString + "\n");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл: " + file.getName());
        }
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager;
        try (FileReader reader = new FileReader(file)) {
            List<String> lines = Files.readAllLines(file.toPath());
            manager = new FileBackedTaskManager(file);

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) {
                    continue;
                }
                Task newTask = fromString(line);

                switch (newTask.getType()) {
                    case TASK:
                        manager.tasks.put(newTask.getId(), newTask);
                        break;
                    case EPIC:
                        Epic epic = (Epic) newTask;
                        epic.setSubTaskIds(new ArrayList<>());
                        manager.epics.put(newTask.getId(), epic);
                        break;
                    case SUBTASK:
                        manager.subTasks.put(newTask.getId(), (SubTask) newTask);
                        int epicId = ((SubTask) newTask).getEpicId();
                        Epic subEpic = manager.epics.get(epicId);
                        if (subEpic != null) {
                            subEpic.getSubTaskIds().add(newTask.getId());
                        }
                        break;
                }
                manager.currentId = Math.max(manager.currentId, newTask.getId() + 1);
            }
            for (Epic epic : manager.getEpics()) {
                manager.updateEpic(epic);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла. Загружается пустой менеджер.");
            return new FileBackedTaskManager(file);
        }
        return manager;
    }
}
