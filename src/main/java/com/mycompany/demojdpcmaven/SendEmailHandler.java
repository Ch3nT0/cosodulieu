package com.mycompany.demojdpcmaven;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SendEmailHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        try {
            // Đọc request body
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            reader.close();

            // Parse JSON
            JSONObject requestJson = new JSONObject(requestBody.toString());
            String to = requestJson.getString("to");
            String subject = requestJson.getString("subject");
            String body = requestJson.getString("body");

            // Gửi email
            boolean success = SendEmail.sendEmail(to, subject, body);

            // Trả kết quả JSON
            JSONObject responseJson = new JSONObject();
            responseJson.put("success", success);

            byte[] response = responseJson.toString().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(success ? 200 : 500, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();

        } catch (Exception e) {
            e.printStackTrace();

            JSONObject error = new JSONObject();
            error.put("success", false);
            error.put("error", e.getMessage());

            byte[] response = error.toString().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }
}
