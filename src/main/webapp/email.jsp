<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Murach's Java Servlets - Send Email</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css" type="text/css"/>
</head>
<body>

<!-- Navigation Taskbar -->
<div class="taskbar">
    <div class="taskbar-brand">Murach Web Application</div>
    <div class="taskbar-menu">
        <a href="${pageContext.request.contextPath}/index.jsp" class="nav-item">📊 SQL Gateway</a>
        <a href="${pageContext.request.contextPath}/email.jsp" class="nav-item active">✉️ Send Email (JavaMail)</a>
    </div>
</div>

<div class="content-container">
    <h1>Send an Email</h1>


    <%
        String msg = (String) request.getAttribute("message");
        String msgType = (String) request.getAttribute("messageType");
        if (msg != null && !msg.trim().isEmpty()) {
            String alertClass = "success".equalsIgnoreCase(msgType) ? "alert-success" : "alert-danger";
    %>
        <div class="alert <%= alertClass %>">
            <%= msg %>
        </div>
    <%
        }
    %>

    <%
        String toVal = (String) request.getAttribute("to");
        if (toVal == null) toVal = "";

        String fromVal = (String) request.getAttribute("from");
        if (fromVal == null || fromVal.trim().isEmpty()) {
            fromVal = "haile442006@gmail.com";
        }

        String subjectVal = (String) request.getAttribute("subject");
        if (subjectVal == null || subjectVal.trim().isEmpty()) {
            subjectVal = "Chào mừng bạn đến với Murach SQL Gateway!";
        }

        String bodyVal = (String) request.getAttribute("body");
        if (bodyVal == null || bodyVal.trim().isEmpty()) {
            bodyVal = "Xin chào,\n\nĐây là email được gửi tự động bằng JavaMail API từ ứng dụng Murach Web Application.\n\nTrân trọng,\nMike Murach & Associates";
        }

        String formatVal = (String) request.getAttribute("format");
        if (formatVal == null) formatVal = "text";

        String serverTypeVal = (String) request.getAttribute("serverType");
        if (serverTypeVal == null) serverTypeVal = "gmail";
    %>

    <form action="${pageContext.request.contextPath}/email" method="post" class="email-form">
        <input type="hidden" name="action" value="send">

        <div class="form-group">
            <label for="serverType"><b>Cổng & Máy chủ gửi mail:</b></label>
            <select name="serverType" id="serverType">
                <option value="brevo" <%= "brevo".equals(serverTypeVal) ? "selected" : "" %>>Brevo SMTP Relay (Port 587)</option>
                <option value="gmail" <%= "gmail".equals(serverTypeVal) ? "selected" : "" %>>Google Gmail SMTPS (Port 465)</option>
            </select>
        </div>

        <div class="form-group">
            <label for="from"><b>From (Email người gửi):</b></label>
            <input type="email" id="from" name="from" value="<%= fromVal %>" required style="width: 450px;">
        </div>

        <div class="form-group">
            <label for="to"><b>To (Email người nhận):</b></label>
            <input type="email" id="to" name="to" value="<%= toVal %>" placeholder="Nhập địa chỉ email người nhận..." required style="width: 450px;">
        </div>

        <div class="form-group">
            <label for="subject"><b>Tiêu đề (Subject):</b></label>
            <input type="text" id="subject" name="subject" value="<%= subjectVal %>" required style="width: 450px;">
        </div>

        <div class="form-group">
            <label for="format"><b>Định dạng nội dung:</b></label>
            <input type="radio" id="formatText" name="format" value="text" <%= "text".equals(formatVal) ? "checked" : "" %>>
            <label for="formatText" style="display:inline; font-weight:normal;">Văn bản thuần (Plain Text)</label>
            &nbsp;&nbsp;
            <input type="radio" id="formatHtml" name="format" value="html" <%= "html".equals(formatVal) ? "checked" : "" %>>
            <label for="formatHtml" style="display:inline; font-weight:normal;">Nội dung HTML</label>
        </div>

        <div class="form-group">
            <label for="body"><b>Nội dung thư (Body):</b></label>
            <textarea id="body" name="body" rows="8" cols="60" required><%= bodyVal %></textarea>
        </div>

        <div class="form-group">
            <input type="submit" value="Send Email ✉️" class="btn-primary">
            <input type="reset" value="Làm mới form" class="btn-secondary">
        </div>
    </form>
</div>

</body>
</html>
