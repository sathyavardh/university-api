package org.sathya;

import com.sun.net.httpserver.HttpServer;
import org.sathya.handler.DepartmentHandler;
import org.sathya.handler.StudentHandler;
import org.sathya.handler.CourseHandler;
import org.sathya.handler.TeacherHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SimpleHttpServer {

    public static void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(9090), 0);
        System.out.println("Server running on http://localhost:9090");

        //API handler
        server.createContext("/students", new StudentHandler());
        server.createContext("/courses", new CourseHandler());
        server.createContext("/departments", new DepartmentHandler());
        server.createContext("/teachers", new TeacherHandler());

        // Root welcome handler
        server.createContext("/", exchange -> {
            String response = "Java HTTP Server is Running!";
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });

        server.start();
    }
}
