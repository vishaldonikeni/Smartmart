package service;

import dao.CustomerDAO;
import dao.OrderDAO;
import dao.ProductDAO;
import model.*;

import java.util.List;

/**
 * Service layer that coordinates DAOs and contains business logic
 * such as bill calculation and invoice printing.
 */
public class SalesService {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    // ---- Customer operations ----
    public boolean addCustomer(Customer c) { return customerDAO.addCustomer(c); }
    public boolean updateCustomer(Customer c) { return customerDAO.updateCustomer(c); }
    public boolean deleteCustomer(int id) { return customerDAO.deleteCustomer(id); }
    public Customer getCustomer(int id) { return customerDAO.getCustomerById(id); }
    public List<Customer> searchCustomers(String name) { return customerDAO.searchCustomerByName(name); }
    public List<Customer> getAllCustomers() { return customerDAO.getAllCustomers(); }

    // ---- Product operations ----
    public boolean addProduct(Product p) { return productDAO.addProduct(p); }
    public boolean updateProduct(Product p) { return productDAO.updateProduct(p); }
    public boolean deleteProduct(int id) { return productDAO.deleteProduct(id); }
    public Product getProduct(int id) { return productDAO.getProductById(id); }
    public List<Product> searchProducts(String name) { return productDAO.searchProductByName(name); }
    public List<Product> getAllProducts() { return productDAO.getAllProducts(); }
    public List<Product> getLowStockProducts(int threshold) { return productDAO.getLowStockProducts(threshold); }

    // ---- Order operations ----
    public int createOrder(int customerId, List<OrderItem> items) {
        return orderDAO.createOrder(customerId, items);
    }

    public double calculateBill(List<OrderItem> items) {
        double total = 0;
        for (OrderItem item : items) total += item.getSubTotal();
        return total;
    }

    public void printInvoice(int orderId) {
        Order order = orderDAO.getOrderById(orderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }
        Customer customer = customerDAO.getCustomerById(order.getCustomerId());
        List<OrderItem> items = orderDAO.getOrderItems(orderId);

        System.out.println("=".repeat(50));
        System.out.println("               SMARTMART INVOICE");
        System.out.println("=".repeat(50));
        System.out.println("Order ID   : " + order.getOrderId());
        System.out.println("Date       : " + order.getOrderDate());
        System.out.println("Customer   : " + (customer != null ? customer.getCustomerName() : "N/A"));
        System.out.println("-".repeat(50));
        System.out.printf("%-20s %-8s %-10s %-10s%n", "Product", "Qty", "Price", "Subtotal");
        System.out.println("-".repeat(50));

        double total = 0;
        for (OrderItem item : items) {
            System.out.printf("%-20s %-8d %-10.2f %-10.2f%n",
                    item.getProductName(), item.getQuantity(), item.getPrice(), item.getSubTotal());
            total += item.getSubTotal();
        }

        System.out.println("-".repeat(50));
        System.out.printf("TOTAL: %.2f%n", total);
        System.out.println("=".repeat(50));
    }

    // ---- Reports ----
    public double getTotalSales() { return orderDAO.getTotalSales(); }
    public String getBestSellingProduct() { return orderDAO.getBestSellingProduct(); }
    public void printCustomerPurchaseReport() { orderDAO.printCustomerPurchaseReport(); }
    public void printCustomersWithoutOrders() { orderDAO.printCustomersWithoutOrders(); }
    public void printTopCustomers(int limit) { orderDAO.printTopCustomers(limit); }
    public double getAverageOrderValue() { return orderDAO.getAverageOrderValue(); }
}
