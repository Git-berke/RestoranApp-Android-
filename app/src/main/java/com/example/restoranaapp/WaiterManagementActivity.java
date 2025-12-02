package com.example.restoranaapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restoranaapp.adapter.WaiterAdapter;
import com.example.restoranaapp.database.dao.UserDao;
import com.example.restoranaapp.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class WaiterManagementActivity extends AppCompatActivity {

    private UserDao userDao;
    private RecyclerView rvWaiters;
    private WaiterAdapter adapter;
    private List<User> waiterList;
    private FloatingActionButton fabAdd;
    private TextView tvEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_management);

        userDao = new UserDao(this);

        initViews();
        loadWaiters();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvWaiters = findViewById(R.id.rvWaiters);
        rvWaiters.setLayoutManager(new LinearLayoutManager(this));
        
        tvEmptyState = findViewById(R.id.tvEmptyState);
        fabAdd = findViewById(R.id.fabAddWaiter);

        fabAdd.setOnClickListener(v -> showAddEditWaiterDialog(null));
    }

    private void loadWaiters() {
        waiterList = userDao.getAllWaiters();
        
        if (adapter == null) {
            adapter = new WaiterAdapter(this, waiterList, new WaiterAdapter.OnWaiterClickListener() {
                @Override
                public void onEditClick(User waiter) {
                    showAddEditWaiterDialog(waiter);
                }
            });
            rvWaiters.setAdapter(adapter);
        } else {
            adapter.updateList(waiterList);
        }
        
        tvEmptyState.setVisibility(waiterList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showAddEditWaiterDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(user == null ? "Yeni Garson Ekle" : "Garson Düzenle");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_waiter, null);
        builder.setView(view);

        EditText etFullName = view.findViewById(R.id.etFullName);
        EditText etUsername = view.findViewById(R.id.etUsername);
        EditText etPassword = view.findViewById(R.id.etPassword);
        EditText etPhone = view.findViewById(R.id.etPhone);
        CheckBox cbActive = view.findViewById(R.id.cbActive);

        if (user != null) {
            etFullName.setText(user.getFullName());
            etUsername.setText(user.getUsername());
            etPassword.setText(user.getPassword());
            etPhone.setText(user.getPhone());
            cbActive.setChecked(user.getIsActive() == 1);
        } else {
            cbActive.setChecked(true); // Default active for new
        }

        builder.setPositiveButton("Kaydet", (dialog, which) -> {
            String fullName = etFullName.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            boolean isActive = cbActive.isChecked();

            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                Toast.makeText(this, "Ad, Kullanıcı Adı ve Şifre zorunludur.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (user == null) {
                // Add new
                User newUser = new User();
                newUser.setFullName(fullName);
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.setPhone(phone);
                newUser.setRole("WAITER");
                newUser.setIsActive(isActive ? 1 : 0);
                
                try {
                    long id = userDao.addUser(newUser);
                    if (id != -1) {
                        Toast.makeText(this, "Garson eklendi", Toast.LENGTH_SHORT).show();
                        loadWaiters();
                    } else {
                        Toast.makeText(this, "Hata oluştu (Kullanıcı adı benzersiz olmalı)", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                // Update
                user.setFullName(fullName);
                user.setUsername(username);
                user.setPassword(password);
                user.setPhone(phone);
                user.setIsActive(isActive ? 1 : 0);
                userDao.updateUser(user);
                Toast.makeText(this, "Bilgiler güncellendi", Toast.LENGTH_SHORT).show();
                loadWaiters();
            }
        });

        builder.setNegativeButton("İptal", null);
        builder.show();
    }
}
