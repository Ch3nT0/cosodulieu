package com.mycompany.demojdpcmaven;

import java.io.IOException;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;

public class HotelHandle implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Thêm header CORS cho phép truy cập từ bất kỳ domain nào
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        // Lấy query string từ URI yêu cầu (ví dụ: /hotel?location=Hanoi)
        String location = exchange.getRequestURI().getQuery(); // Lấy query string từ URI

        // Kiểm tra nếu có tham số "location" trong query, nếu không thì mặc định là ""
        if (location != null && location.contains("location=")) {
            location = location.split("=")[1]; // Lấy giá trị location từ query
        } else {
            location = ""; // Nếu không có location thì mặc định lấy tất cả
        }

        // Lấy dữ liệu khách sạn từ cơ sở dữ liệu theo location
        JSONArray jsonArray = DatabaseUtil.getHotelJson(location);

        // Chuyển dữ liệu thành JSON và trả về cho client
        String response = jsonArray.toString();
        
        // Gửi dữ liệu trả về với mã 200 (thành công)
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
