package murach.util;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class MailUtilGmail {

    // 1. Cấu hình thông tin Brevo SMTP
    public static final String SMTP_HOST = "smtp-relay.brevo.com";
    public static final int SMTP_PORT = 587;
    
    // Login Email của Brevo (Email bạn dùng đăng ký tài khoản Brevo)
    public static final String DEFAULT_GMAIL = "haile442006@gmail.com";
    
    // SMTP Master Key do Brevo cấp (bắt đầu bằng xsmtpsib-...)
    public static final String DEFAULT_APP_PASSWORD = "jtbm iblx lqho kmsg";
    
    public static final String DEFAULT_SENDER_NAME = "Murach SQL Gateway & Email";

    public static void sendMail(String to, String from, String subject, String body, boolean bodyIsHTML)
            throws MessagingException, UnsupportedEncodingException {

        // Cấu hình Properties chuẩn cho giao thức SMTP cổng 587 STARTTLS của Brevo
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");

        Session session = Session.getInstance(props);
        session.setDebug(true);

        // Tạo nội dung email
        Message message = new MimeMessage(session);
        message.setSubject(subject);
        if (bodyIsHTML) {
            message.setContent(body, "text/html; charset=UTF-8");
        } else {
            message.setText(body);
        }

        // Người gửi và người nhận
        String senderEmail = (from != null && !from.trim().isEmpty()) ? from : DEFAULT_GMAIL;
        Address fromAddress = new InternetAddress(senderEmail, DEFAULT_SENDER_NAME);
        Address toAddress = new InternetAddress(to);
        message.setFrom(fromAddress);
        message.setRecipient(Message.RecipientType.TO, toAddress);

        // Đăng nhập vào Brevo và gửi
        Transport transport = session.getTransport("smtp");
        try {
            transport.connect(SMTP_HOST, SMTP_PORT, DEFAULT_GMAIL, DEFAULT_APP_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
        } finally {
            transport.close();
        }
    }
}
