package com.mycompany.demojdpcmaven;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HotelIDHandler implements HttpHandler {
    private static final Pattern pattern = Pattern.compile("/hotel/(\\d+)");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        
        
        // Thêm header CORS cho phép truy cập từ bất kỳ domain nào
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        Matcher matcher = pattern.matcher(path);
        String response;

        if (matcher.matches()) {
            int hotelID = Integer.parseInt(matcher.group(1));
            JSONArray roomsJson = DatabaseUtil.getRoomsByHotelID(hotelID);
            response = roomsJson.toString();
        } else {
            response = "{\"error\": \"Invalid URL format. Use /hotel/{id}\"}";
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes("UTF-8"));
        }
    }
}
