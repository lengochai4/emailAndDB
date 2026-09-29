package murach.util;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class MailUtilGmail {

    // Thông tin tài khoản gửi Gmail mặc định
    private static final String DEFAULT_GMAIL = "haile442006@gmail.com";
    private static final String DEFAULT_APP_PASSWORD = "jtbm iblx lqho kmsg";
    private static final String DEFAULT_SENDER_NAME = "Murach SQL Gateway & Email";

    public static void sendMail(String to, String from, String subject, String body, boolean bodyIsHTML)
            throws MessagingException, UnsupportedEncodingException {

        // 1. Create mail session (Cấu hình kết nối SSL tới Gmail)
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", "smtp.gmail.com");
        props.put("mail.smtps.port", "465");
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.quitwait", "false");
        props.put("mail.smtps.ssl.enable", "true");
        props.put("mail.smtps.ssl.protocols", "TLSv1.2 TLSv1.3");

        Session session = Session.getInstance(props);
        session.setDebug(true);

        // 2. Create message
        Message message = new MimeMessage(session);
        message.setSubject(subject);
        if (bodyIsHTML) {
            message.setContent(body, "text/html; charset=UTF-8");
        } else {
            message.setText(body);
        }

        // 3. Set sender and receiver
        String senderEmail = (from != null && !from.trim().isEmpty()) ? from : DEFAULT_GMAIL;
        Address fromAddress = new InternetAddress(senderEmail, DEFAULT_SENDER_NAME);
        Address toAddress = new InternetAddress(to);
        message.setFrom(fromAddress);
        message.setRecipient(Message.RecipientType.TO, toAddress);

        // 4. Login and send
        Transport transport = session.getTransport();
        try {
            transport.connect(DEFAULT_GMAIL, DEFAULT_APP_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
        } finally {
            transport.close();
        }
    }
}
