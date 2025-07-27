package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (!"/prioritized".equals(path)) {
            sendBadRequest(exchange, "Некорректный путь");
            return;
        }

        if ("GET".equals(method)) {
            sendText(exchange, gson.toJson(manager.getPrioritizedTasks()));
        } else {
            sendBadRequest(exchange, "Метод не поддерживается");
        }
    }
}
