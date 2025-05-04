package com.mycompany.demojdpcmaven;

import java.io.IOException;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

public class LoginHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String requestBody = new String(exchange.getRequestBody().readAllBytes());
        JSONObject requestJson = new JSONObject(requestBody);
        String username = requestJson.getString("username");
        String password = requestJson.getString("password");

        boolean loginSuccess = DatabaseUtil.checkLogin(username, password);
        JSONObject responseJson = new JSONObject();

        if (loginSuccess) {
            responseJson.put("success", true);
            responseJson.put("message", "Đăng nhập thành công");

            // Lấy role từ username
            String role = DatabaseUtil.getRole(username);

            if ("admin".equalsIgnoreCase(role)) {
                // Nếu là admin, chỉ trả về role và isAdmin = true
                responseJson.put("role", "admin");
                responseJson.put("isAdmin", true);
            } else {
                // Nếu là user, trả về user info và isAdmin = false
                int accountID = DatabaseUtil.getAccountID(username);
                int userID = DatabaseUtil.getUserID(accountID);
                JSONObject userJson = DatabaseUtil.getUserInfo(accountID);
                userJson.put("role", role); // thêm role vào userJson

                responseJson.put("user", userJson);
                responseJson.put("userId", userID);
                responseJson.put("isAdmin", false);
            }

        } else {
            responseJson.put("success", false);
            responseJson.put("message", "Sai tài khoản hoặc mật khẩu");
        }

        String response = responseJson.toString();
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
