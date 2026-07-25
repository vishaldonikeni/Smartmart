-- =========================================================
-- SmartMart Sales & Inventory Management System
-- Database Schema
-- =========================================================

DROP DATABASE IF EXISTS smartmart_db;
CREATE DATABASE smartmart_db;
USE smartmart_db;

-- ---------------------------------------------------------
-- Table: Customers
-- ---------------------------------------------------------
CREATE TABLE Customers (
    customer_id   INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    phone         VARCHAR(20),
    email         VARCHAR(100),
    city          VARCHAR(50)
);

-- ---------------------------------------------------------
-- Table: Products
-- ---------------------------------------------------------
CREATE TABLE Products (
    product_id    INT AUTO_INCREMENT PRIMARY KEY,
    product_name  VARCHAR(100) NOT NULL,
    category      VARCHAR(50),
    price         DECIMAL(10,2) NOT NULL DEFAULT 0,
    stock         INT NOT NULL DEFAULT 0
);

-- ---------------------------------------------------------
-- Table: Orders
-- ---------------------------------------------------------
CREATE TABLE Orders (
    order_id     INT AUTO_INCREMENT PRIMARY KEY,
    customer_id  INT NOT NULL,
    order_date   DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Table: Order_Items
-- ---------------------------------------------------------
CREATE TABLE Order_Items (
    item_id     INT AUTO_INCREMENT PRIMARY KEY,
    order_id    INT NOT NULL,
    product_id  INT NOT NULL,
    quantity    INT NOT NULL,
    price       DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

-- =========================================================
-- Sample Data
-- =========================================================

INSERT INTO Customers (customer_name, phone, email, city) VALUES
('Ravi Kumar', '9876543210', 'ravi.kumar@example.com', 'Hyderabad'),
('Ananya Sharma', '9123456780', 'ananya.sharma@example.com', 'Bengaluru'),
('Mohammed Ali', '9988776655', 'mohammed.ali@example.com', 'Hyderabad'),
('Priya Reddy', '9012345678', 'priya.reddy@example.com', 'Chennai'),
('Sanjay Gupta', '9765432109', 'sanjay.gupta@example.com', 'Mumbai');

INSERT INTO Products (product_name, category, price, stock) VALUES
('Wireless Mouse', 'Electronics', 499.00, 50),
('Mechanical Keyboard', 'Electronics', 2499.00, 30),
('Notebook A5', 'Stationery', 60.00, 200),
('Ball Pen (Pack of 10)', 'Stationery', 90.00, 150),
('Office Chair', 'Furniture', 5999.00, 15),
('LED Desk Lamp', 'Electronics', 799.00, 40),
('Water Bottle 1L', 'Home & Kitchen', 249.00, 100),
('Backpack', 'Accessories', 1499.00, 25);

-- Sample orders (order_date defaults to insert time)
INSERT INTO Orders (customer_id) VALUES (1);
INSERT INTO Orders (customer_id) VALUES (2);
INSERT INTO Orders (customer_id) VALUES (1);

INSERT INTO Order_Items (order_id, product_id, quantity, price) VALUES
(1, 1, 2, 499.00),
(1, 3, 5, 60.00),
(2, 5, 1, 5999.00),
(2, 6, 2, 799.00),
(3, 4, 3, 90.00);

-- Reflect stock reduction for sample orders
UPDATE Products SET stock = stock - 2 WHERE product_id = 1;
UPDATE Products SET stock = stock - 5 WHERE product_id = 3;
UPDATE Products SET stock = stock - 1 WHERE product_id = 5;
UPDATE Products SET stock = stock - 2 WHERE product_id = 6;
UPDATE Products SET stock = stock - 3 WHERE product_id = 4;

-- =========================================================
-- After creating  all we use these queries
-- =========================================================

USE smartmart_db;
SELECT * FROM Customers;
SELECT * FROM Products;
SELECT * FROM Orders;
SELECT * FROM Order_Items;
