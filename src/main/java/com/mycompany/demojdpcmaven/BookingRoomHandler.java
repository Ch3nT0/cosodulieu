package com.mycompany.demojdpcmaven;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class BookingRoomHandler implements HttpHandler {

    private Connection getConnection() throws SQLException {
        // Cập nhật thông tin kết nối đúng với CSDL của bạn
        String url = "jdbc:mysql://localhost:3306/hotel_booking"; // Hoặc SQL Server
        String user = "root";
        String password = "chencode";
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        if ("GET".equalsIgnoreCase(method)) {
            String path = exchange.getRequestURI().getPath(); // /booking/room/101
            String[] parts = path.split("/");
            JSONObject responseJson = new JSONObject();

            if (parts.length >= 4) {
                String roomId = parts[3];

                try (Connection conn = getConnection()) {
                    String query = "SELECT checkInDate, checkOutDate FROM bookings WHERE roomID = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, roomId);
                    ResultSet rs = stmt.executeQuery();

                    JSONArray bookingsArray = new JSONArray();
                    while (rs.next()) {
                        JSONObject obj = new JSONObject();
                        obj.put("check_in", rs.getString("checkInDate"));
                        obj.put("check_out", rs.getString("checkOutDate"));
                        bookingsArray.put(obj);
                    }

                    responseJson.put("room_id", roomId);
                    responseJson.put("bookings", bookingsArray);
                    sendResponse(exchange, 200, responseJson.toString());

                } catch (SQLException e) {
                    responseJson.put("error", e.getMessage());
                    sendResponse(exchange, 500, responseJson.toString());
                }
            } else {
                responseJson.put("error", "Missing room ID in URL.");
                sendResponse(exchange, 400, responseJson.toString());
            }

        } else {
            sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
