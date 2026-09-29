<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Murach's Java Servlets and JSP</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css" type="text/css"/>
</head>
<body>

<!-- Navigation Taskbar -->
<div class="taskbar">
    <div class="taskbar-brand">Murach Web Application</div>
    <div class="taskbar-menu">
        <a href="${pageContext.request.contextPath}/index.jsp" class="nav-item active">📊 SQL Gateway</a>
        <a href="${pageContext.request.contextPath}/email.jsp" class="nav-item">✉️ Send Email (JavaMail)</a>
    </div>
</div>

<div class="content-container">
    <%
        String sqlStmt = (String) request.getAttribute("sqlStatement");
        if (sqlStmt == null || sqlStmt.trim().isEmpty()) {
            sqlStmt = "select * from User";
        }
        String sqlRes = (String) request.getAttribute("sqlResult");
        if (sqlRes == null) {
            sqlRes = "";
        }
    %>

    <h1>The SQL Gateway</h1>
    <p>Enter an SQL statement and click the Execute button.</p>

    <form action="${pageContext.request.contextPath}/sqlGateway" method="post">
        <p><b>SQL statement:</b></p>
        <textarea name="sqlStatement" rows="8" cols="60"><%= sqlStmt %></textarea>
        <br><br>
        <input type="submit" value="Execute">
    </form>

    <p><b>SQL result:</b></p>
    <%= sqlRes %>
</div>

</body>
</html>