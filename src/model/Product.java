package model;

import java.time.LocalDateTime;

/**
 * Product - Model đại diện cho sản phẩm trong hệ thống
 */
public class Product {
    private int id;
    private String name;
    private int stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructor rỗng
    public Product() {
    }
    
    // Constructor đầy đủ
    public Product(int id, String name, int stock) {
        this.id = id;
        this.name = name;
        this.stock = stock;
    }
    
    // Constructor không có id (dùng khi insert)
    public Product(String name, int stock) {
        this.name = name;
        this.stock = stock;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getStock() {
        return stock;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return String.format("Product[id=%d, name=%s, stock=%d]", id, name, stock);
    }
}
