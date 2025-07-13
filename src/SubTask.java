import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class SubTask extends Task {
    private int epicId;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy, HH:mm");


    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }


    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        updateEndTime();
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
        updateEndTime();
    }

    private void updateEndTime() {
        if (startTime != null && duration != null) {
            this.endTime = startTime.plus(duration);
        }
    }

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String start = (startTime != null) ? startTime.format(formatter) : "null";
        String end = (endTime != null) ? endTime.format(formatter) : "null";
        String dur = (duration != null) ? duration.toString() : "null";

        return "SubTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + start +
                ", endTime=" + end +
                ", duration=" + dur +
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
        return TaskType.SUBTASK; // или EPIC, или SUBTASK
    }
}



