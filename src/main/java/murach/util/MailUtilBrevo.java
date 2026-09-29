package murach.util;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class MailUtilBrevo {

    public static void sendMail(String to, String from, String subject, String body, boolean bodyIsHTML)
            throws MessagingException, UnsupportedEncodingException {

        // Lấy thông tin từ Biến môi trường (hoặc giá trị mặc định)
        String smtpUser = System.getenv("BREVO_SMTP_USER");
        String smtpKey = System.getenv("BREVO_SMTP_KEY");

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp-relay.brevo.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Bắt buộc dùng STARTTLS trên cổng 587

        Session session = Session.getInstance(props);

        Message message = new MimeMessage(session);
        message.setSubject(subject);
        if (bodyIsHTML) {
            message.setContent(body, "text/html; charset=UTF-8");
        } else {
            message.setText(body);
        }

        // Email người gửi (Phải là email bạn đã xác thực trên Brevo)
        Address fromAddress = new InternetAddress(from != null ? from : smtpUser, "Murach Web App");
        Address toAddress = new InternetAddress(to);
        message.setFrom(fromAddress);
        message.setRecipient(Message.RecipientType.TO, toAddress);

        Transport transport = session.getTransport();
        try {
            transport.connect("smtp-relay.brevo.com", smtpUser, smtpKey);
            transport.sendMessage(message, message.getAllRecipients());
        } finally {
            transport.close();
        }
    }
}
