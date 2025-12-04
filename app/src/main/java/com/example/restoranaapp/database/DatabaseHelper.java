package com.example.restoranaapp.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RestoranaApp.db";
    private static final int DATABASE_VERSION = 4;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_PRODUCTS = "products";
    public static final String TABLE_TABLES = "restaurant_tables";
    public static final String TABLE_ORDERS = "orders";
    public static final String TABLE_ORDER_ITEMS = "order_items";
    public static final String TABLE_INGREDIENTS = "ingredients";

    // Common Columns
    public static final String COL_ID = "id";
    public static final String COL_CREATED_AT = "created_at";
    public static final String COL_UPDATED_AT = "updated_at";
    public static final String COL_IS_ACTIVE = "is_active";

    // Users Table Columns
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_ROLE = "role";
    public static final String COL_FULL_NAME = "full_name";
    public static final String COL_PHONE = "phone";

    // Categories Table Columns
    public static final String COL_CATEGORY_NAME = "name";

    // Products Table Columns
    public static final String COL_PRODUCT_NAME = "name";
    public static final String COL_PRODUCT_DESCRIPTION = "description";
    public static final String COL_PRODUCT_PRICE = "price";
    public static final String COL_PRODUCT_CATEGORY_ID = "category_id";
    public static final String COL_PRODUCT_IMAGE_PATH = "image_path";
    public static final String COL_PRODUCT_STOCK_QUANTITY = "stock_quantity";
    public static final String COL_PRODUCT_STOCK_UNIT = "stock_unit";
    public static final String COL_PRODUCT_CRITICAL_LEVEL = "critical_level";

    // Tables Table Columns
    public static final String COL_TABLE_NUMBER = "table_number";
    public static final String COL_TABLE_NAME = "table_name";
    public static final String COL_TABLE_STATUS = "status";

    // Orders Table Columns
    public static final String COL_ORDER_TABLE_ID = "table_id";
    public static final String COL_ORDER_WAITER_ID = "waiter_id";
    public static final String COL_ORDER_STATUS = "status";
    public static final String COL_ORDER_TOTAL_PRICE = "total_price";

    // Order Items Table Columns
    public static final String COL_ITEM_ORDER_ID = "order_id";
    public static final String COL_ITEM_PRODUCT_ID = "product_id";
    public static final String COL_ITEM_QUANTITY = "quantity";
    public static final String COL_ITEM_UNIT_PRICE = "unit_price";
    public static final String COL_ITEM_LINE_TOTAL = "line_total";

    // Ingredients Table Columns
    public static final String COL_INGREDIENT_NAME = "name";
    public static final String COL_INGREDIENT_QUANTITY = "quantity";
    public static final String COL_INGREDIENT_UNIT = "unit";
    public static final String COL_INGREDIENT_CATEGORY = "category";
    public static final String COL_INGREDIENT_CRITICAL_THRESHOLD = "critical_threshold";

    // Create Table Statements

    // 7.1 users (Updated definition for fresh install)
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_USERNAME + " TEXT UNIQUE NOT NULL, "
            + COL_PASSWORD + " TEXT NOT NULL, "
            + COL_FULL_NAME + " TEXT, "
            + COL_PHONE + " TEXT, "
            + COL_ROLE + " TEXT NOT NULL, "
            + COL_IS_ACTIVE + " INTEGER NOT NULL DEFAULT 1, "
            + COL_CREATED_AT + " TEXT, "
            + COL_UPDATED_AT + " TEXT"
            + ")";

    // 7.2 categories
    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_CATEGORY_NAME + " TEXT UNIQUE NOT NULL, "
            + COL_IS_ACTIVE + " INTEGER NOT NULL DEFAULT 1, "
            + COL_CREATED_AT + " TEXT, "
            + COL_UPDATED_AT + " TEXT"
            + ")";

    // 7.3 products
    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_PRODUCT_NAME + " TEXT NOT NULL, "
            + COL_PRODUCT_DESCRIPTION + " TEXT, "
            + COL_PRODUCT_PRICE + " REAL NOT NULL, "
            + COL_PRODUCT_CATEGORY_ID + " INTEGER NOT NULL, "
            + COL_PRODUCT_IMAGE_PATH + " TEXT, "
            + COL_PRODUCT_STOCK_QUANTITY + " REAL DEFAULT 0, "
            + COL_PRODUCT_STOCK_UNIT + " TEXT DEFAULT 'Adet', "
            + COL_PRODUCT_CRITICAL_LEVEL + " REAL DEFAULT 10, "
            + COL_IS_ACTIVE + " INTEGER NOT NULL DEFAULT 1, "
            + COL_CREATED_AT + " TEXT, "
            + COL_UPDATED_AT + " TEXT, "
            + "FOREIGN KEY(" + COL_PRODUCT_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COL_ID + ")"
            + ")";

    // 7.4 restaurant_tables
    private static final String CREATE_TABLE_TABLES = "CREATE TABLE " + TABLE_TABLES + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TABLE_NUMBER + " INTEGER UNIQUE NOT NULL, "
            + COL_TABLE_NAME + " TEXT, "
            + COL_TABLE_STATUS + " TEXT NOT NULL, " // EMPTY, ACTIVE
            + COL_IS_ACTIVE + " INTEGER NOT NULL DEFAULT 1, "
            + COL_CREATED_AT + " TEXT, "
            + COL_UPDATED_AT + " TEXT"
            + ")";

    // 7.5 orders
    private static final String CREATE_TABLE_ORDERS = "CREATE TABLE " + TABLE_ORDERS + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_ORDER_TABLE_ID + " INTEGER NOT NULL, "
            + COL_ORDER_WAITER_ID + " INTEGER NOT NULL, "
            + COL_ORDER_STATUS + " TEXT NOT NULL, " // PENDING, IN_PROGRESS, SERVED, CANCELLED, PAID
            + COL_ORDER_TOTAL_PRICE + " REAL NOT NULL DEFAULT 0, "
            + COL_CREATED_AT + " TEXT, "
            + COL_UPDATED_AT + " TEXT, "
            + "FOREIGN KEY(" + COL_ORDER_TABLE_ID + ") REFERENCES " + TABLE_TABLES + "(" + COL_ID + "), "
            + "FOREIGN KEY(" + COL_ORDER_WAITER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + ")"
            + ")";

    // 7.6 order_items
    private static final String CREATE_TABLE_ORDER_ITEMS = "CREATE TABLE " + TABLE_ORDER_ITEMS + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_ITEM_ORDER_ID + " INTEGER NOT NULL, "
            + COL_ITEM_PRODUCT_ID + " INTEGER NOT NULL, "
            + COL_ITEM_QUANTITY + " INTEGER NOT NULL, "
            + COL_ITEM_UNIT_PRICE + " REAL NOT NULL, "
            + COL_ITEM_LINE_TOTAL + " REAL NOT NULL, "
            + COL_CREATED_AT + " TEXT, "
            + COL_UPDATED_AT + " TEXT, "
            + "FOREIGN KEY(" + COL_ITEM_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COL_ID + "), "
            + "FOREIGN KEY(" + COL_ITEM_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COL_ID + ")"
            + ")";

    // 7.7 ingredients
    private static final String CREATE_TABLE_INGREDIENTS = "CREATE TABLE " + TABLE_INGREDIENTS + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_INGREDIENT_NAME + " TEXT NOT NULL, "
            + COL_INGREDIENT_QUANTITY + " REAL NOT NULL DEFAULT 0, "
            + COL_INGREDIENT_UNIT + " TEXT NOT NULL, "
            + COL_INGREDIENT_CATEGORY + " TEXT NOT NULL, "
            + COL_INGREDIENT_CRITICAL_THRESHOLD + " REAL NOT NULL DEFAULT 10, "
            + COL_CREATED_AT + " TEXT, "
            + COL_UPDATED_AT + " TEXT"
            + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_PRODUCTS);
        db.execSQL(CREATE_TABLE_TABLES);
        db.execSQL(CREATE_TABLE_ORDERS);
        db.execSQL(CREATE_TABLE_ORDER_ITEMS);
        db.execSQL(CREATE_TABLE_INGREDIENTS);

        String adminSql = "INSERT INTO " + TABLE_USERS + " (" 
                + COL_USERNAME + ", " + COL_PASSWORD + ", " + COL_ROLE + ", " + COL_IS_ACTIVE + ", " + COL_CREATED_AT + ") VALUES "
                + "('admin', 'admin123', 'ADMIN', 1, datetime('now'))";
        db.execSQL(adminSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Migration from V1 to V2: Add columns to users table without losing data
            // Check if column exists first to be safe (though version check handles it mostly)
            try {
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COL_FULL_NAME + " TEXT");
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COL_PHONE + " TEXT");
            } catch (Exception e) {
                // Columns might already exist or other error, log it but don't crash
            }
        }
        
        if (oldVersion < 3) {
            // Migration from V2 to V3: Add inventory columns to products table
            try {
                db.execSQL("ALTER TABLE " + TABLE_PRODUCTS + " ADD COLUMN " + COL_PRODUCT_STOCK_QUANTITY + " REAL DEFAULT 0");
                db.execSQL("ALTER TABLE " + TABLE_PRODUCTS + " ADD COLUMN " + COL_PRODUCT_STOCK_UNIT + " TEXT DEFAULT 'Adet'");
                db.execSQL("ALTER TABLE " + TABLE_PRODUCTS + " ADD COLUMN " + COL_PRODUCT_CRITICAL_LEVEL + " REAL DEFAULT 10");
            } catch (Exception e) {
                // Columns might already exist or other error, log it but don't crash
            }
        }
        
        if (oldVersion < 4) {
            // Migration from V3 to V4: Create ingredients table
            try {
                db.execSQL(CREATE_TABLE_INGREDIENTS);
            } catch (Exception e) {
                // Table might already exist or other error, log it but don't crash
            }
        }
        
        // For future versions, add else if (oldVersion < 5) ...
    }
    
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
