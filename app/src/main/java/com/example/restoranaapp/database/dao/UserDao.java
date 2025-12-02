package com.example.restoranaapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.restoranaapp.database.DatabaseHelper;
import com.example.restoranaapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private DatabaseHelper dbHelper;

    public UserDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public User login(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;

        String selection = DatabaseHelper.COL_USERNAME + " = ? AND " + 
                           DatabaseHelper.COL_PASSWORD + " = ? AND " + 
                           DatabaseHelper.COL_IS_ACTIVE + " = 1";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        return user;
    }
    
    public long addUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USERNAME, user.getUsername());
        values.put(DatabaseHelper.COL_PASSWORD, user.getPassword());
        values.put(DatabaseHelper.COL_ROLE, user.getRole());
        values.put(DatabaseHelper.COL_FULL_NAME, user.getFullName());
        values.put(DatabaseHelper.COL_PHONE, user.getPhone());
        values.put(DatabaseHelper.COL_IS_ACTIVE, user.getIsActive());
        values.put(DatabaseHelper.COL_CREATED_AT, user.getCreatedAt());
        
        return db.insert(DatabaseHelper.TABLE_USERS, null, values);
    }
    
    public List<User> getAllWaiters() {
        List<User> waiters = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String selection = DatabaseHelper.COL_ROLE + " = ?";
        String[] selectionArgs = {"WAITER"};
        
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, null, selection, selectionArgs, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                waiters.add(cursorToUser(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return waiters;
    }
    
    public int updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USERNAME, user.getUsername());
        values.put(DatabaseHelper.COL_PASSWORD, user.getPassword());
        values.put(DatabaseHelper.COL_FULL_NAME, user.getFullName());
        values.put(DatabaseHelper.COL_PHONE, user.getPhone());
        values.put(DatabaseHelper.COL_IS_ACTIVE, user.getIsActive());
        
        return db.update(DatabaseHelper.TABLE_USERS, values, DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(user.getId())});
    }
    
    public int deleteUser(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_IS_ACTIVE, 0);
        return db.update(DatabaseHelper.TABLE_USERS, values, DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USERNAME)));
        user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PASSWORD)));
        user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ROLE)));
        user.setIsActive(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_IS_ACTIVE)));
        
        // Handle nullable fields gracefully
        int idxName = cursor.getColumnIndex(DatabaseHelper.COL_FULL_NAME);
        if (idxName != -1) user.setFullName(cursor.getString(idxName));
        
        int idxPhone = cursor.getColumnIndex(DatabaseHelper.COL_PHONE);
        if (idxPhone != -1) user.setPhone(cursor.getString(idxPhone));

        int idxCreated = cursor.getColumnIndex(DatabaseHelper.COL_CREATED_AT);
        if (idxCreated != -1) user.setCreatedAt(cursor.getString(idxCreated));
        
        return user;
    }
}
