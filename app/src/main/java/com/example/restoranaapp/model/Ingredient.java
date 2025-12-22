package com.example.restoranaapp.model;

public class Ingredient {
    private int id;
    private String name;
    private double quantity;
    private String unit;
    private String category;
    private double criticalThreshold;
    private String createdAt;
    private String updatedAt;

    public Ingredient() {
    }

    public Ingredient(String name, double quantity, String unit, String category, double criticalThreshold) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.category = category;
        this.criticalThreshold = criticalThreshold;
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getCriticalThreshold() {
        return criticalThreshold;
    }

    public void setCriticalThreshold(double criticalThreshold) {
        this.criticalThreshold = criticalThreshold;
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

    public boolean isLowStock() {
        return quantity < criticalThreshold;
    }
}

