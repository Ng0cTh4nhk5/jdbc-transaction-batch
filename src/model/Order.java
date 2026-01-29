package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order - Model đại diện cho đơn hàng
 */
public class Order {
    private int id;
    private LocalDateTime createdAt;
    private List<OrderItem> items;
    
    // Constructor rỗng
    public Order() {
        this.items = new ArrayList<>();
    }
    
    // Constructor với id
    public Order(int id) {
        this.id = id;
        this.items = new ArrayList<>();
    }
    
    // Constructor đầy đủ
    public Order(int id, LocalDateTime createdAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.items = new ArrayList<>();
    }
    
    // Thêm item vào order
    public void addItem(OrderItem item) {
        this.items.add(item);
    }
    
    // Thêm item vào order (overload)
    public void addItem(int productId, int qty) {
        this.items.add(new OrderItem(this.id, productId, qty));
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<OrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    
    // Tính tổng số lượng items
    public int getTotalItems() {
        return items.size();
    }
    
    // Tính tổng số lượng sản phẩm
    public int getTotalQuantity() {
        return items.stream().mapToInt(OrderItem::getQty).sum();
    }
    
    @Override
    public String toString() {
        return String.format("Order[id=%d, createdAt=%s, totalItems=%d, totalQty=%d]", 
            id, createdAt, getTotalItems(), getTotalQuantity());
    }
}
