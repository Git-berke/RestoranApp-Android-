package com.example.restoranaapp.model;

public class RestaurantTable {
    private int id;
    private int tableNumber;
    private String tableName;
    private String status; // EMPTY, ACTIVE, RESERVED
    private String area; // Salon, Bahçe, Teras
    private int isActive;
    private String createdAt;
    private String updatedAt;

    public RestaurantTable() {
    }

    public RestaurantTable(int tableNumber, String tableName, String status, String area, int isActive) {
        this.tableNumber = tableNumber;
        this.tableName = tableName;
        this.status = status;
        this.area = area;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}

