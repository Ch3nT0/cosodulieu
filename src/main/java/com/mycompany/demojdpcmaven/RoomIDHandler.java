/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.demojdpcmaven;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Laptop K1
 */
public class RoomIDHandler implements HttpHandler {
    private static final Pattern pattern = Pattern.compile("/room/(\\d+)");

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
            int roomID = Integer.parseInt(matcher.group(1));
            JSONObject roomJson = DatabaseUtil.getRoomID(roomID);

            if (roomJson != null) {
                response = roomJson.toString();
            } else {
                response = "{\"error\": \"Room not found\"}";
            }
        } else {
            response = "{\"error\": \"Invalid URL format. Use /room/{id}\"}";
        }

        exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes("UTF-8"));
        }
    }
}
