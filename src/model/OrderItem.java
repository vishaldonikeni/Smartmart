package model;

import java.sql.Timestamp;

public class Order {
    private int orderId;
    private int customerId;
    private Timestamp orderDate;

    public Order() {}

    public Order(int orderId, int customerId, Timestamp orderDate) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
    }

    public Order(int customerId) {
        this.customerId = customerId;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

    @Override
    public String toString() {
        return String.format("Order #%d | Customer ID: %d | Date: %s", orderId, customerId, orderDate);
    }
}
