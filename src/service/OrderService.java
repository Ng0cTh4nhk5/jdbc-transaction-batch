package service;

import config.DatabaseConfig;
import model.Order;
import model.OrderItem;
import model.Product;

import java.sql.*;
import java.util.List;

/**
 * OrderService - Xử lý business logic cho đơn hàng
 * 
 * Chức năng chính:
 * - Tạo đơn hàng mới
 * - Kiểm tra tồn kho
 * - Trừ kho
 * - Insert order items bằng batch
 * - Quản lý transaction (commit/rollback)
 */
public class OrderService {

    private DatabaseConfig dbConfig;

    public OrderService() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    /**
     * Tạo đơn hàng mới với transaction và batch processing
     * 
     * Quy trình:
     * 1. Bắt đầu transaction (setAutoCommit(false))
     * 2. Tạo order mới
     * 3. Kiểm tra tồn kho cho từng item
     * 4. Trừ kho cho từng item
     * 5. Insert order_items bằng batch
     * 6. Commit nếu thành công, rollback nếu thất bại
     * 
     * @param order Order cần tạo (chứa danh sách items)
     * @return Order ID của đơn hàng vừa tạo
     * @throws Exception nếu có lỗi xảy ra
     */
    public int createOrder(Order order) throws Exception {
        Connection conn = null;
        PreparedStatement pstmtCreateOrder = null;
        PreparedStatement pstmtCheckStock = null;
        PreparedStatement pstmtUpdateStock = null;
        PreparedStatement pstmtInsertItems = null;
        ResultSet rs = null;

        int orderId = -1;

        try {
            // 1. Lấy connection và tắt auto-commit
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);
            System.out.println("═══════════════════════════════════════════════════");
            System.out.println("🔄 Bắt đầu Transaction");
            System.out.println("═══════════════════════════════════════════════════");

            // 2. Tạo order mới
            String sqlCreateOrder = "INSERT INTO orders (created_at) VALUES (CURRENT_TIMESTAMP) RETURNING id";
            pstmtCreateOrder = conn.prepareStatement(sqlCreateOrder);
            rs = pstmtCreateOrder.executeQuery();

            if (rs.next()) {
                orderId = rs.getInt("id");
                System.out.println("✓ Đã tạo Order ID: " + orderId);
            }

            // 3. Kiểm tra tồn kho cho TẤT CẢ items trước
            System.out.println("\n📦 Kiểm tra tồn kho:");
            String sqlCheckStock = "SELECT id, name, stock FROM products WHERE id = ?";
            pstmtCheckStock = conn.prepareStatement(sqlCheckStock);

            for (OrderItem item : order.getItems()) {
                pstmtCheckStock.setInt(1, item.getProductId());
                ResultSet rsStock = pstmtCheckStock.executeQuery();

                if (rsStock.next()) {
                    int currentStock = rsStock.getInt("stock");
                    String productName = rsStock.getString("name");

                    System.out.printf("  - Product ID %d (%s): Tồn kho = %d, Cần = %d%n",
                            item.getProductId(), productName, currentStock, item.getQty());

                    // Kiểm tra đủ hàng không
                    if (currentStock < item.getQty()) {
                        throw new Exception(
                                String.format("❌ KHÔNG ĐỦ HÀNG! Product '%s' (ID: %d) - Tồn kho: %d, Yêu cầu: %d",
                                        productName, item.getProductId(), currentStock, item.getQty()));
                    }
                } else {
                    throw new Exception("❌ Không tìm thấy Product ID: " + item.getProductId());
                }
                rsStock.close();
            }
            System.out.println("✓ Tất cả sản phẩm đều đủ hàng");

            // 4. Trừ kho cho từng item
            System.out.println("\n📉 Trừ kho:");
            String sqlUpdateStock = "UPDATE products SET stock = stock - ? WHERE id = ?";
            pstmtUpdateStock = conn.prepareStatement(sqlUpdateStock);

            for (OrderItem item : order.getItems()) {
                pstmtUpdateStock.setInt(1, item.getQty());
                pstmtUpdateStock.setInt(2, item.getProductId());
                int rowsAffected = pstmtUpdateStock.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.printf("  ✓ Product ID %d: Trừ %d sản phẩm%n",
                            item.getProductId(), item.getQty());
                }
            }

            // 5. Insert order_items bằng BATCH
            System.out.println("\n📝 Insert Order Items (Batch Processing):");
            String sqlInsertItems = "INSERT INTO order_items (order_id, product_id, qty) VALUES (?, ?, ?)";
            pstmtInsertItems = conn.prepareStatement(sqlInsertItems);

            for (OrderItem item : order.getItems()) {
                item.setOrderId(orderId); // Set order ID cho item

                pstmtInsertItems.setInt(1, item.getOrderId());
                pstmtInsertItems.setInt(2, item.getProductId());
                pstmtInsertItems.setInt(3, item.getQty());
                pstmtInsertItems.addBatch(); // Thêm vào batch

                System.out.printf("  + Batch: Order ID %d, Product ID %d, Qty %d%n",
                        item.getOrderId(), item.getProductId(), item.getQty());
            }

            // Execute batch
            int[] batchResults = pstmtInsertItems.executeBatch();
            System.out.printf("✓ Đã insert %d order items bằng batch%n", batchResults.length);

            // 6. Commit transaction
            conn.commit();
            System.out.println("\n✅ COMMIT TRANSACTION - Đơn hàng đã được tạo thành công!");
            System.out.println("═══════════════════════════════════════════════════");

            return orderId;

        } catch (Exception e) {
            // Rollback nếu có lỗi
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("\n🔙 ROLLBACK TRANSACTION - Đã hoàn tác tất cả thay đổi");
                    System.out.println("═══════════════════════════════════════════════════");
                } catch (SQLException rollbackEx) {
                    System.err.println("✗ Lỗi khi rollback: " + rollbackEx.getMessage());
                }
            }
            throw e; // Re-throw exception

        } finally {
            // Đóng resources
            try {
                if (rs != null)
                    rs.close();
                if (pstmtCreateOrder != null)
                    pstmtCreateOrder.close();
                if (pstmtCheckStock != null)
                    pstmtCheckStock.close();
                if (pstmtUpdateStock != null)
                    pstmtUpdateStock.close();
                if (pstmtInsertItems != null)
                    pstmtInsertItems.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Bật lại auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("✗ Lỗi khi đóng resources: " + e.getMessage());
            }
        }
    }

    /**
     * Lấy thông tin sản phẩm theo ID
     */
    public Product getProduct(int productId) throws SQLException {
        String sql = "SELECT id, name, stock FROM products WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("stock"));
            }
            return null;
        }
    }

    /**
     * Hiển thị tồn kho hiện tại
     */
    public void displayCurrentStock() throws SQLException {
        String sql = "SELECT id, name, stock FROM products ORDER BY id";

        System.out.println("\n📊 TỒN KHO HIỆN TẠI:");
        System.out.println("─────────────────────────────────────────────────");

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                System.out.printf("Product ID %d: %-25s | Stock: %3d%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("stock"));
            }
        }
        System.out.println("─────────────────────────────────────────────────");
    }
}
