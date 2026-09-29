package murach.data;

import java.sql.*;

public class SQLUtil {

    public static String getHtmlTable(ResultSet results) throws SQLException {
        StringBuilder htmlRows = new StringBuilder();
        ResultSetMetaData metaData = results.getMetaData();
        int columnCount = metaData.getColumnCount();

        htmlRows.append("<table border='1' cellpadding='5'>");

        htmlRows.append("<tr>");
        for (int i = 1; i <= columnCount; i++) {
            htmlRows.append("<th>").append(metaData.getColumnName(i)).append("</th>");
        }
        htmlRows.append("</tr>");

        // Tạo các dòng dữ liệu (Table Body)
        while (results.next()) {
            htmlRows.append("<tr>");
            for (int i = 1; i <= columnCount; i++) {
                htmlRows.append("<td>").append(results.getString(i)).append("</td>");
            }
            htmlRows.append("</tr>");
        }

        htmlRows.append("</table>");
        return htmlRows.toString();
    }
}