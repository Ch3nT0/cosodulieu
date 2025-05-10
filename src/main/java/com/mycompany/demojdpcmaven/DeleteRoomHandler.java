package com.mycompany.demojdpcmaven;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeleteRoomHandler implements HttpHandler {

    private static final Pattern pattern = Pattern.compile("/delete-room/(\\d+)");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        Matcher matcher = pattern.matcher(exchange.getRequestURI().getPath());
        if (!matcher.find()) {
            sendResponse(exchange, 400, new JSONObject().put("error", "Thiếu ID phòng trong URL."));
            return;
        }

        int roomId = Integer.parseInt(matcher.group(1));

        if ("DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                boolean success = DatabaseUtil.deleteRoom(roomId);
                JSONObject responseJson = new JSONObject()
                        .put("message", success ? "Xóa phòng thành công!" : "Không thể xóa phòng.");

                sendResponse(exchange, 200, responseJson);

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, new JSONObject().put("error", "Lỗi xử lý dữ liệu."));
            }

        } else {
            sendResponse(exchange, 405, new JSONObject().put("error", "Chỉ hỗ trợ phương thức DELETE."));
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, JSONObject jsonObject) throws IOException {
        byte[] responseBytes = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
