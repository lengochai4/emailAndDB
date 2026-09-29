package murach.email;

import java.io.IOException;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import murach.util.MailUtilGmail;
import murach.util.MailUtilLocal;

public class EmailServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "view";
        }

        String message = "";
        String messageType = "info";

        if ("send".equals(action)) {
            String to = request.getParameter("to");
            String from = request.getParameter("from");
            String subject = request.getParameter("subject");
            String body = request.getParameter("body");
            String format = request.getParameter("format");
            String serverType = request.getParameter("serverType");

            boolean isBodyHTML = "html".equalsIgnoreCase(format);

            if (to == null || to.trim().isEmpty()) {
                message = "Vui lòng nhập địa chỉ email người nhận (To)!";
                messageType = "error";
            } else {
                try {
                    if ("local".equalsIgnoreCase(serverType)) {
                        MailUtilLocal.sendMail(to, from, subject, body, isBodyHTML);
                    } else {
                        // Mặc định gửi qua Gmail với tài khoản được thiết lập sẵn
                        MailUtilGmail.sendMail(to, from, subject, body, isBodyHTML);
                    }
                    message = "Email đã được gửi thành công đến: " + to;
                    messageType = "success";
                } catch (Exception e) {
                    message = "Lỗi khi gửi email: " + e.getMessage();
                    messageType = "error";
                    this.log("Unable to send email: " + e.getMessage(), e);
                }
            }

            request.setAttribute("to", to);
            request.setAttribute("from", from);
            request.setAttribute("subject", subject);
            request.setAttribute("body", body);
            request.setAttribute("format", format);
            request.setAttribute("serverType", serverType);
        }

        request.setAttribute("message", message);
        request.setAttribute("messageType", messageType);

        getServletContext()
                .getRequestDispatcher("/email.jsp")
                .forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
