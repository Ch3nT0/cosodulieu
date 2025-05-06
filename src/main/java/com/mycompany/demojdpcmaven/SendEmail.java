package com.mycompany.demojdpcmaven;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class SendEmail {

    public static void sendEmail(String recipientEmail, String subject, String body) {
        String host = "smtp.gmail.com";
        String port = "587";
        String username = "your-email@gmail.com";  // Địa chỉ email của bạn
        String password = "your-email-password";   // Mật khẩu ứng dụng Gmail (hoặc mật khẩu chính thức)

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Tạo phiên làm việc với thông tin tài khoản
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Soạn thư
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(subject);
            message.setText(body);

            // Gửi thư
            Transport.send(message);
            System.out.println("Email đã được gửi đến " + recipientEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
