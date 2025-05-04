package com.mycompany.demojdpcmaven;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.json.JSONArray;
import org.json.JSONObject;

public class AccountsHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        // CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("GET".equalsIgnoreCase(method)) {
            handleGetAccounts(exchange);
        } else if ("POST".equalsIgnoreCase(method)) {
            handleRegisterAccount(exchange);
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }

    private void handleGetAccounts(HttpExchange exchange) throws IOException {
        JSONArray jsonArray = DatabaseUtil.getAccountsJson();
        String response = jsonArray.toString();

        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    private void handleRegisterAccount(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        JSONObject requestJson = new JSONObject(body);
        String username = requestJson.getString("username");
        String password = requestJson.getString("password");
        String role = "user";

        String fullName = requestJson.optString("fullName");
        String email = requestJson.optString("email");
        String phone = requestJson.optString("phone");

        boolean result = DatabaseUtil.registerAccountAndUser(username, password, role, fullName, email, phone);

        JSONObject responseJson = new JSONObject();
        responseJson.put("success", result);
        if (result) {
            responseJson.put("message", "Đăng ký thành công!");
        } else {
            responseJson.put("message", "Tài khoản đã tồn tại hoặc lỗi hệ thống.");
        }

        String response = responseJson.toString();
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}
