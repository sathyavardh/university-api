package org.sathya.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.sathya.dao.TeacherDAO;
import org.sathya.model.Teacher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class TeacherHandler implements HttpHandler {

    private final Gson gson = new Gson();
    private final TeacherDAO dao = new TeacherDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery(); // id=1
        String idParam = (query != null && query.startsWith("id=")) ? query.substring(3) : null;

        try {
            switch (method.toUpperCase()) {
                case "GET":
                    if (idParam != null) {
                        int id = Integer.parseInt(idParam);
                        Teacher teacher = dao.getById(id);
                        if (teacher != null)
                            respond(exchange, 200, "application/json", gson.toJson(teacher));
                        else
                            respond(exchange, 404, "application/json", "{\"error\":\"Teacher not found\"}");
                    } else {
                        List<Teacher> teachers = dao.getAll();
                        respond(exchange, 200, "application/json", gson.toJson(teachers));
                    }
                    break;

                case "POST":
                    String postBody = readRequestBody(exchange);
                    Teacher newTeacher = gson.fromJson(postBody, Teacher.class);
                    dao.save(newTeacher);
                    respond(exchange, 201, "application/json", "{\"status\":\"created\"}");
                    break;

                case "PUT":
                    if (idParam == null) {
                        respond(exchange, 400, "application/json", "{\"error\":\"ID required for update\"}");
                        return;
                    }
                    String putBody = readRequestBody(exchange);
                    Teacher updatedTeacher = gson.fromJson(putBody, Teacher.class);
                    updatedTeacher.setId(Integer.parseInt(idParam));
                    dao.update(updatedTeacher);
                    respond(exchange, 200, "application/json", "{\"status\":\"updated\"}");
                    break;

                case "DELETE":
                    if (idParam == null) {
                        respond(exchange, 400, "application/json", "{\"error\":\"ID required for delete\"}");
                        return;
                    }
                    int deleteId = Integer.parseInt(idParam);
                    boolean deleted = dao.delete(deleteId);
                    if (deleted)
                        respond(exchange, 200, "application/json", "{\"status\":\"deleted\"}");
                    else
                        respond(exchange, 404, "application/json", "{\"error\":\"Teacher not found\"}");
                    break;

                default:
                    respond(exchange, 405, "text/plain", "Method Not Allowed");
            }
        } catch (NumberFormatException e) {
            respond(exchange, 400, "application/json", "{\"error\":\"Invalid ID format\"}");
        } catch (Exception e) {
            respond(exchange, 500, "application/json", "{\"error\":\"Internal Server Error\"}");
            e.printStackTrace();
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        return new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining());
    }

    private void respond(HttpExchange exchange, int status, String contentType, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
