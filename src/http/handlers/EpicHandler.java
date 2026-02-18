package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.TaskValidationException;
import model.Epic;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicHandler(TaskManager manager,Gson gson) {
        this.gson = gson;
        this.manager = manager;
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
            sendBadRequest(exchange, "Ошибка обработки запроса");
        }
    }


    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);

        try {
            if (epic.getId() != 0 && manager.getEpicById(epic.getId()) != null) {
                manager.updateEpic(epic);
            } else {
                manager.addEpic(epic);
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
                Epic epic = manager.getEpicById(id);
                if (epic != null) {
                    manager.deleteEpicById(id);
                    sendText(exchange, "Эпик с ID " + id + " удалён");
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendBadRequest(exchange, "Некорректный ID");
            }
        } else {
            sendBadRequest(exchange, "Неверный формат пути");
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        String[] segments = path.split("/");
        if (segments.length == 2) {
            List<Epic> epics = manager.getEpics();
            sendText(exchange, gson.toJson(epics));
        } else if (segments.length == 3) {
            try {
                int id = Integer.parseInt(segments[2]);
                Epic epic = manager.getEpicById(id);
                if (epic == null) {
                    sendNotFound(exchange);
                } else {
                    sendText(exchange, gson.toJson(epic));
                }
            } catch (NumberFormatException e) {
                sendBadRequest(exchange, "Некорректный ID");
            }
        } else {
            sendBadRequest(exchange, "Неверный формат запроса");
        }
    }
}

