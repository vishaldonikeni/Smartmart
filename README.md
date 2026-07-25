# SmartMart – Sales & Inventory Management System

A Java console application for managing customers, products, and orders for a retail
store, built with **JDBC** and **MySQL**. Developed as a mini-project to demonstrate
Java OOP, JDBC connectivity, SQL joins/aggregates, and modular software design.

---

## Table of Contents

- [Overview](#overview)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Setup Instructions](#setup-instructions)
- [Features](#features)
- [Sample Menu](#sample-menu)
- [Reports](#reports)
- [Screenshots](#screenshots)
- [Future Enhancements](#future-enhancements)

---

## Overview

SmartMart is a menu-driven console application that lets a store operator:

- Manage customer records
- Manage product inventory
- Create orders and generate invoices
- View sales reports and analytics

All data is persisted in a MySQL database and accessed through JDBC using
`PreparedStatement` for safe, parameterized SQL execution.

---

## Technologies Used

| Technology       | Purpose                              |
|-------------------|---------------------------------------|
| Java (17+)        | Core application logic (OOP)          |
| JDBC              | Database connectivity                 |
| MySQL             | Relational data storage               |
| MySQL Connector/J | JDBC driver for MySQL                 |
| Git & GitHub      | Version control and submission        |
| IntelliJ IDEA / VS Code | Development environment          |

---

## Project Structure

```
SmartMart/
├── src/
│   ├── model/
│   │   ├── Customer.java
│   │   ├── Product.java
│   │   ├── Order.java
│   │   └── OrderItem.java
│   ├── dao/
│   │   ├── CustomerDAO.java
│   │   ├── ProductDAO.java
│   │   └── OrderDAO.java
│   ├── db/
│   │   └── DBConnection.java
│   ├── service/
│   │   └── SalesService.java
│   ├── util/
│   │   └── Menu.java
│   └── Main.java
├── database.sql
├── README.md
├── .gitignore
└── screenshots/
```

**Layer responsibilities:**

- `model` – Plain Java objects (POJOs) mapping to database tables.
- `dao` – Data Access Objects; all SQL/JDBC code lives here (INSERT, UPDATE, DELETE,
  SELECT, JOINs, aggregates) using `PreparedStatement`.
- `db` – Centralized JDBC `Connection` management.
- `service` – Business logic layer (bill calculation, invoice printing) that
  coordinates DAOs so the UI never touches SQL directly.
- `util` – Console menu / user interaction layer.
- `Main.java` – Application entry point.

---

## Database Schema

```
Customers(customer_id PK, customer_name, phone, email, city)
Products(product_id PK, product_name, category, price, stock)
Orders(order_id PK, customer_id FK, order_date)
Order_Items(item_id PK, order_id FK, product_id FK, quantity, price)
```

**Relationships:**

- `Orders.customer_id` → `Customers.customer_id` (one customer has many orders)
- `Order_Items.order_id` → `Orders.order_id` (one order has many items)
- `Order_Items.product_id` → `Products.product_id`

Full DDL and sample seed data is provided in [`database.sql`](database.sql).

---

## Setup Instructions

### Prerequisites

- JDK 17 or later
- MySQL Server 8.x
- MySQL Connector/J (`mysql-connector-j-x.x.x.jar`)
- IntelliJ IDEA or VS Code with the Java Extension Pack

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/smartmart-sales-inventory.git
cd smartmart-sales-inventory
```

### 2. Create the database

Run the provided SQL script in MySQL Workbench or the MySQL CLI:

```bash
mysql -u root -p < database.sql
```

This creates the `smartmart_db` database, all four tables, and inserts sample
customers, products, and orders.

### 3. Configure the database connection

Open `src/db/DBConnection.java` and update the credentials to match your local
MySQL setup:

```java
private static final String URL = "jdbc:mysql://localhost:3306/smartmart_db?useSSL=false&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "your_password";
```

### 4. Add the MySQL JDBC driver

Download `mysql-connector-j` and add it to your project classpath.

- **IntelliJ IDEA:** File → Project Structure → Libraries → `+` → select the JAR.
- **VS Code:** Place the JAR in a `lib/` folder and add it to `.vscode/settings.json`
  under `java.project.referencedLibraries`.
- **Command line:**

  ```bash
  javac -d bin -cp lib/mysql-connector-j-x.x.x.jar $(find src -name "*.java")
  java -cp bin:lib/mysql-connector-j-x.x.x.jar Main
  ```

  (On Windows, use `;` instead of `:` in the classpath.)

### 5. Run the application

Run `Main.java` from your IDE, or use the command-line steps above.

---

## Features

### Customer Management
- Add, update, delete customers
- Search customers by name (partial match)
- View all customers

### Product Management
- Add, update, delete products
- Search products by name
- View all products
- Low stock alert (bonus feature)

### Order Management
- Create an order with multiple products
- Automatic stock validation and deduction (transaction-safe)
- Calculate total bill
- Print formatted invoice

### Reports
- Total Sales
- Best Selling Product
- Customer Purchase Report (`INNER JOIN` across Customers, Orders, Order_Items, Products)
- Customers Without Orders (`LEFT JOIN`)
- Top Customers (`GROUP BY`, `SUM`)
- Average Order Value (bonus)

All SQL access uses `PreparedStatement` to prevent SQL injection, and order
creation runs inside a JDBC transaction (`setAutoCommit(false)` + `commit`/`rollback`)
so partial orders are never persisted if stock is insufficient.

---

## Sample Menu

```
===== SmartMart Sales & Inventory Management System =====
1. Customer Management
2. Product Management
3. Order Management
4. Reports
5. Exit
```

---

## Reports

| Report                         | SQL Concept Used            |
|--------------------------------|------------------------------|
| Total Sales                    | `SUM()`                      |
| Best Selling Product           | `JOIN`, `SUM()`, `ORDER BY`, `LIMIT` |
| Customer Purchase Report       | `INNER JOIN` (3-table join)  |
| Customers Without Orders       | `LEFT JOIN`, `IS NULL`       |
| Top Customers                  | `GROUP BY`, `SUM()`, `COUNT()` |
| Average Order Value            | Subquery, `AVG()`            |

---

## Screenshots

> Add screenshots of the running application here before submission.

| Main Menu | Add Customer | Invoice | Reports |
|-----------|--------------|---------|---------|
| ![main-menu](screenshots/main-menu.png) | ![add-customer](screenshots/add-customer.png) | ![invoice](screenshots/invoice.png) | ![reports](screenshots/reports.png) |

---

## Future Enhancements

- Login/authentication for staff accounts
- Monthly sales report with date filters
- Export invoice to PDF
- GUI version using JavaFX or Swing

---

## Author

Rishi — Mini Project Submission
