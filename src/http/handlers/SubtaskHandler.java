package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.TaskValidationException;
import model.SubTask;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtaskHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
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
            sendBadRequest(exchange, "Ошибка при обработке запроса");
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        String[] segments = path.split("/");
        if (segments.length == 2) {
            List<SubTask> subtasks = manager.getSubTasks();
            sendText(exchange, gson.toJson(subtasks));
        } else if (segments.length == 3) {
            try {
                int id = Integer.parseInt(segments[2]);
                SubTask subtask = manager.getSubTaskById(id);
                if (subtask != null) {
                    sendText(exchange, gson.toJson(subtask));
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
        SubTask subTask = gson.fromJson(body, SubTask.class);

        try {
            if (subTask.getId() != 0 && manager.getSubTaskById(subTask.getId()) != null) {
                manager.updateSubTask(subTask);
            } else {
                manager.addSubTask(subTask);
            }
            sendCreated(exchange);
        } catch (TaskValidationException e) {
            sendHasIntersection(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        String[] segments = path.split("/");
        if (segments.length == 3) {
            try {
                int id = Integer.parseInt(segments[2]);
                SubTask subtask = manager.getSubTaskById(id);
                if (subtask != null) {
                    manager.deleteSubtaskById(id);
                    sendText(exchange, "Подзадача с ID " + id + " удалена");
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
