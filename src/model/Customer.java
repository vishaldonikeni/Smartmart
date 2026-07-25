package model;

public class Customer {
    private int customerId;
    private String customerName;
    private String phone;
    private String email;
    private String city;

    public Customer() {}

    public Customer(int customerId, String customerName, String phone, String email, String city) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.phone = phone;
        this.email = email;
        this.city = city;
    }

    public Customer(String customerName, String phone, String email, String city) {
        this.customerName = customerName;
        this.phone = phone;
        this.email = email;
        this.city = city;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    @Override
    public String toString() {
        return String.format("%-5d %-20s %-15s %-25s %-15s",
                customerId, customerName, phone, email, city);
    }
}
