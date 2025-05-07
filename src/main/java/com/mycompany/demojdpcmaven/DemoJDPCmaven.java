package com.mycompany.demojdpcmaven;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class DemoJDPCmaven {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/accounts", new AccountsHandler());
        server.createContext("/hotel", new HotelHandle());
        server.createContext("/login", new LoginHandler());
        server.createContext("/hotel/", new HotelIDHandler());
        server.createContext("/room/", new RoomIDHandler());
        server.createContext("/booking/room/", new BookingRoomHandler());
        server.createContext("/booking", new BookingHandler());
        server.createContext("/add-hotel", new AddHotelHandler());
        server.createContext("/addRoom", new AddRoomHandler());
        server.createContext("/send-email", new SendEmailHandler());
        server.createContext("/api/momo", new MomoPaymentHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server run in: http://localhost:8080/accounts");
    }
}
