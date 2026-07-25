package util;

import model.*;
import service.SalesService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {

    private final Scanner sc = new Scanner(System.in);
    private final SalesService service = new SalesService();

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n===== SmartMart Sales & Inventory Management System =====");
            System.out.println("1. Customer Management");
            System.out.println("2. Product Management");
            System.out.println("3. Order Management");
            System.out.println("4. Reports");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> customerMenu();
                case 2 -> productMenu();
                case 3 -> orderMenu();
                case 4 -> reportsMenu();
                case 5 -> running = false;
                default -> System.out.println("Invalid option. Try again.");
            }
        }
        System.out.println("Thank you for using SmartMart. Goodbye!");
    }

    // ---------------- CUSTOMER MENU ----------------
    private void customerMenu() {
        System.out.println("\n--- Customer Management ---");
        System.out.println("1. Add Customer");
        System.out.println("2. Update Customer");
        System.out.println("3. Delete Customer");
        System.out.println("4. Search Customer");
        System.out.println("5. View All Customers");
        System.out.println("6. Back");
        System.out.print("Choose an option: ");

        switch (readInt()) {
            case 1 -> {
                System.out.print("Name: ");
                String name = sc.nextLine();
                System.out.print("Phone: ");
                String phone = sc.nextLine();
                System.out.print("Email: ");
                String email = sc.nextLine();
                System.out.print("City: ");
                String city = sc.nextLine();
                boolean ok = service.addCustomer(new Customer(name, phone, email, city));
                System.out.println(ok ? "Customer added." : "Failed to add customer.");
            }
            case 2 -> {
                System.out.print("Customer ID to update: ");
                int id = readInt();
                Customer existing = service.getCustomer(id);
                if (existing == null) { System.out.println("Customer not found."); return; }
                System.out.print("New Name (" + existing.getCustomerName() + "): ");
                existing.setCustomerName(sc.nextLine());
                System.out.print("New Phone (" + existing.getPhone() + "): ");
                existing.setPhone(sc.nextLine());
                System.out.print("New Email (" + existing.getEmail() + "): ");
                existing.setEmail(sc.nextLine());
                System.out.print("New City (" + existing.getCity() + "): ");
                existing.setCity(sc.nextLine());
                boolean ok = service.updateCustomer(existing);
                System.out.println(ok ? "Customer updated." : "Failed to update customer.");
            }
            case 3 -> {
                System.out.print("Customer ID to delete: ");
                int id = readInt();
                boolean ok = service.deleteCustomer(id);
                System.out.println(ok ? "Customer deleted." : "Failed to delete customer.");
            }
            case 4 -> {
                System.out.print("Search by name: ");
                String name = sc.nextLine();
                List<Customer> results = service.searchCustomers(name);
                printCustomers(results);
            }
            case 5 -> printCustomers(service.getAllCustomers());
            case 6 -> { }
            default -> System.out.println("Invalid option.");
        }
    }

    private void printCustomers(List<Customer> customers) {
        if (customers.isEmpty()) { System.out.println("No customers found."); return; }
        System.out.printf("%-5s %-20s %-15s %-25s %-15s%n", "ID", "Name", "Phone", "Email", "City");
        System.out.println("-".repeat(85));
        for (Customer c : customers) System.out.println(c);
    }

    // ---------------- PRODUCT MENU ----------------
    private void productMenu() {
        System.out.println("\n--- Product Management ---");
        System.out.println("1. Add Product");
        System.out.println("2. Update Product");
        System.out.println("3. Delete Product");
        System.out.println("4. Search Product");
        System.out.println("5. View All Products");
        System.out.println("6. Low Stock Alert");
        System.out.println("7. Back");
        System.out.print("Choose an option: ");

        switch (readInt()) {
            case 1 -> {
                System.out.print("Name: ");
                String name = sc.nextLine();
                System.out.print("Category: ");
                String category = sc.nextLine();
                System.out.print("Price: ");
                double price = readDouble();
                System.out.print("Stock: ");
                int stock = readInt();
                boolean ok = service.addProduct(new Product(name, category, price, stock));
                System.out.println(ok ? "Product added." : "Failed to add product.");
            }
            case 2 -> {
                System.out.print("Product ID to update: ");
                int id = readInt();
                Product existing = service.getProduct(id);
                if (existing == null) { System.out.println("Product not found."); return; }
                System.out.print("New Name (" + existing.getProductName() + "): ");
                existing.setProductName(sc.nextLine());
                System.out.print("New Category (" + existing.getCategory() + "): ");
                existing.setCategory(sc.nextLine());
                System.out.print("New Price (" + existing.getPrice() + "): ");
                existing.setPrice(readDouble());
                System.out.print("New Stock (" + existing.getStock() + "): ");
                existing.setStock(readInt());
                boolean ok = service.updateProduct(existing);
                System.out.println(ok ? "Product updated." : "Failed to update product.");
            }
            case 3 -> {
                System.out.print("Product ID to delete: ");
                int id = readInt();
                boolean ok = service.deleteProduct(id);
                System.out.println(ok ? "Product deleted." : "Failed to delete product.");
            }
            case 4 -> {
                System.out.print("Search by name: ");
                String name = sc.nextLine();
                printProducts(service.searchProducts(name));
            }
            case 5 -> printProducts(service.getAllProducts());
            case 6 -> {
                System.out.print("Stock threshold (e.g. 5): ");
                int threshold = readInt();
                printProducts(service.getLowStockProducts(threshold));
            }
            case 7 -> { }
            default -> System.out.println("Invalid option.");
        }
    }

    private void printProducts(List<Product> products) {
        if (products.isEmpty()) { System.out.println("No products found."); return; }
        System.out.printf("%-5s %-20s %-15s %-10s %-8s%n", "ID", "Name", "Category", "Price", "Stock");
        System.out.println("-".repeat(65));
        for (Product p : products) System.out.println(p);
    }

    // ---------------- ORDER MENU ----------------
    private void orderMenu() {
        System.out.println("\n--- Order Management ---");
        System.out.println("1. Create New Order");
        System.out.println("2. Print Invoice");
        System.out.println("3. Back");
        System.out.print("Choose an option: ");

        switch (readInt()) {
            case 1 -> createOrderFlow();
            case 2 -> {
                System.out.print("Order ID: ");
                service.printInvoice(readInt());
            }
            case 3 -> { }
            default -> System.out.println("Invalid option.");
        }
    }

    private void createOrderFlow() {
        System.out.print("Customer ID: ");
        int customerId = readInt();
        if (service.getCustomer(customerId) == null) {
            System.out.println("Customer not found. Please add the customer first.");
            return;
        }

        List<OrderItem> items = new ArrayList<>();
        boolean addingItems = true;
        while (addingItems) {
            System.out.print("Product ID (0 to finish): ");
            int productId = readInt();
            if (productId == 0) { addingItems = false; continue; }

            Product product = service.getProduct(productId);
            if (product == null) { System.out.println("Product not found."); continue; }

            System.out.print("Quantity: ");
            int qty = readInt();
            if (qty <= 0 || qty > product.getStock()) {
                System.out.println("Invalid quantity or insufficient stock (available: " + product.getStock() + ").");
                continue;
            }

            items.add(new OrderItem(0, productId, qty, product.getPrice()));
            System.out.println(product.getProductName() + " x " + qty + " added.");
        }

        if (items.isEmpty()) {
            System.out.println("No items added. Order cancelled.");
            return;
        }

        double bill = service.calculateBill(items);
        System.out.printf("Total Bill: %.2f%n", bill);
        System.out.print("Confirm order? (y/n): ");
        String confirm = sc.nextLine();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Order cancelled.");
            return;
        }

        int orderId = service.createOrder(customerId, items);
        if (orderId != -1) {
            System.out.println("Order created successfully. Order ID: " + orderId);
            service.printInvoice(orderId);
        } else {
            System.out.println("Order failed. Please check stock availability.");
        }
    }

    // ---------------- REPORTS MENU ----------------
    private void reportsMenu() {
        System.out.println("\n--- Reports ---");
        System.out.println("1. Total Sales");
        System.out.println("2. Best Selling Product");
        System.out.println("3. Customer Purchase Report (INNER JOIN)");
        System.out.println("4. Customers Without Orders (LEFT JOIN)");
        System.out.println("5. Top Customers (GROUP BY, SUM)");
        System.out.println("6. Average Order Value");
        System.out.println("7. Back");
        System.out.print("Choose an option: ");

        switch (readInt()) {
            case 1 -> System.out.printf("Total Sales: %.2f%n", service.getTotalSales());
            case 2 -> System.out.println("Best Selling Product: " + service.getBestSellingProduct());
            case 3 -> service.printCustomerPurchaseReport();
            case 4 -> service.printCustomersWithoutOrders();
            case 5 -> {
                System.out.print("How many top customers to show: ");
                service.printTopCustomers(readInt());
            }
            case 6 -> System.out.printf("Average Order Value: %.2f%n", service.getAverageOrderValue());
            case 7 -> { }
            default -> System.out.println("Invalid option.");
        }
    }

    // ---------------- INPUT HELPERS ----------------
    private int readInt() {
        while (true) {
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    private double readDouble() {
        while (true) {
            String line = sc.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
}
