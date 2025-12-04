package com.example.restoranaapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.restoranaapp.database.DatabaseHelper;
import com.example.restoranaapp.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductDao {
    private DatabaseHelper dbHelper;

    public ProductDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long addProduct(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_PRODUCT_NAME, product.getName());
        values.put(DatabaseHelper.COL_PRODUCT_DESCRIPTION, product.getDescription());
        values.put(DatabaseHelper.COL_PRODUCT_PRICE, product.getPrice());
        values.put(DatabaseHelper.COL_PRODUCT_CATEGORY_ID, product.getCategoryId());
        values.put(DatabaseHelper.COL_PRODUCT_IMAGE_PATH, product.getImagePath());
        values.put(DatabaseHelper.COL_IS_ACTIVE, product.getIsActive());
        values.put(DatabaseHelper.COL_CREATED_AT, product.getCreatedAt());
        
        return db.insert(DatabaseHelper.TABLE_PRODUCTS, null, values);
    }

    public List<Product> getProductsByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String selection = DatabaseHelper.COL_PRODUCT_CATEGORY_ID + " = ? AND " + DatabaseHelper.COL_IS_ACTIVE + " = 1";
        String[] selectionArgs = {String.valueOf(categoryId)};
        
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, null, selection, selectionArgs, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                products.add(cursorToProduct(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return products;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, null, null, null, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                products.add(cursorToProduct(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return products;
    }

    private Product cursorToProduct(Cursor cursor) {
        Product product = new Product();
        product.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
        product.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_NAME)));
        product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_DESCRIPTION)));
        product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_PRICE)));
        product.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_CATEGORY_ID)));
        product.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_IMAGE_PATH)));
        product.setIsActive(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_IS_ACTIVE)));
        
        // Handle stock fields (may not exist in older DB versions)
        try {
            product.setStockQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_STOCK_QUANTITY)));
            product.setStockUnit(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_STOCK_UNIT)));
            product.setCriticalLevel(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_CRITICAL_LEVEL)));
        } catch (Exception e) {
            // Set defaults if columns don't exist
            product.setStockQuantity(0);
            product.setStockUnit("Adet");
            product.setCriticalLevel(10);
        }
        
        return product;
    }

    public int updateProduct(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_PRODUCT_NAME, product.getName());
        values.put(DatabaseHelper.COL_PRODUCT_DESCRIPTION, product.getDescription());
        values.put(DatabaseHelper.COL_PRODUCT_PRICE, product.getPrice());
        values.put(DatabaseHelper.COL_PRODUCT_CATEGORY_ID, product.getCategoryId());
        values.put(DatabaseHelper.COL_PRODUCT_IMAGE_PATH, product.getImagePath());
        values.put(DatabaseHelper.COL_IS_ACTIVE, product.getIsActive());
        values.put(DatabaseHelper.COL_PRODUCT_STOCK_QUANTITY, product.getStockQuantity());
        values.put(DatabaseHelper.COL_PRODUCT_STOCK_UNIT, product.getStockUnit());
        values.put(DatabaseHelper.COL_PRODUCT_CRITICAL_LEVEL, product.getCriticalLevel());
        
        return db.update(DatabaseHelper.TABLE_PRODUCTS, values, DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(product.getId())});
    }
    
    // Soft delete / make passive
    public int deleteProduct(int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_IS_ACTIVE, 0);
        return db.update(DatabaseHelper.TABLE_PRODUCTS, values, DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(productId)});
    }
}

