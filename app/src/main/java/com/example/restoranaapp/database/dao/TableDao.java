package com.example.restoranaapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.restoranaapp.database.DatabaseHelper;
import com.example.restoranaapp.model.RestaurantTable;

import java.util.ArrayList;
import java.util.List;

public class TableDao {
    private DatabaseHelper dbHelper;

    public TableDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long addTable(RestaurantTable table) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_TABLE_NUMBER, table.getTableNumber());
        values.put(DatabaseHelper.COL_TABLE_NAME, table.getTableName());
        values.put(DatabaseHelper.COL_TABLE_STATUS, table.getStatus() != null ? table.getStatus() : "EMPTY"); // Default
        values.put(DatabaseHelper.COL_TABLE_AREA, table.getArea() != null ? table.getArea() : "Salon"); // Default
        values.put(DatabaseHelper.COL_IS_ACTIVE, table.getIsActive());
        values.put(DatabaseHelper.COL_CREATED_AT, table.getCreatedAt());
        
        return db.insert(DatabaseHelper.TABLE_TABLES, null, values);
    }

    public List<RestaurantTable> getAllTables() {
        List<RestaurantTable> tables = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        // Order by table number
        Cursor cursor = db.query(DatabaseHelper.TABLE_TABLES, null, null, null, null, null, DatabaseHelper.COL_TABLE_NUMBER + " ASC");
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                RestaurantTable table = new RestaurantTable();
                table.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
                table.setTableNumber(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TABLE_NUMBER)));
                table.setTableName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TABLE_NAME)));
                table.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TABLE_STATUS)));
                table.setIsActive(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_IS_ACTIVE)));
                
                // Handle area field (may not exist in older DB versions)
                try {
                    table.setArea(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TABLE_AREA)));
                } catch (Exception e) {
                    table.setArea("Salon"); // Default
                }
                
                tables.add(table);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return tables;
    }

    public int updateTableStatus(int tableId, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_TABLE_STATUS, status);
        return db.update(DatabaseHelper.TABLE_TABLES, values, DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(tableId)});
    }

    public int updateTable(RestaurantTable table) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_TABLE_NUMBER, table.getTableNumber());
        values.put(DatabaseHelper.COL_TABLE_NAME, table.getTableName());
        values.put(DatabaseHelper.COL_TABLE_AREA, table.getArea());
        values.put(DatabaseHelper.COL_IS_ACTIVE, table.getIsActive());
        
        return db.update(DatabaseHelper.TABLE_TABLES, values, DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(table.getId())});
    }
}

