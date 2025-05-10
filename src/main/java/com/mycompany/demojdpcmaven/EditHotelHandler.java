package com.mycompany.demojdpcmaven;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditHotelHandler implements HttpHandler {

    private static final Pattern pattern = Pattern.compile("/edit-hotel/(\\d+)");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "PUT, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        Matcher matcher = pattern.matcher(exchange.getRequestURI().getPath());
        if (!matcher.find()) {
            sendResponse(exchange, 400, new JSONObject().put("error", "Thiếu ID khách sạn trong URL."));
            return;
        }

        int hotelId = Integer.parseInt(matcher.group(1));

        if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }

                JSONObject jsonRequest = new JSONObject(requestBody.toString());
                String hotelName = jsonRequest.getString("hotelName");
                String hotelAddress = jsonRequest.getString("hotelAddress");
                String description = jsonRequest.getString("description");
                String img = jsonRequest.getString("img");
                boolean success = DatabaseUtil.updateHotel(hotelId, hotelName, hotelAddress, description, img);

                JSONObject responseJson = new JSONObject()
                        .put("message", success ? "Cập nhật khách sạn thành công!" : "Không thể cập nhật khách sạn.");

                sendResponse(exchange, 200, responseJson);

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 400, new JSONObject().put("error", "Lỗi xử lý dữ liệu."));
            }

        } else {
            sendResponse(exchange, 405, new JSONObject().put("error", "Chỉ hỗ trợ phương thức PUT."));
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, JSONObject jsonObject) throws IOException {
        byte[] responseBytes = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}
