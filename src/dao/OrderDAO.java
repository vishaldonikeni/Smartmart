package dao;

import db.DBConnection;
import model.Order;
import model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    /**
     * Creates an order along with its items in a single transaction.
     * Also decrements stock for each product purchased.
     * Returns the generated order_id, or -1 on failure.
     */
    public int createOrder(int customerId, List<OrderItem> items) {
        Connection conn = DBConnection.getConnection();
        String orderSql = "INSERT INTO Orders (customer_id, order_date) VALUES (?, NOW())";
        String itemSql = "INSERT INTO Order_Items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        String stockCheckSql = "SELECT stock FROM Products WHERE product_id=? FOR UPDATE";
        String stockUpdateSql = "UPDATE Products SET stock = stock - ? WHERE product_id=?";

        int generatedOrderId = -1;

        try {
            conn.setAutoCommit(false);

            // Validate stock first
            for (OrderItem item : items) {
                try (PreparedStatement ps = conn.prepareStatement(stockCheckSql)) {
                    ps.setInt(1, item.getProductId());
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        int currentStock = rs.getInt("stock");
                        if (currentStock < item.getQuantity()) {
                            System.out.println("Insufficient stock for product ID " + item.getProductId());
                            conn.rollback();
                            return -1;
                        }
                    } else {
                        System.out.println("Product ID " + item.getProductId() + " not found.");
                        conn.rollback();
                        return -1;
                    }
                }
            }

            // Insert order
            try (PreparedStatement ps = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, customerId);
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) generatedOrderId = keys.getInt(1);
            }

            // Insert items + update stock
            for (OrderItem item : items) {
                try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
                    ps.setInt(1, generatedOrderId);
                    ps.setInt(2, item.getProductId());
                    ps.setInt(3, item.getQuantity());
                    ps.setDouble(4, item.getPrice());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(stockUpdateSql)) {
                    ps.setInt(1, item.getQuantity());
                    ps.setInt(2, item.getProductId());
                    ps.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            System.out.println("Error creating order: " + e.getMessage());
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return -1;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }

        return generatedOrderId;
    }

    public List<OrderItem> getOrderItems(int orderId) {
        String sql = "SELECT oi.*, p.product_name FROM Order_Items oi " +
                "JOIN Products p ON oi.product_id = p.product_id WHERE oi.order_id=?";
        List<OrderItem> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem(
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                );
                item.setItemId(rs.getInt("item_id"));
                item.setProductName(rs.getString("product_name"));
                list.add(item);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching order items: " + e.getMessage());
        }
        return list;
    }

    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM Orders WHERE order_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Order(rs.getInt("order_id"), rs.getInt("customer_id"), rs.getTimestamp("order_date"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching order: " + e.getMessage());
        }
        return null;
    }

    public List<Order> getAllOrders() {
        String sql = "SELECT * FROM Orders ORDER BY order_id DESC";
        List<Order> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Order(rs.getInt("order_id"), rs.getInt("customer_id"), rs.getTimestamp("order_date")));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching orders: " + e.getMessage());
        }
        return list;
    }

    // ---------------- REPORTS ----------------

    /** Total sales revenue across all orders. */
    public double getTotalSales() {
        String sql = "SELECT SUM(quantity * price) AS total FROM Order_Items";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            System.out.println("Error computing total sales: " + e.getMessage());
        }
        return 0.0;
    }

    /** Best selling product by total quantity sold. */
    public String getBestSellingProduct() {
        String sql = "SELECT p.product_name, SUM(oi.quantity) AS total_qty " +
                "FROM Order_Items oi JOIN Products p ON oi.product_id = p.product_id " +
                "GROUP BY oi.product_id, p.product_name " +
                "ORDER BY total_qty DESC LIMIT 1";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString("product_name") + " (" + rs.getInt("total_qty") + " units sold)";
            }
        } catch (SQLException e) {
            System.out.println("Error computing best seller: " + e.getMessage());
        }
        return "No sales yet";
    }

    /** Customer purchase report using INNER JOIN across Customers, Orders, Order_Items, Products. */
    public void printCustomerPurchaseReport() {
        String sql = "SELECT c.customer_name, p.product_name, oi.quantity, oi.price, o.order_date " +
                "FROM Customers c " +
                "INNER JOIN Orders o ON c.customer_id = o.customer_id " +
                "INNER JOIN Order_Items oi ON o.order_id = oi.order_id " +
                "INNER JOIN Products p ON oi.product_id = p.product_id " +
                "ORDER BY c.customer_name, o.order_date";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.printf("%-20s %-20s %-10s %-10s %-20s%n", "Customer", "Product", "Qty", "Price", "Order Date");
            System.out.println("-".repeat(85));
            while (rs.next()) {
                System.out.printf("%-20s %-20s %-10d %-10.2f %-20s%n",
                        rs.getString("customer_name"), rs.getString("product_name"),
                        rs.getInt("quantity"), rs.getDouble("price"), rs.getTimestamp("order_date"));
            }
        } catch (SQLException e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    /** Customers who have never placed an order, using LEFT JOIN. */
    public void printCustomersWithoutOrders() {
        String sql = "SELECT c.customer_id, c.customer_name, c.email " +
                "FROM Customers c " +
                "LEFT JOIN Orders o ON c.customer_id = o.customer_id " +
                "WHERE o.order_id IS NULL";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.printf("%-5s %-20s %-25s%n", "ID", "Name", "Email");
            System.out.println("-".repeat(55));
            while (rs.next()) {
                System.out.printf("%-5d %-20s %-25s%n",
                        rs.getInt("customer_id"), rs.getString("customer_name"), rs.getString("email"));
            }
        } catch (SQLException e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    /** Top customers ranked by total spend, using GROUP BY + SUM. */
    public void printTopCustomers(int limit) {
        String sql = "SELECT c.customer_name, SUM(oi.quantity * oi.price) AS total_spent, COUNT(DISTINCT o.order_id) AS order_count " +
                "FROM Customers c " +
                "JOIN Orders o ON c.customer_id = o.customer_id " +
                "JOIN Order_Items oi ON o.order_id = oi.order_id " +
                "GROUP BY c.customer_id, c.customer_name " +
                "ORDER BY total_spent DESC LIMIT ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            System.out.printf("%-20s %-15s %-12s%n", "Customer", "Total Spent", "Orders");
            System.out.println("-".repeat(50));
            while (rs.next()) {
                System.out.printf("%-20s %-15.2f %-12d%n",
                        rs.getString("customer_name"), rs.getDouble("total_spent"), rs.getInt("order_count"));
            }
        } catch (SQLException e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    /** Average order value across all orders. */
    public double getAverageOrderValue() {
        String sql = "SELECT AVG(order_total) AS avg_total FROM " +
                "(SELECT o.order_id, SUM(oi.quantity * oi.price) AS order_total " +
                "FROM Orders o JOIN Order_Items oi ON o.order_id = oi.order_id " +
                "GROUP BY o.order_id) AS sub";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble("avg_total");
        } catch (SQLException e) {
            System.out.println("Error computing average order value: " + e.getMessage());
        }
        return 0.0;
    }
}
