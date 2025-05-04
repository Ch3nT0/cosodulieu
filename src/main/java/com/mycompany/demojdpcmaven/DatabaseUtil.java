package com.mycompany.demojdpcmaven;

import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class DatabaseUtil {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/hotel_booking";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "chencode";

    public static JSONArray getRoomsByHotelID(int hotelID) {
        JSONArray jsonArray = new JSONArray();
        String sql = "SELECT * FROM rooms WHERE hotelID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, hotelID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("roomID", rs.getInt("id"));
                obj.put("hotelID", rs.getInt("hotelID"));
                obj.put("roomName", rs.getString("roomNumber"));
                obj.put("price", rs.getDouble("pricePerNight"));
                obj.put("roomType", rs.getString("roomType"));
                obj.put("capacity", rs.getInt("capacity"));
                obj.put("status", rs.getString("status"));
                jsonArray.put(obj);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi DB: " + e.getMessage());
        }
        return jsonArray;
    }

    public static JSONObject getRoomID(int roomID) {
        JSONObject obj = new JSONObject();
        String sql = "SELECT * FROM rooms WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                obj.put("roomID", rs.getInt("id"));
                obj.put("hotelID", rs.getInt("hotelID"));
                obj.put("roomName", rs.getString("roomNumber"));
                obj.put("price", rs.getDouble("pricePerNight"));
                obj.put("roomType", rs.getString("roomType"));
                obj.put("capacity", rs.getInt("capacity"));
                obj.put("status", rs.getString("status"));
            } else {
                return null; // Không tìm thấy phòng với roomID
            }
        } catch (SQLException e) {
            System.out.println("Lỗi DB: " + e.getMessage());
            return null;
        }
        return obj;
    }

    public static boolean checkLogin(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM accounts WHERE username=? AND password=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Lỗi login: " + e.getMessage());
            return false;
        }
    }

    public static JSONObject getUserInfo(int accountID) {
        JSONObject userJson = new JSONObject();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "SELECT  fullname, email FROM users WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                userJson.put("fullname", rs.getString("fullname"));
                userJson.put("email", rs.getString("email"));
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userJson;
    }

    public static String getRole(String username) {
        String role = null;
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "SELECT role FROM accounts WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                role = rs.getString("role");
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return role;
    }

    public static int getUserID(int accountID) {
        int userID = -1;
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "SELECT id FROM users WHERE accountID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                userID = rs.getInt("id");
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userID;
    }

    public static int getAccountID(String username) {
        int accountID = -1;
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "SELECT id FROM accounts WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                accountID = rs.getInt("id");
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountID;
    }

    public static JSONArray getAccountsJson() {
        JSONArray jsonArray = new JSONArray();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM accounts")) {

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("account_id", rs.getInt("id"));
                obj.put("username", rs.getString("username"));
                obj.put("password", rs.getString("password"));
                obj.put("role", rs.getString("role"));
                jsonArray.put(obj);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi DB: " + e.getMessage());
        }
        return jsonArray;
    }

    public static JSONArray getHotelJson(String location) {
        JSONArray jsonArray = new JSONArray();
        String sql = "SELECT * FROM hotels WHERE hotelAddress LIKE ?"; // Lọc theo địa chỉ khách sạn

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + location + "%"); // Sử dụng LIKE để tìm kiếm theo địa chỉ

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", rs.getInt("id"));
                obj.put("hotelName", rs.getString("hotelName"));
                obj.put("hotelAddress", rs.getString("hotelAddress"));
                obj.put("description", rs.getString("description"));
                obj.put("img", rs.getString("img"));
                jsonArray.put(obj);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi DB: " + e.getMessage());
        }
        return jsonArray;
    }

    public static boolean registerAccountAndUser(String username, String password, String role,
            String fullName, String email, String phone) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // Kiểm tra tài khoản đã tồn tại
            String checkSql = "SELECT id FROM accounts WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                conn.rollback();
                return false; // Tài khoản đã tồn tại
            }

            // Thêm vào bảng accounts
            String insertAccountSql = "INSERT INTO accounts (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement accountStmt = conn.prepareStatement(insertAccountSql, Statement.RETURN_GENERATED_KEYS);
            accountStmt.setString(1, username);
            accountStmt.setString(2, password);
            accountStmt.setString(3, role);
            accountStmt.executeUpdate();

            // Lấy account_id vừa thêm
            ResultSet generatedKeys = accountStmt.getGeneratedKeys();
            int accountId = -1;
            if (generatedKeys.next()) {
                accountId = generatedKeys.getInt(1);
            } else {
                conn.rollback();
                return false;
            }

            // Thêm vào bảng user
            String insertUserSql = "INSERT INTO users (accountID, fullName, email, phone) VALUES (?, ?, ?, ?)";
            PreparedStatement userStmt = conn.prepareStatement(insertUserSql);
            userStmt.setInt(1, accountId);
            userStmt.setString(2, fullName);
            userStmt.setString(3, email);
            userStmt.setString(4, phone);
            userStmt.executeUpdate();

            // Commit giao dịch
            conn.commit();
            accountStmt.close();
            userStmt.close();
            checkStmt.close();
            rs.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean insertRoom(String roomNumber, String pricePerNight, String capacity, String roomType, String status, String hotelID) {
        try {
            // Kết nối với cơ sở dữ liệu
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Chuẩn bị câu lệnh SQL
            String query = "INSERT INTO rooms (roomNumber, pricePerNight, capacity, roomType, status, hotelID) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, roomNumber);
            stmt.setString(2, pricePerNight);
            stmt.setString(3, capacity);
            stmt.setString(4, roomType);
            stmt.setString(5, status);
            stmt.setString(6, hotelID);

            // Thực thi câu lệnh
            int rowsAffected = stmt.executeUpdate();

            // Đóng kết nối
            stmt.close();
            connection.close();

            return rowsAffected > 0; // Nếu có phòng được thêm vào, trả về true
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Trả về false nếu có lỗi
        }
    }

    public static boolean insertHotel(String name, String address, String description, String imgLink) {
        String sql = "INSERT INTO Hotels (hotelName, hotelAddress, description, img) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, description);
            stmt.setString(4, imgLink);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                conn.rollback(); // Nếu không chèn được thì rollback
                return false;
            }

            conn.commit(); // Commit giao dịch nếu thành công
            stmt.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    static boolean saveBooking(int userID, int roomID, String checkInDate, String checkOutDate) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // Thêm vào bảng bookings
            String insertBookingSql = "INSERT INTO bookings (userID, checkInDate, checkOutDate, roomID) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertBookingSql);

            pstmt.setInt(1, userID);         // ID người dùng
            pstmt.setString(2, checkInDate); // Ngày check-in
            pstmt.setString(3, checkOutDate); // Ngày check-out
            pstmt.setInt(4, roomID);         // ID phòng

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                conn.rollback(); // Nếu không có dòng nào bị ảnh hưởng thì rollback
                return false;
            }

            // Commit giao dịch
            conn.commit();
            pstmt.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
