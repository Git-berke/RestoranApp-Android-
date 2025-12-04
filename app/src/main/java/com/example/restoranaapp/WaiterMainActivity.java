package com.example.restoranaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restoranaapp.adapter.WaiterTableAdapter;
import com.example.restoranaapp.database.dao.TableDao;
import com.example.restoranaapp.model.RestaurantTable;

import java.util.ArrayList;
import java.util.List;

public class WaiterMainActivity extends AppCompatActivity {

    private TableDao tableDao;
    private RecyclerView rvTables;
    private WaiterTableAdapter adapter;
    private TextView tvWaiterInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_main);

        tableDao = new TableDao(this);

        rvTables = findViewById(R.id.rvTables);
        tvWaiterInfo = findViewById(R.id.tvWaiterInfo);
        
        // Grid Layout for tables - 2 columns for mobile
        rvTables.setLayoutManager(new GridLayoutManager(this, 2));

        findViewById(R.id.btnLogout).setOnClickListener(v -> logout());
        findViewById(R.id.btnActiveOrders).setOnClickListener(v -> {
            startActivity(new Intent(WaiterMainActivity.this, ActiveOrdersActivity.class));
        });

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = prefs.getString("username", "Garson");
        tvWaiterInfo.setText("Garson: " + username);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTables();
    }

    private void loadTables() {
        List<RestaurantTable> allTables = tableDao.getAllTables();
        List<RestaurantTable> activeTables = new ArrayList<>();
        
        for(RestaurantTable t : allTables) {
            if(t.getIsActive() == 1) {
                activeTables.add(t);
            }
        }

        if (adapter == null) {
            adapter = new WaiterTableAdapter(this, activeTables, this::openOrderScreen);
            rvTables.setAdapter(adapter);
        } else {
            adapter.updateList(activeTables);
        }
    }

    private void openOrderScreen(RestaurantTable table) {
        Intent intent = new Intent(WaiterMainActivity.this, OrderActivity.class);
        intent.putExtra("TABLE_ID", table.getId());
        intent.putExtra("TABLE_NAME", table.getTableName() != null && !table.getTableName().isEmpty() ? table.getTableName() : "Masa " + table.getTableNumber());
        startActivity(intent);
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(WaiterMainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

