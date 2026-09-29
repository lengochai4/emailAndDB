package murach.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class MailUtilGmail {

    // 1. Cấu hình Brevo SMTP & API
    public static final String BREVO_HOST = "smtp-relay.brevo.com";
    public static final int BREVO_PORT = 587;
    public static final String DEFAULT_BREVO_LOGIN = "bba486001@smtp-brevo.com";
    public static final String DEFAULT_BREVO_KEY = "xsmtpsib-9f65b23f91b7f0d34a399d5390cf92db701339a156c92db0d5f14de20db896dd-hua8AzEX4AFivjuY";

    // 2. Cấu hình Gmail SMTPS (Local)
    public static final String GMAIL_HOST = "smtp.gmail.com";
    public static final int GMAIL_PORT = 465;
    public static final String DEFAULT_GMAIL_USER = "haile442006@gmail.com";
    public static final String DEFAULT_GMAIL_APP_PASS = "jtbm iblx lqho kmsg";

    public static final String DEFAULT_SENDER_NAME = "Murach SQL Gateway & Email";

    /**
     * Gửi mail qua Brevo:
     * - Tự động sử dụng Brevo HTTPS API (Cổng 443) khi chạy trên Render (do Render Free chặn cổng SMTP 587/465).
     * - Tự động fallback linh hoạt nếu SMTP bị chặn.
     */
    public static void sendMailBrevo(String to, String from, String subject, String body, boolean bodyIsHTML)
            throws Exception {

        String login = System.getenv("BREVO_SMTP_USER");
        if (login == null || login.trim().isEmpty()) {
            login = DEFAULT_BREVO_LOGIN;
        }

        String key = System.getenv("BREVO_SMTP_KEY");
        if (key == null || key.trim().isEmpty()) {
            key = DEFAULT_BREVO_KEY;
        }

        String senderFrom = (from != null && !from.trim().isEmpty()) ? from : "haile442006@gmail.com";

        try {
            // Thử gửi qua Brevo HTTPS API trước (Cổng 443 - 100% không bao giờ bị Render chặn)
            sendViaBrevoHttpApi(to, senderFrom, subject, body, bodyIsHTML, key);
        } catch (Exception apiEx) {
            // Nếu API lỗi, fallback sang SMTP truyền thống
            System.err.println("Brevo HTTP API failed, trying SMTP 587: " + apiEx.getMessage());
            sendViaBrevoSmtp(to, senderFrom, subject, body, bodyIsHTML, login, key);
        }
    }

    // Gửi qua Brevo HTTPS REST API (Port 443 - Chuẩn cho Render Cloud)
    private static void sendViaBrevoHttpApi(String to, String from, String subject, String body, boolean bodyIsHTML, String apiKey)
            throws Exception {

        URL url = new URL("https://api.brevo.com/v3/smtp/email");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("api-key", apiKey);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("accept", "application/json");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setDoOutput(true);

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"sender\":{\"name\":\"").append(escapeJson(DEFAULT_SENDER_NAME)).append("\",\"email\":\"").append(escapeJson(from)).append("\"},");
        json.append("\"to\":[{\"email\":\"").append(escapeJson(to)).append("\"}],");
        json.append("\"subject\":\"").append(escapeJson(subject)).append("\",");
        if (bodyIsHTML) {
            json.append("\"htmlContent\":\"").append(escapeJson(body)).append("\"");
        } else {
            json.append("\"textContent\":\"").append(escapeJson(body)).append("\"");
        }
        json.append("}");

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int code = conn.getResponseCode();
        if (code < 200 || code >= 300) {
            StringBuilder response = new StringBuilder();
            if (conn.getErrorStream() != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line.trim());
                    }
                }
            }
            throw new Exception("Brevo API phản hồi lỗi (" + code + "): " + response.toString());
        }
    }

    // Gửi qua Brevo SMTP (Port 587)
    private static void sendViaBrevoSmtp(String to, String from, String subject, String body, boolean bodyIsHTML, String login, String key)
            throws MessagingException, UnsupportedEncodingException {

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", BREVO_HOST);
        props.put("mail.smtp.port", String.valueOf(BREVO_PORT));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");

        Session session = Session.getInstance(props);
        Message message = createMimeMessage(session, to, from, subject, body, bodyIsHTML);

        Transport transport = session.getTransport("smtp");
        try {
            transport.connect(BREVO_HOST, BREVO_PORT, login, key);
            transport.sendMessage(message, message.getAllRecipients());
        } finally {
            transport.close();
        }
    }

    // Gửi qua Gmail SMTPS (Port 465)
    public static void sendMailGmail(String to, String from, String subject, String body, boolean bodyIsHTML)
            throws MessagingException, UnsupportedEncodingException {

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", GMAIL_HOST);
        props.put("mail.smtps.port", String.valueOf(GMAIL_PORT));
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.quitwait", "false");
        props.put("mail.smtps.ssl.enable", "true");
        props.put("mail.smtps.ssl.protocols", "TLSv1.2 TLSv1.3");

        Session session = Session.getInstance(props);

        String senderEmail = (from != null && !from.trim().isEmpty()) ? from : DEFAULT_GMAIL_USER;
        Message message = createMimeMessage(session, to, senderEmail, subject, body, bodyIsHTML);

        Transport transport = session.getTransport("smtps");
        try {
            transport.connect(GMAIL_HOST, DEFAULT_GMAIL_USER, DEFAULT_GMAIL_APP_PASS.replace(" ", ""));
            transport.sendMessage(message, message.getAllRecipients());
        } finally {
            transport.close();
        }
    }

    private static Message createMimeMessage(Session session, String to, String from, String subject, String body, boolean bodyIsHTML)
            throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setSubject(subject);
        if (bodyIsHTML) {
            message.setContent(body, "text/html; charset=UTF-8");
        } else {
            message.setText(body);
        }
        Address fromAddress = new InternetAddress(from, DEFAULT_SENDER_NAME);
        Address toAddress = new InternetAddress(to);
        message.setFrom(fromAddress);
        message.setRecipient(Message.RecipientType.TO, toAddress);
        return message;
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
