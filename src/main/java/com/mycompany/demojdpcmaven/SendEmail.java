package com.mycompany.demojdpcmaven;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class SendEmail {


    private static final String USERNAME = "phamxuanhoanglong@gmail.com";
    private static final String PASSWORD = "pavhsaowkouenzou"; // App password 16 chữ, không phải mật khẩu thật

    public static boolean sendEmail(String recipientEmail, String subject, String body) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(body); // hoặc setContent(body, "text/html; charset=UTF-8");

            Transport.send(message);
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
