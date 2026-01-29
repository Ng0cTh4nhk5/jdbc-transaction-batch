package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConfig - Quản lý kết nối đến PostgreSQL Database
 * 
 * Singleton pattern để đảm bảo chỉ có một instance duy nhất
 */
public class DatabaseConfig {
    
    // Database connection parameters
    private static final String URL = "jdbc:postgresql://localhost:5432/order_system";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    
    // Singleton instance
    private static DatabaseConfig instance;
    
    // Private constructor để ngăn khởi tạo từ bên ngoài
    private DatabaseConfig() {
        try {
            // Load PostgreSQL JDBC Driver
            Class.forName("org.postgresql.Driver");
            System.out.println("✓ PostgreSQL JDBC Driver đã được load");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Không tìm thấy PostgreSQL JDBC Driver");
            e.printStackTrace();
        }
    }
    
    /**
     * Lấy instance của DatabaseConfig (Singleton)
     */
    public static DatabaseConfig getInstance() {
        if (instance == null) {
            synchronized (DatabaseConfig.class) {
                if (instance == null) {
                    instance = new DatabaseConfig();
                }
            }
        }
        return instance;
    }
    
    /**
     * Tạo connection mới đến database
     * 
     * @return Connection object
     * @throws SQLException nếu không thể kết nối
     */
    public Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("✓ Kết nối database thành công: " + URL);
        return conn;
    }
    
    /**
     * Đóng connection
     * 
     * @param conn Connection cần đóng
     */
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("✓ Đã đóng kết nối database");
            } catch (SQLException e) {
                System.err.println("✗ Lỗi khi đóng kết nối: " + e.getMessage());
            }
        }
    }
    
    /**
     * Kiểm tra kết nối database
     * 
     * @return true nếu kết nối thành công
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("✗ Kiểm tra kết nối thất bại: " + e.getMessage());
            return false;
        }
    }
    
    // Getters cho các thông tin cấu hình
    public String getUrl() {
        return URL;
    }
    
    public String getUser() {
        return USER;
    }
}
