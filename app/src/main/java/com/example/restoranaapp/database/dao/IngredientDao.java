package com.example.restoranaapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.restoranaapp.database.DatabaseHelper;
import com.example.restoranaapp.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientDao {
    private DatabaseHelper dbHelper;

    public IngredientDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long addIngredient(Ingredient ingredient) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_INGREDIENT_NAME, ingredient.getName());
        values.put(DatabaseHelper.COL_INGREDIENT_QUANTITY, ingredient.getQuantity());
        values.put(DatabaseHelper.COL_INGREDIENT_UNIT, ingredient.getUnit());
        values.put(DatabaseHelper.COL_INGREDIENT_CATEGORY, ingredient.getCategory());
        values.put(DatabaseHelper.COL_INGREDIENT_CRITICAL_THRESHOLD, ingredient.getCriticalThreshold());
        values.put(DatabaseHelper.COL_CREATED_AT, "datetime('now')");
        
        return db.insert(DatabaseHelper.TABLE_INGREDIENTS, null, values);
    }

    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_INGREDIENTS, null, null, null, null, null, 
                DatabaseHelper.COL_INGREDIENT_NAME + " ASC");
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ingredients.add(cursorToIngredient(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return ingredients;
    }

    public List<Ingredient> getIngredientsByCategory(String category) {
        List<Ingredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String selection = DatabaseHelper.COL_INGREDIENT_CATEGORY + " = ?";
        String[] selectionArgs = {category};
        
        Cursor cursor = db.query(DatabaseHelper.TABLE_INGREDIENTS, null, selection, selectionArgs, 
                null, null, DatabaseHelper.COL_INGREDIENT_NAME + " ASC");
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ingredients.add(cursorToIngredient(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return ingredients;
    }

    private Ingredient cursorToIngredient(Cursor cursor) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
        ingredient.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_INGREDIENT_NAME)));
        ingredient.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_INGREDIENT_QUANTITY)));
        ingredient.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_INGREDIENT_UNIT)));
        ingredient.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_INGREDIENT_CATEGORY)));
        ingredient.setCriticalThreshold(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_INGREDIENT_CRITICAL_THRESHOLD)));
        return ingredient;
    }

    public int updateIngredient(Ingredient ingredient) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_INGREDIENT_NAME, ingredient.getName());
        values.put(DatabaseHelper.COL_INGREDIENT_QUANTITY, ingredient.getQuantity());
        values.put(DatabaseHelper.COL_INGREDIENT_UNIT, ingredient.getUnit());
        values.put(DatabaseHelper.COL_INGREDIENT_CATEGORY, ingredient.getCategory());
        values.put(DatabaseHelper.COL_INGREDIENT_CRITICAL_THRESHOLD, ingredient.getCriticalThreshold());
        values.put(DatabaseHelper.COL_UPDATED_AT, "datetime('now')");
        
        return db.update(DatabaseHelper.TABLE_INGREDIENTS, values, DatabaseHelper.COL_ID + " = ?", 
                new String[]{String.valueOf(ingredient.getId())});
    }

    public int deleteIngredient(int ingredientId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_INGREDIENTS, DatabaseHelper.COL_ID + " = ?", 
                new String[]{String.valueOf(ingredientId)});
    }

    // Insert dummy data for testing
    public void insertDummyData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        // Check if data already exists
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_INGREDIENTS, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        
        if (count > 0) {
            // Data already exists, don't insert again
            return;
        }
        
        // Insert dummy ingredients
        // Domates - 5.5 kg (Category: Sebze)
        Ingredient domates = new Ingredient("Domates", 5.5, "kg", "Sebze", 10.0);
        addIngredient(domates);
        
        // Dana Kıyma - 12.0 kg (Category: Et)
        Ingredient danaKiyma = new Ingredient("Dana Kıyma", 12.0, "kg", "Et", 8.0);
        addIngredient(danaKiyma);
        
        // Ayçiçek Yağı - 2.0 lt (Category: Bakliyat/Yağ) - CRITICAL LOW
        Ingredient aycicekYagi = new Ingredient("Ayçiçek Yağı", 2.0, "lt", "Yağ", 5.0);
        addIngredient(aycicekYagi);
        
        // Cola 330ml - 150 adet (Category: İçecek)
        Ingredient cola = new Ingredient("Cola 330ml", 150.0, "adet", "İçecek", 50.0);
        addIngredient(cola);
        
        // Un - 50 kg (Category: Kuru Gıda)
        Ingredient un = new Ingredient("Un", 50.0, "kg", "Kuru Gıda", 20.0);
        addIngredient(un);
    }

    public int getLowStockCount() {
        int count = 0;
        List<Ingredient> ingredients = getAllIngredients();
        for (Ingredient ingredient : ingredients) {
            if (ingredient.isLowStock()) {
                count++;
            }
        }
        return count;
    }
}

