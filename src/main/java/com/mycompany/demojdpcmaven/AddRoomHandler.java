package com.mycompany.demojdpcmaven;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class AddRoomHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Bắt buộc phải khai báo các header CORS ở mọi request
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        // Trả về 200 OK cho request OPTIONS (Preflight)
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1); // Không có body
            return;
        }

        // Chỉ cho phép POST
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();
            isr.close();

            try {
                JSONObject jsonRequest = new JSONObject(sb.toString());

                // Lấy dữ liệu từ request JSON
                String roomNumber = jsonRequest.getString("roomNumber");
                String pricePerNight = jsonRequest.getString("pricePerNight");
                String capacity = jsonRequest.getString("capacity");
                String roomType = jsonRequest.getString("roomType");
                String status = jsonRequest.getString("status");
                String hotelID = jsonRequest.getString("hotelID");

                // Gọi method thêm phòng vào cơ sở dữ liệu
                boolean success = DatabaseUtil.insertRoom(roomNumber, pricePerNight, capacity, roomType, status, hotelID);

                // Tạo phản hồi
                String responseJson = new JSONObject()
                        .put("message", success ? "Thêm phòng thành công!" : "Không thể thêm phòng.")
                        .toString();

                byte[] responseBytes = responseJson.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(responseBytes);
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
                String errorResponse = new JSONObject().put("error", "Lỗi xử lý dữ liệu.").toString();
                byte[] responseBytes = errorResponse.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(400, responseBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(responseBytes);
                os.close();
            }

        } else {
            String error = new JSONObject().put("error", "Chỉ hỗ trợ phương thức POST.").toString();
            byte[] responseBytes = error.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(405, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }
}
