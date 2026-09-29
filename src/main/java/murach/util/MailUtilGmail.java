package murach.util;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class MailUtilGmail {

    // 1. Cấu hình Brevo SMTP chuẩn theo tài khoản của bạn (Cổng 587 - Dùng cho cả Local & Render)
    public static final String BREVO_HOST = "smtp-relay.brevo.com";
    public static final int BREVO_PORT = 587;
    
    // Login chuẩn từ Brevo dashboard của bạn
    public static final String DEFAULT_BREVO_LOGIN = "bba486001@smtp-brevo.com";
    public static final String DEFAULT_BREVO_KEY = "xsmtpsib-9f65b23f91b7f0d34a399d5390cf92db701339a156c92db0d5f14de20db896dd-hua8AzEX4AFivjuY";

    // 2. Cấu hình Gmail SMTPS (Cổng 465 SSL)
    public static final String GMAIL_HOST = "smtp.gmail.com";
    public static final int GMAIL_PORT = 465;
    public static final String DEFAULT_GMAIL_USER = "haile442006@gmail.com";
    public static final String DEFAULT_GMAIL_APP_PASS = "jtbm iblx lqho kmsg";

    public static final String DEFAULT_SENDER_NAME = "Murach SQL Gateway & Email";

    // Gửi mail qua Brevo SMTP (Port 587)
    public static void sendMailBrevo(String to, String from, String subject, String body, boolean bodyIsHTML)
            throws MessagingException, UnsupportedEncodingException {

        String login = System.getenv("BREVO_SMTP_USER");
        if (login == null || login.trim().isEmpty()) {
            login = DEFAULT_BREVO_LOGIN;
        }

        String key = System.getenv("BREVO_SMTP_KEY");
        if (key == null || key.trim().isEmpty()) {
            key = DEFAULT_BREVO_KEY;
        }

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", BREVO_HOST);
        props.put("mail.smtp.port", String.valueOf(BREVO_PORT));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");

        Session session = Session.getInstance(props);
        session.setDebug(true);

        String senderFrom = (from != null && !from.trim().isEmpty()) ? from : "haile442006@gmail.com";
        Message message = createMimeMessage(session, to, senderFrom, subject, body, bodyIsHTML);

        Transport transport = session.getTransport("smtp");
        try {
            // Đăng nhập bằng Login ID của Brevo (bba486001@smtp-brevo.com) và Master Key
            transport.connect(BREVO_HOST, BREVO_PORT, login, key);
            transport.sendMessage(message, message.getAllRecipients());
        } finally {
            transport.close();
        }
    }

    // Gửi mail qua Gmail SMTPS (Port 465)
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
        session.setDebug(true);

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
}
