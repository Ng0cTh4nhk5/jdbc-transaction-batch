package model;

/**
 * OrderItem - Model đại diện cho chi tiết đơn hàng
 */
public class OrderItem {
    private int orderId;
    private int productId;
    private int qty;

    // Constructor rỗng
    public OrderItem() {
    }

    // Constructor đầy đủ
    public OrderItem(int orderId, int productId, int qty) {
        this.orderId = orderId;
        this.productId = productId;
        this.qty = qty;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    @Override
    public String toString() {
        return String.format("OrderItem[orderId=%d, productId=%d, qty=%d]",
                orderId, productId, qty);
    }
}
