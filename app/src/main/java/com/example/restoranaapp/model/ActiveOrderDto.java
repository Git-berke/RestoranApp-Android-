package com.example.restoranaapp.model;

public class ActiveOrderDto {
    private int orderId;
    private int tableId;
    private String tableName;
    private String status;
    private double totalPrice;
    private String createdAt;

    public ActiveOrderDto(int orderId, int tableId, String tableName, String status, double totalPrice, String createdAt) {
        this.orderId = orderId;
        this.tableId = tableId;
        this.tableName = tableName;
        this.status = status;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    public int getOrderId() { return orderId; }
    public int getTableId() { return tableId; }
    public String getTableName() { return tableName; }
    public String getStatus() { return status; }
    public double getTotalPrice() { return totalPrice; }
    public String getCreatedAt() { return createdAt; }
}

