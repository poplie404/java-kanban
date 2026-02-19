package model;

import java.util.Objects;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + (startTime != null ? startTime.format(formatter) : "null") +
                ", endTime=" + (endTime != null ? endTime.format(formatter) : "null") +
                ", duration=" + (duration != null ? duration.toMinutes() : "null") +
                ", epicId=" + epicId +
                '}';
    }


    @Override
    public int hashCode() {
        int result = Objects.hash(name, id, description, status, epicId);
        result = 31 * result;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask subtask = (SubTask) o;
        return id == subtask.id &&
                Objects.equals(name, subtask.name) &&
                Objects.equals(description, subtask.description) &&
                Objects.equals(status, subtask.status) &&
                Objects.equals(epicId, subtask.epicId);

    }

    public TaskType getType() {
        return TaskType.SUBTASK;
    }
}



