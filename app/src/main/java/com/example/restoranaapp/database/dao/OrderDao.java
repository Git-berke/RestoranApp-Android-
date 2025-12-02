package com.example.restoranaapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.restoranaapp.database.DatabaseHelper;
import com.example.restoranaapp.model.ActiveOrderDto;
import com.example.restoranaapp.model.Order;
import com.example.restoranaapp.model.OrderItem;
import com.example.restoranaapp.model.ProductSalesReport;

import java.util.ArrayList;
import java.util.List;

public class OrderDao {
    private DatabaseHelper dbHelper;

    public OrderDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // --- EXISTING METHODS ---

    public long createOrder(Order order, List<OrderItem> items) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long orderId = -1;
        
        db.beginTransaction();
        try {
            ContentValues orderValues = new ContentValues();
            orderValues.put(DatabaseHelper.COL_ORDER_TABLE_ID, order.getTableId());
            orderValues.put(DatabaseHelper.COL_ORDER_WAITER_ID, order.getWaiterId());
            orderValues.put(DatabaseHelper.COL_ORDER_STATUS, order.getStatus());
            orderValues.put(DatabaseHelper.COL_ORDER_TOTAL_PRICE, order.getTotalPrice());
            orderValues.put(DatabaseHelper.COL_CREATED_AT, order.getCreatedAt());
            
            orderId = db.insert(DatabaseHelper.TABLE_ORDERS, null, orderValues);
            
            if (orderId != -1) {
                for (OrderItem item : items) {
                    ContentValues itemValues = new ContentValues();
                    itemValues.put(DatabaseHelper.COL_ITEM_ORDER_ID, orderId);
                    itemValues.put(DatabaseHelper.COL_ITEM_PRODUCT_ID, item.getProductId());
                    itemValues.put(DatabaseHelper.COL_ITEM_QUANTITY, item.getQuantity());
                    itemValues.put(DatabaseHelper.COL_ITEM_UNIT_PRICE, item.getUnitPrice());
                    itemValues.put(DatabaseHelper.COL_ITEM_LINE_TOTAL, item.getLineTotal());
                    db.insert(DatabaseHelper.TABLE_ORDER_ITEMS, null, itemValues);
                }
                
                ContentValues tableValues = new ContentValues();
                tableValues.put(DatabaseHelper.COL_TABLE_STATUS, "ACTIVE");
                db.update(DatabaseHelper.TABLE_TABLES, tableValues, DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(order.getTableId())});
            }
            
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return orderId;
    }

    public List<Order> getActiveOrdersByTable(int tableId) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        // Only not finished orders
        // SERVED orders should still be visible on table as active for waiter to add more or close
        String selection = DatabaseHelper.COL_ORDER_TABLE_ID + " = ? AND " + DatabaseHelper.COL_ORDER_STATUS + " NOT IN ('PAID', 'CANCELLED')"; 
        String[] selectionArgs = {String.valueOf(tableId)};
        
        Cursor cursor = db.query(DatabaseHelper.TABLE_ORDERS, null, selection, selectionArgs, null, null, DatabaseHelper.COL_CREATED_AT + " DESC");
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
                order.setTableId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_TABLE_ID)));
                order.setWaiterId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_WAITER_ID)));
                order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_STATUS)));
                order.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_TOTAL_PRICE)));
                order.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CREATED_AT)));
                orders.add(order);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return orders;
    }
    
    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COL_ITEM_ORDER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(orderId)};
        Cursor cursor = db.query(DatabaseHelper.TABLE_ORDER_ITEMS, null, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                OrderItem item = new OrderItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
                item.setOrderId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_ORDER_ID)));
                item.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_PRODUCT_ID)));
                item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_QUANTITY)));
                item.setUnitPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_UNIT_PRICE)));
                item.setLineTotal(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_LINE_TOTAL)));
                items.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return items;
    }

    public void updateOrder(Order order, List<OrderItem> newItems) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
             ContentValues orderValues = new ContentValues();
             orderValues.put(DatabaseHelper.COL_ORDER_TOTAL_PRICE, order.getTotalPrice());
             orderValues.put(DatabaseHelper.COL_ORDER_STATUS, order.getStatus()); 
             orderValues.put(DatabaseHelper.COL_UPDATED_AT, order.getUpdatedAt());
             db.update(DatabaseHelper.TABLE_ORDERS, orderValues, DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(order.getId())});

             db.delete(DatabaseHelper.TABLE_ORDER_ITEMS, DatabaseHelper.COL_ITEM_ORDER_ID + " = ?", new String[]{String.valueOf(order.getId())});

             for (OrderItem item : newItems) {
                 ContentValues itemValues = new ContentValues();
                 itemValues.put(DatabaseHelper.COL_ITEM_ORDER_ID, order.getId());
                 itemValues.put(DatabaseHelper.COL_ITEM_PRODUCT_ID, item.getProductId());
                 itemValues.put(DatabaseHelper.COL_ITEM_QUANTITY, item.getQuantity());
                 itemValues.put(DatabaseHelper.COL_ITEM_UNIT_PRICE, item.getUnitPrice());
                 itemValues.put(DatabaseHelper.COL_ITEM_LINE_TOTAL, item.getLineTotal());
                 db.insert(DatabaseHelper.TABLE_ORDER_ITEMS, null, itemValues);
             }
             db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // --- REPORTING & LIST METHODS ---

    public List<ActiveOrderDto> getAllActiveOrders() {
        List<ActiveOrderDto> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        // Join with Tables to get table Name
        String query = "SELECT o." + DatabaseHelper.COL_ID + ", "
                     + "o." + DatabaseHelper.COL_ORDER_TABLE_ID + ", "
                     + "t." + DatabaseHelper.COL_TABLE_NAME + ", "
                     + "t." + DatabaseHelper.COL_TABLE_NUMBER + ", "
                     + "o." + DatabaseHelper.COL_ORDER_STATUS + ", "
                     + "o." + DatabaseHelper.COL_ORDER_TOTAL_PRICE + ", "
                     + "o." + DatabaseHelper.COL_CREATED_AT + " "
                     + "FROM " + DatabaseHelper.TABLE_ORDERS + " o "
                     + "JOIN " + DatabaseHelper.TABLE_TABLES + " t ON o." + DatabaseHelper.COL_ORDER_TABLE_ID + " = t." + DatabaseHelper.COL_ID + " "
                     + "WHERE o." + DatabaseHelper.COL_ORDER_STATUS + " NOT IN ('PAID', 'CANCELLED') " // Include SERVED in active list so we can close it
                     + "ORDER BY o." + DatabaseHelper.COL_CREATED_AT + " DESC";
                     
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int orderId = cursor.getInt(0);
                int tableId = cursor.getInt(1);
                String tName = cursor.getString(2);
                int tNum = cursor.getInt(3);
                String status = cursor.getString(4);
                double total = cursor.getDouble(5);
                String time = cursor.getString(6);
                
                String displayName = (tName != null && !tName.isEmpty()) ? tName : "Masa " + tNum;
                
                list.add(new ActiveOrderDto(orderId, tableId, displayName, status, total, time));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public Order getOrderById(int orderId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_ORDERS, null, DatabaseHelper.COL_ID + "=?", new String[]{String.valueOf(orderId)}, null, null, null);
        Order order = null;
        if (cursor != null && cursor.moveToFirst()) {
            order = new Order();
            order.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
            order.setTableId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_TABLE_ID)));
            order.setWaiterId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_WAITER_ID)));
            order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_STATUS)));
            order.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_TOTAL_PRICE)));
            order.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CREATED_AT)));
            cursor.close();
        }
        return order;
    }

    public double getTotalRevenue(String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;
        String query = "SELECT SUM(" + DatabaseHelper.COL_ORDER_TOTAL_PRICE + ") FROM " + DatabaseHelper.TABLE_ORDERS 
                     + " WHERE " + DatabaseHelper.COL_ORDER_STATUS + " IN ('SERVED', 'PAID')"
                     + " AND " + DatabaseHelper.COL_CREATED_AT + " BETWEEN ? AND ?";
        Cursor cursor = db.rawQuery(query, new String[]{startDate, endDate});
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public int getTotalItemsSold(String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int total = 0;
        String query = "SELECT SUM(oi." + DatabaseHelper.COL_ITEM_QUANTITY + ") FROM " + DatabaseHelper.TABLE_ORDER_ITEMS + " oi "
                     + "JOIN " + DatabaseHelper.TABLE_ORDERS + " o ON oi." + DatabaseHelper.COL_ITEM_ORDER_ID + " = o." + DatabaseHelper.COL_ID
                     + " WHERE o." + DatabaseHelper.COL_ORDER_STATUS + " IN ('SERVED', 'PAID')"
                     + " AND o." + DatabaseHelper.COL_CREATED_AT + " BETWEEN ? AND ?";
        Cursor cursor = db.rawQuery(query, new String[]{startDate, endDate});
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public String getTopSellingProduct(String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String productName = "-";
        String query = "SELECT p." + DatabaseHelper.COL_PRODUCT_NAME + ", SUM(oi." + DatabaseHelper.COL_ITEM_QUANTITY + ") as total_qty "
                     + "FROM " + DatabaseHelper.TABLE_ORDER_ITEMS + " oi "
                     + "JOIN " + DatabaseHelper.TABLE_ORDERS + " o ON oi." + DatabaseHelper.COL_ITEM_ORDER_ID + " = o." + DatabaseHelper.COL_ID + " "
                     + "JOIN " + DatabaseHelper.TABLE_PRODUCTS + " p ON oi." + DatabaseHelper.COL_ITEM_PRODUCT_ID + " = p." + DatabaseHelper.COL_ID + " "
                     + "WHERE o." + DatabaseHelper.COL_ORDER_STATUS + " IN ('SERVED', 'PAID') "
                     + "AND o." + DatabaseHelper.COL_CREATED_AT + " BETWEEN ? AND ? "
                     + "GROUP BY p." + DatabaseHelper.COL_ID + " "
                     + "ORDER BY total_qty DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, new String[]{startDate, endDate});
        if (cursor.moveToFirst()) {
            productName = cursor.getString(0);
        }
        cursor.close();
        return productName;
    }

    public List<ProductSalesReport> getProductSalesReports(String startDate, String endDate) {
        List<ProductSalesReport> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT p." + DatabaseHelper.COL_PRODUCT_NAME + ", "
                     + "SUM(oi." + DatabaseHelper.COL_ITEM_QUANTITY + ") as total_qty, "
                     + "SUM(oi." + DatabaseHelper.COL_ITEM_LINE_TOTAL + ") as total_amt "
                     + "FROM " + DatabaseHelper.TABLE_ORDER_ITEMS + " oi "
                     + "JOIN " + DatabaseHelper.TABLE_ORDERS + " o ON oi." + DatabaseHelper.COL_ITEM_ORDER_ID + " = o." + DatabaseHelper.COL_ID + " "
                     + "JOIN " + DatabaseHelper.TABLE_PRODUCTS + " p ON oi." + DatabaseHelper.COL_ITEM_PRODUCT_ID + " = p." + DatabaseHelper.COL_ID + " "
                     + "WHERE o." + DatabaseHelper.COL_ORDER_STATUS + " IN ('SERVED', 'PAID') "
                     + "AND o." + DatabaseHelper.COL_CREATED_AT + " BETWEEN ? AND ? "
                     + "GROUP BY p." + DatabaseHelper.COL_ID + " "
                     + "ORDER BY total_amt DESC";
        Cursor cursor = db.rawQuery(query, new String[]{startDate, endDate});
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                int qty = cursor.getInt(1);
                double total = cursor.getDouble(2);
                list.add(new ProductSalesReport(name, qty, total));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    
    // Complete Order and Empty Table
    public void completeOrderAndFreeTable(int orderId, int tableId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // Set Order to SERVED
            ContentValues orderValues = new ContentValues();
            orderValues.put(DatabaseHelper.COL_ORDER_STATUS, "SERVED");
            db.update(DatabaseHelper.TABLE_ORDERS, orderValues, DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(orderId)});

            // Set Table to EMPTY
            ContentValues tableValues = new ContentValues();
            tableValues.put(DatabaseHelper.COL_TABLE_STATUS, "EMPTY");
            db.update(DatabaseHelper.TABLE_TABLES, tableValues, DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(tableId)});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void payAndCloseOrder(int orderId, int tableId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues orderValues = new ContentValues();
            orderValues.put(DatabaseHelper.COL_ORDER_STATUS, "PAID");
            db.update(DatabaseHelper.TABLE_ORDERS, orderValues, DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(orderId)});

            ContentValues tableValues = new ContentValues();
            tableValues.put(DatabaseHelper.COL_TABLE_STATUS, "EMPTY");
            db.update(DatabaseHelper.TABLE_TABLES, tableValues, DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(tableId)});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void updateOrderStatus(int orderId, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_ORDER_STATUS, status);
        db.update(DatabaseHelper.TABLE_ORDERS, values, DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(orderId)});
    }
}
