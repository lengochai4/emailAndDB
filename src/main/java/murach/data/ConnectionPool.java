package murach.data;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ConnectionPool {

    private static ConnectionPool pool = null;
    private static DataSource dataSource = null;

    private ConnectionPool() {
        try {
            InitialContext ic = new InitialContext();
            dataSource = (DataSource) ic.lookup("java:/comp/env/jdbc/murach");
        } catch (NamingException e) {
            System.err.println("Lỗi Lookup JNDI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static synchronized ConnectionPool getInstance() {
        if (pool == null) {
            pool = new ConnectionPool();
        }
        return pool;
    }

    public Connection getConnection() {
        try {
            if (dataSource == null) {
                System.err.println("DataSource bị null! Kiểm tra file context.xml");
                return null;
            }
            return dataSource.getConnection();
        } catch (SQLException e) {
            System.err.println("Lỗi lấy Connection: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void freeConnection(Connection c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đóng Connection: " + e.getMessage());
        }
    }
}