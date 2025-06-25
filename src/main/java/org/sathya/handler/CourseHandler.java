package org.sathya.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.sathya.dao.CourseDAO;
import org.sathya.model.Course;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class CourseHandler implements HttpHandler {

    private final Gson gson = new Gson();
    private final CourseDAO dao = new CourseDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery(); // e.g. ?id=2
        String idParam = (query != null && query.startsWith("id=")) ? query.substring(3) : null;

        try {
            switch (method.toUpperCase()) {
                case "GET":
                    if (idParam != null) {
                        int id = Integer.parseInt(idParam);
                        Course course = dao.getById(id);
                        if (course != null)
                            respond(exchange, 200, "application/json", gson.toJson(course));
                        else
                            respond(exchange, 404, "application/json", "{\"error\":\"Course not found\"}");
                    } else {
                        List<Course> courses = dao.getAll();
                        respond(exchange, 200, "application/json", gson.toJson(courses));
                    }
                    break;

                case "POST":
                    String postBody = readRequestBody(exchange);
                    Course newCourse = gson.fromJson(postBody, Course.class);
                    dao.save(newCourse);
                    respond(exchange, 201, "application/json", "{\"status\":\"created\"}");
                    break;

                case "PUT":
                    if (idParam == null) {
                        respond(exchange, 400, "application/json", "{\"error\":\"ID required for update\"}");
                        return;
                    }
                    String putBody = readRequestBody(exchange);
                    Course updatedCourse = gson.fromJson(putBody, Course.class);
                    updatedCourse.setId(Integer.parseInt(idParam));
                    dao.update(updatedCourse);
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
                        respond(exchange, 404, "application/json", "{\"error\":\"Course not found\"}");
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
