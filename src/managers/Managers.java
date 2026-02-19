package managers;

import managers.memory.InMemoryHistoryManager;
import managers.memory.InMemoryTaskManager;
import service.HistoryManager;
import service.TaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
