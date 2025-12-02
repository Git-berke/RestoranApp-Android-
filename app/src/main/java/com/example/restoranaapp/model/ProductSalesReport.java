package com.example.restoranaapp.model;

public class ProductSalesReport {
    private String productName;
    private int quantity;
    private double totalAmount;

    public ProductSalesReport(String productName, int quantity, double totalAmount) {
        this.productName = productName;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}

