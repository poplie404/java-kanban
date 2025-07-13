import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subTaskIds;
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

    public Epic(String name, String description) {
        super(name, description);
        this.subTaskIds = new ArrayList<>();
    }

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(ArrayList<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTaskIds=" + subTaskIds +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + (startTime != null ? startTime.format(formatter) : "null") +
                ", endTime=" + (endTime != null ? endTime.format(formatter) : "null") +
                ", duration=" + (duration != null ? duration.toMinutes() : "null") +
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

    public TaskType getType() {
        return TaskType.EPIC; // или EPIC, или SUBTASK
    }

}
