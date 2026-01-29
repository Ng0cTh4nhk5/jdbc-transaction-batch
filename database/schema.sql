-- =====================================================
-- JDBC Transaction & Batch Lab: Order-Inventory System
-- =====================================================

-- Drop existing tables (nếu có)
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS products CASCADE;

-- =====================================================
-- Table: products
-- Mô tả: Lưu thông tin sản phẩm và số lượng tồn kho
-- =====================================================
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    stock INTEGER NOT NULL CHECK (stock >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- Table: orders
-- Mô tả: Lưu thông tin đơn hàng
-- =====================================================
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- Table: order_items
-- Mô tả: Chi tiết đơn hàng (mối quan hệ nhiều-nhiều)
-- =====================================================
CREATE TABLE order_items (
    order_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    qty INTEGER NOT NULL CHECK (qty > 0),
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- =====================================================
-- Index để tối ưu query
-- =====================================================
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

-- =====================================================
-- Sample Data: Thêm sản phẩm mẫu
-- =====================================================
INSERT INTO products (name, stock) VALUES 
    ('Laptop Dell XPS 15', 10),
    ('iPhone 15 Pro Max', 25),
    ('Samsung Galaxy S24', 15),
    ('AirPods Pro 2', 50),
    ('iPad Pro 12.9', 8),
    ('MacBook Air M3', 12),
    ('Sony WH-1000XM5', 30),
    ('Apple Watch Series 9', 20);

-- =====================================================
-- Verify data
-- =====================================================
SELECT 
    id,
    name,
    stock,
    created_at
FROM products
ORDER BY id;

-- =====================================================
-- Useful Queries
-- =====================================================

-- Xem tất cả sản phẩm và tồn kho
SELECT id, name, stock FROM products ORDER BY id;

-- Xem chi tiết đơn hàng
SELECT 
    o.id AS order_id,
    o.created_at,
    p.name AS product_name,
    oi.qty,
    (oi.qty * p.stock) AS subtotal
FROM orders o
JOIN order_items oi ON o.id = oi.order_id
JOIN products p ON oi.product_id = p.id
ORDER BY o.id, p.name;

-- Xem tổng số lượng đã bán theo sản phẩm
SELECT 
    p.id,
    p.name,
    p.stock AS current_stock,
    COALESCE(SUM(oi.qty), 0) AS total_sold
FROM products p
LEFT JOIN order_items oi ON p.id = oi.product_id
GROUP BY p.id, p.name, p.stock
ORDER BY p.id;

-- Xem lịch sử đơn hàng
SELECT 
    o.id,
    o.created_at,
    COUNT(oi.product_id) AS total_items,
    SUM(oi.qty) AS total_quantity
FROM orders o
LEFT JOIN order_items oi ON o.id = oi.order_id
GROUP BY o.id, o.created_at
ORDER BY o.created_at DESC;
