package murach.sql;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import murach.data.ConnectionPool;
import murach.data.SQLUtil;

import java.io.IOException;
import java.sql.*;

public class SqlGatewayServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String sqlStatement = request.getParameter("sqlStatement");
        String sqlResult = "";

        if (sqlStatement != null && !sqlStatement.trim().isEmpty()) {
            ConnectionPool pool = ConnectionPool.getInstance();
            Connection connection = pool.getConnection();

            if (connection == null) {
                sqlResult = "<p style='color:red;'><b>Lỗi:</b> Không thể kết nối tới Cơ sở dữ liệu PostgreSQL! Hãy kiểm tra lại file context.xml (Username/Password/Port/Tên DB).</p>";
            } else {
                PreparedStatement ps = null;
                ResultSet resultSet = null;

                try {
                    ps = connection.prepareStatement(sqlStatement);
                    String lowerSql = sqlStatement.trim().toLowerCase();

                    if (lowerSql.startsWith("select")) {
                        resultSet = ps.executeQuery();
                        sqlResult = SQLUtil.getHtmlTable(resultSet);
                    } else {
                        int i = ps.executeUpdate();
                        if (i == 0) {
                            sqlResult = "The statement executed successfully. No rows affected.";
                        } else {
                            sqlResult = "The statement executed successfully. " + i + " row(s) affected.";
                        }
                    }
                } catch (SQLException e) {
                    sqlResult = "<p style='color:red;'><b>Error executing the SQL statement:</b></p>"
                            + "<p style='color:red;'>" + e.getMessage() + "</p>";
                } finally {
                    if (resultSet != null) {
                        try { resultSet.close(); } catch (SQLException ignored) {}
                    }
                    if (ps != null) {
                        try { ps.close(); } catch (SQLException ignored) {}
                    }
                    pool.freeConnection(connection);
                }
            }
        }

        request.setAttribute("sqlResult", sqlResult);
        request.setAttribute("sqlStatement", sqlStatement);

        getServletContext()
                .getRequestDispatcher("/index.jsp")
                .forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}