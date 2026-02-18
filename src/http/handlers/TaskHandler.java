package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.TaskValidationException;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TaskHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String cleanedPath = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        path = cleanedPath;
        try {
            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    sendBadRequest(exchange, "Метод не поддерживается");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendBadRequest(exchange, "Ошибка при обработке запроса");
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        String[] segments = path.split("/");
        if (segments.length == 2) {
            List<Task> tasks = manager.getTasks();
            sendText(exchange, gson.toJson(tasks));
        } else if (segments.length == 3) {
            try {
                int id = Integer.parseInt(segments[2]);
                Task task = manager.getTaskById(id);
                if (task != null) {
                    sendText(exchange, gson.toJson(task));
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendBadRequest(exchange, "Некорректный ID");
            }
        } else {
            sendBadRequest(exchange, "Неверный путь");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);

        try {
            if (task.getId() != 0 && manager.getTaskById(task.getId()) != null) {
                manager.updateTask(task);
            } else {
                manager.addTask(task);
            }
            sendCreated(exchange);
        } catch (TaskValidationException e) {
            sendHasIntersection(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        String[] segments = path.split("/");
        if (segments.length == 2) {
            manager.deleteAllTasks();
            sendText(exchange, "Все задачи удалены");
        } else if (segments.length == 3) {
            try {
                int id = Integer.parseInt(segments[2]);
                Task task = manager.getTaskById(id);
                if (task != null) {
                    manager.deleteTaskById(id);
                    sendText(exchange, "Задача с ID " + id + " удалена");
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendBadRequest(exchange, "Некорректный ID");
            }
        } else {
            sendBadRequest(exchange, "Неверный путь");
        }
    }
}
