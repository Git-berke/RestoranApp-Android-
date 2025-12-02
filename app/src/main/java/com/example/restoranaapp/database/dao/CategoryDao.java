package com.example.restoranaapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.restoranaapp.database.DatabaseHelper;
import com.example.restoranaapp.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDao {
    private DatabaseHelper dbHelper;

    public CategoryDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long addCategory(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_CATEGORY_NAME, category.getName());
        values.put(DatabaseHelper.COL_IS_ACTIVE, category.getIsActive());
        values.put(DatabaseHelper.COL_CREATED_AT, category.getCreatedAt());
        
        return db.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        // Usually show active ones, or all for admin management
        Cursor cursor = db.query(DatabaseHelper.TABLE_CATEGORIES, null, null, null, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
                category.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CATEGORY_NAME)));
                category.setIsActive(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_IS_ACTIVE)));
                categories.add(category);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return categories;
    }

    public int updateCategory(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_CATEGORY_NAME, category.getName());
        values.put(DatabaseHelper.COL_IS_ACTIVE, category.getIsActive());
        
        return db.update(DatabaseHelper.TABLE_CATEGORIES, values, DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(category.getId())});
    }

    // Check if category has products before delete (G10)
    public boolean hasProducts(int categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, new String[]{DatabaseHelper.COL_ID}, 
                DatabaseHelper.COL_PRODUCT_CATEGORY_ID + " = ? AND " + DatabaseHelper.COL_IS_ACTIVE + " = 1", 
                new String[]{String.valueOf(categoryId)}, null, null, null);
        boolean has = (cursor != null && cursor.getCount() > 0);
        if(cursor != null) cursor.close();
        return has;
    }

    public int deleteCategory(int categoryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_CATEGORIES, DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(categoryId)});
    }
}

