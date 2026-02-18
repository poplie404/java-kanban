package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public HistoryHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (!"/history".equals(path)) {
            sendBadRequest(exchange, "Некорректный путь");
            return;
        }

        if ("GET".equals(method)) {
            sendText(exchange, gson.toJson(manager.getHistory()));
        } else {
            sendBadRequest(exchange, "Метод не поддерживается");
        }
    }
}
