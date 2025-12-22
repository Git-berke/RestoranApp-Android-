package com.example.restoranaapp.model;

public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private int categoryId;
    private String imagePath;
    private double stockQuantity;
    private String stockUnit;
    private double criticalLevel;
    private int isActive;
    private String createdAt;
    private String updatedAt;

    public Product() {
    }

    public Product(String name, String description, double price, int categoryId, String imagePath, int isActive) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.imagePath = imagePath;
        this.isActive = isActive;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(double stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getStockUnit() {
        return stockUnit;
    }

    public void setStockUnit(String stockUnit) {
        this.stockUnit = stockUnit;
    }

    public double getCriticalLevel() {
        return criticalLevel;
    }

    public void setCriticalLevel(double criticalLevel) {
        this.criticalLevel = criticalLevel;
    }

    public boolean isLowStock() {
        return stockQuantity < criticalLevel;
    }
}

