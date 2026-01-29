import config.DatabaseConfig;
import model.Order;
import model.OrderItem;
import service.OrderService;

/**
 * Demo - Chương trình demo JDBC Transaction & Batch
 * 
 * Case 1: Tạo đơn hàng THÀNH CÔNG (đủ hàng trong kho)
 * Case 2: Tạo đơn hàng THẤT BẠI (không đủ hàng → rollback)
 */
public class Demo {

    public static void main(String[] args) {
        OrderService orderService = new OrderService();

        try {
            // Kiểm tra kết nối database
            System.out.println("🔌 Kiểm tra kết nối Database...");
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            if (dbConfig.testConnection()) {
                System.out.println("✓ Kết nối Database thành công!\n");
            } else {
                System.err.println("✗ Không thể kết nối đến Database");
                return;
            }

            // Hiển thị tồn kho ban đầu
            orderService.displayCurrentStock();

            // =====================================================
            // CASE 1: ĐẶT HÀNG THÀNH CÔNG
            // =====================================================
            System.out.println("\n\n");
            System.out.println("╔═══════════════════════════════════════════════════╗");
            System.out.println("║        CASE 1: ĐẶT HÀNG THÀNH CÔNG               ║");
            System.out.println("╚═══════════════════════════════════════════════════╝");

            testSuccessCase(orderService);

            // Hiển thị tồn kho sau case 1
            orderService.displayCurrentStock();

            // =====================================================
            // CASE 2: ĐẶT HÀNG THẤT BẠI (ROLLBACK)
            // =====================================================
            System.out.println("\n\n");
            System.out.println("╔═══════════════════════════════════════════════════╗");
            System.out.println("║        CASE 2: ĐẶT HÀNG THẤT BẠI                 ║");
            System.out.println("╚═══════════════════════════════════════════════════╝");

            testFailureCase(orderService);

            // Hiển thị tồn kho sau case 2 (phải giống như sau case 1)
            orderService.displayCurrentStock();

        } catch (Exception e) {
            System.err.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * CASE 1: Đặt hàng thành công
     * 
     * Đặt hàng với số lượng vừa phải, đủ hàng trong kho
     * → Transaction sẽ được commit
     */
    private static void testSuccessCase(OrderService orderService) {
        try {
            Order order = new Order();

            // Thêm items vào order (số lượng đủ)
            order.addItem(new OrderItem(0, 1, 2)); // Laptop Dell XPS 15: 2 chiếc (tồn kho: 10)
            order.addItem(new OrderItem(0, 2, 5)); // iPhone 15 Pro Max: 5 chiếc (tồn kho: 25)
            order.addItem(new OrderItem(0, 4, 10)); // AirPods Pro 2: 10 chiếc (tồn kho: 50)

            System.out.println("\n📋 Chi tiết đơn hàng:");
            System.out.println("  - Product ID 1 (Laptop Dell XPS 15): 2 chiếc");
            System.out.println("  - Product ID 2 (iPhone 15 Pro Max): 5 chiếc");
            System.out.println("  - Product ID 4 (AirPods Pro 2): 10 chiếc");
            System.out.println();

            // Tạo order
            int orderId = orderService.createOrder(order);

            System.out.println("\n🎉 KẾT QUẢ: Đơn hàng #" + orderId + " đã được tạo thành công!");

        } catch (Exception e) {
            System.err.println("\n❌ KẾT QUẢ: Đơn hàng thất bại - " + e.getMessage());
        }
    }

    /**
     * CASE 2: Đặt hàng thất bại (Rollback)
     * 
     * Đặt hàng với số lượng vượt quá tồn kho
     * → Transaction sẽ được rollback, không ảnh hưởng đến database
     */
    private static void testFailureCase(OrderService orderService) {
        try {
            Order order = new Order();

            // Thêm items vào order (có item không đủ hàng)
            order.addItem(new OrderItem(0, 3, 5)); // Samsung Galaxy S24: 5 chiếc (tồn kho: 15) ✓
            order.addItem(new OrderItem(0, 5, 20)); // iPad Pro 12.9: 20 chiếc (tồn kho: 8) ✗ KHÔNG ĐỦ
            order.addItem(new OrderItem(0, 7, 10)); // Sony WH-1000XM5: 10 chiếc (tồn kho: 30) ✓

            System.out.println("\n📋 Chi tiết đơn hàng:");
            System.out.println("  - Product ID 3 (Samsung Galaxy S24): 5 chiếc ✓");
            System.out.println("  - Product ID 5 (iPad Pro 12.9): 20 chiếc ✗ (Tồn kho chỉ có 8)");
            System.out.println("  - Product ID 7 (Sony WH-1000XM5): 10 chiếc ✓");
            System.out.println();

            // Tạo order (sẽ thất bại và rollback)
            int orderId = orderService.createOrder(order);

            System.out.println("\n🎉 KẾT QUẢ: Đơn hàng #" + orderId + " đã được tạo thành công!");

        } catch (Exception e) {
            System.err.println("\n❌ KẾT QUẢ: Đơn hàng thất bại - " + e.getMessage());
            System.out.println("\n💡 Giải thích:");
            System.out.println("  - Transaction đã được ROLLBACK");
            System.out.println("  - Tất cả thay đổi đã được hoàn tác");
            System.out.println("  - Tồn kho không bị ảnh hưởng");
        }
    }
}
