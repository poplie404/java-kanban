import com.google.gson.Gson;
import http.HttpTaskServer;
import managers.Managers;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HttpTaskServerTest {
    private HttpTaskServer server;
    private TaskManager manager;
    private Gson gson;
    private final HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    public void startServer() throws IOException {
        manager = Managers.getDefault();
        server = new HttpTaskServer(manager);
        server.start();
        gson = server.getGson();
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void shouldReturnTaskByIdOnGetRequest() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи 1");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(10));
        String json = gson.toJson(task);
        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        client.send(post, HttpResponse.BodyHandlers.ofString());

        HttpRequest get = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/0"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(get, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task returned = gson.fromJson(response.body(), Task.class);
        assertEquals(task.getName(), returned.getName());
    }

    @Test
    public void shouldDeleteTaskByIdOnDeleteRequest() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи 1");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(10));
        String json = gson.toJson(task);
        int id = task.getId();
        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        client.send(post, HttpResponse.BodyHandlers.ofString());

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/0"))
                .DELETE()
                .build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, deleteResponse.statusCode());
        assertNull(manager.getTaskById(id));
    }

    @Test
    public void shouldReturnAllTasksWithGetRequest() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(10));
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        task2.setStartTime(LocalDateTime.now().plusDays(10));
        task2.setDuration(Duration.ofMinutes(10));

        manager.addTask(task1);
        manager.addTask(task2);

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode());

        Task[] tasks = gson.fromJson(getResponse.body(), Task[].class);
        assertEquals(2, tasks.length);
        assertEquals("Задача 1", tasks[0].getName());
        assertEquals("Задача 2", tasks[1].getName());
    }

    @Test
    public void shouldReturnListOfEpicsWithGetRequest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Подзадача1");
        Epic epic2 = new Epic("Epic2", "Подзадача2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        SubTask sub1 = new SubTask("Sub1", "Подзадача1", epic1.getId());
        SubTask sub2 = new SubTask("Sub2", "Подзадача2", epic2.getId());

        sub1.setStartTime(LocalDateTime.now().plusMinutes(30));
        sub1.setDuration(Duration.ofMinutes(5));
        sub1.setId(10);
        sub2.setStartTime(LocalDateTime.now().plusMinutes(40));
        sub2.setDuration(Duration.ofMinutes(10));
        sub2.setId(20);

        manager.addSubTask(sub1);
        manager.addSubTask(sub2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic[] epics = gson.fromJson(response.body(), Epic[].class);
        assertEquals(2, epics.length);
        assertEquals("Epic1", epics[0].getName());
        assertEquals("Epic2", epics[1].getName());
    }

    @Test
    public void shouldAddEpicWithPostRequest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Описание эпика");
        SubTask sub1 = new SubTask("Sub1", "Подзадача1", epic.getId());

        sub1.setStartTime(LocalDateTime.now().plusMinutes(30));
        sub1.setDuration(Duration.ofMinutes(5));
        sub1.setId(10);

        manager.addEpic(epic);
        manager.addSubTask(sub1);
        manager.deleteEpicById(epic.getId());

        String json = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epics = manager.getEpics();
        assertEquals(1, epics.size());
        assertEquals("Epic1", epics.get(0).getName());
    }

    @Test
    public void shouldDeleteEpicWithDelteRequest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Описание эпика");
        SubTask sub1 = new SubTask("Sub1", "Подзадача1", epic.getId());

        sub1.setStartTime(LocalDateTime.now().plusMinutes(30));
        sub1.setDuration(Duration.ofMinutes(5));
        sub1.setId(10);

        manager.addEpic(epic);
        manager.addSubTask(sub1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epic.getId()))
                .DELETE()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> epics = manager.getEpics();
        assertEquals(0, epics.size());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epic.getId()))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getResponse.statusCode());

    }

    @Test
    public void shouldReturnListOfSubtasksWithGetRequest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Эпик с подзадачами");
        manager.addEpic(epic);

        SubTask sub1 = new SubTask("Sub1", "Описание 1", epic.getId());
        sub1.setStartTime(LocalDateTime.now().plusMinutes(10));
        sub1.setDuration(Duration.ofMinutes(15));
        sub1.setId(1);

        SubTask sub2 = new SubTask("Sub2", "Описание 2", epic.getId());
        sub2.setStartTime(LocalDateTime.now().plusMinutes(30));
        sub2.setDuration(Duration.ofMinutes(20));
        sub2.setId(2);

        manager.addSubTask(sub1);
        manager.addSubTask(sub2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        SubTask[] subtasks = gson.fromJson(response.body(), SubTask[].class);
        assertEquals(2, subtasks.length);
        assertEquals("Sub1", subtasks[0].getName());
        assertEquals("Sub2", subtasks[1].getName());
    }

    @Test
    public void shouldAddSubtaskWithPostRequest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Эпик для подзадачи");
        manager.addEpic(epic);

        SubTask subtask = new SubTask("Sub", "Описание", epic.getId());
        subtask.setStartTime(LocalDateTime.now().plusMinutes(20));
        subtask.setDuration(Duration.ofMinutes(10));
        subtask.setId(5);


        String json = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<SubTask> subtasks = manager.getSubTasks();
        assertEquals(1, subtasks.size());
        assertEquals("Sub", subtasks.get(0).getName());
    }

    @Test
    public void shouldDeleteSubtaskWithDeleteRequest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Эпик для удаления подзадачи");
        manager.addEpic(epic);

        SubTask subtask = new SubTask("Sub", "Описание", epic.getId());
        subtask.setStartTime(LocalDateTime.now().plusMinutes(20));
        subtask.setDuration(Duration.ofMinutes(10));
        subtask.setId(42);

        manager.addSubTask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtask.getId()))
                .DELETE()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<SubTask> subtasks = manager.getSubTasks();
        assertEquals(0, subtasks.size());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtask.getId()))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getResponse.statusCode());
    }

    @Test
    public void shouldReturnHistoryWithGetRequest() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Description1");
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(10));
        manager.addTask(task1);

        Task task2 = new Task("Task2", "Description2");
        task2.setStartTime(LocalDateTime.now().plusMinutes(15));
        task2.setDuration(Duration.ofMinutes(10));
        manager.addTask(task2);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, history.length);
        assertEquals(task1.getId(), history[0].getId());
        assertEquals(task2.getId(), history[1].getId());
    }

    @Test
    public void shouldReturnPrioritizedTasksWithGetRequest() throws IOException, InterruptedException {
        Task task1 = new Task("Early Task", "Starts first");
        task1.setStartTime(LocalDateTime.now().plusMinutes(10));
        task1.setDuration(Duration.ofMinutes(10));

        Task task2 = new Task("Later Task", "Starts later");
        task2.setStartTime(LocalDateTime.now().plusMinutes(30));
        task2.setDuration(Duration.ofMinutes(20));

        manager.addTask(task2);
        manager.addTask(task1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] prioritized = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, prioritized.length);
        assertEquals(task1.getName(), prioritized[0].getName());
        assertEquals(task2.getName(), prioritized[1].getName());
    }


}
