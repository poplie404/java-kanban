
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;


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
        if (task instanceof SubTask subTask) {
            epicID = String.valueOf(subTask.getEpicId());
        }

        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                epicID
        );
    }

    static Task fromString(String value) {
        String[] fields = value.split(",", -1); // ← -1 сохраняет пустые поля

        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String name = fields[2];
        String status = fields[3];
        String description = fields[4];

        switch (type) {
            case "TASK":
                Task task = new Task(name, description);
                task.setId(id);
                task.setStatus(TaskStatus.valueOf(status));
                return task;

            case "EPIC":
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(TaskStatus.valueOf(status));
                return epic;

            case "SUBTASK":

                if (fields.length < 6 || fields[5].isEmpty()) {
                    throw new IllegalArgumentException("SUBTASK без epicId: " + value);
                }
                int epicId = Integer.parseInt(fields[5]);
                SubTask subTask = new SubTask(name, description, epicId);
                subTask.setId(id);
                subTask.setStatus(TaskStatus.valueOf(status));
                return subTask;

            default:
                throw new IllegalArgumentException("Неизвестный тип: " + type);
        }
    }


    public void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getTasks()) {
                if (!(task instanceof SubTask)) {
                    writer.write(toString(task) + "\n");
                }
            }

            for (Epic epic : getEpics()) {
                writer.write(toString(epic) + "\n");
            }

            for (SubTask subtask : getSubTasks()) {
                writer.write(toString(subtask) + "\n");
            }
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
