package com.mycompany.demojdpcmaven;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.util.Date;

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

            // Lấy các query parameters từ URL
            Map<String, String> queryParams = getQueryParams(requestURI);

            // Lấy các tham số lọc từ query
            String checkInParam = queryParams.get("checkIn");
            String checkOutParam = queryParams.get("checkOut");
            String capacityParam = queryParams.get("capacity");
            String typeParam = queryParams.get("type");

            // Giả sử DatabaseUtil.getRoomsByHotelID trả về dữ liệu dạng JSONArray hoặc List.
            JSONArray roomsJson = DatabaseUtil.getRoomsByHotelID(hotelID);
            // Nếu không có phòng, trả về thông báo lỗi.
            if (roomsJson == null || roomsJson.length() == 0) {
                response = "{\"error\": \"Không tìm thấy phòng cho khách sạn này.\"}";
            } else {
                // Tạo đối tượng JSON kết quả từ dữ liệu phòng
                JSONArray resultRooms = new JSONArray();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                for (int i = 0; i < roomsJson.length(); i++) {
                    JSONObject room = roomsJson.getJSONObject(i);

                    // Lọc phòng theo các tham số checkIn, checkOut, capacity, type
                    boolean matches = true;

                    // Kiểm tra capacity
                    if (capacityParam != null) {
                        try {
                            int capacity = Integer.parseInt(capacityParam);
                            int roomCapacity = room.getInt("capacity");
                            if (capacity != roomCapacity) {
                                matches = false;
                            }
                        } catch (NumberFormatException e) {
                            // Nếu capacityParam không phải số nguyên hợp lệ, không lọc theo capacity
                            matches = false;
                        }
                    }

                    // Kiểm tra type
                    if (typeParam != null && !room.getString("roomType").equals(typeParam)) {
                        matches = false;
                    }

                    // Nếu phòng thỏa mãn tất cả điều kiện, thêm vào kết quả
                    if (matches) {
                        resultRooms.put(room);
                    }
                }

                // Trả về dữ liệu phòng đã xử lý
                response = resultRooms.length() > 0 ? resultRooms.toString() : "{\"error\": \"Không tìm thấy phòng phù hợp với các tiêu chí.\"}";
            }
        } else {
            response = "{\"error\": \"Invalid URL format. Use /hotel/{id}\"}";
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes("UTF-8"));
        }
    }

    // Hàm để lấy các query parameters từ URL
    private Map<String, String> getQueryParams(URI uri) {
        String query = uri.getQuery();
        Map<String, String> queryParams = new java.util.HashMap<>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    queryParams.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return queryParams;
    }
}
