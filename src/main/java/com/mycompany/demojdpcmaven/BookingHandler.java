package com.mycompany.demojdpcmaven;

import java.io.IOException;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

public class BookingHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Cấu hình CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        // Xử lý yêu cầu OPTIONS cho CORS
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        // Đọc dữ liệu từ request body
        String requestBody = new String(exchange.getRequestBody().readAllBytes());
        JSONObject requestJson = new JSONObject(requestBody);

        int userID = requestJson.getInt("userID");
        int roomID = requestJson.getInt("roomID");
        String checkIn = requestJson.getString("checkIn");
        String checkOut = requestJson.getString("checkOut");
        System.out.println("Hi");
        // Lưu thông tin booking vào cơ sở dữ liệu
        boolean success = DatabaseUtil.saveBooking(userID, roomID, checkIn, checkOut);
//        boolean success =true;
        System.out.println(success);
        // Tạo response JSON
        JSONObject responseJson = new JSONObject();

        if (success) {
            responseJson.put("success", true);
            responseJson.put("message", "Đặt phòng thành công");
            exchange.sendResponseHeaders(200, responseJson.toString().getBytes().length);
        } else {
            responseJson.put("success", false);
            responseJson.put("message", "Đặt phòng thất bại");
            exchange.sendResponseHeaders(400, responseJson.toString().getBytes().length);
        }

        // Gửi phản hồi cho frontend
        OutputStream os = exchange.getResponseBody();
        os.write(responseJson.toString().getBytes());
        os.close();
    }
}
