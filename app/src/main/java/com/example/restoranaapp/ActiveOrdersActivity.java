package com.example.restoranaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restoranaapp.adapter.ActiveOrdersAdapter;
import com.example.restoranaapp.database.dao.OrderDao;
import com.example.restoranaapp.model.ActiveOrderDto;

import java.util.List;

public class ActiveOrdersActivity extends AppCompatActivity {

    private OrderDao orderDao;
    private RecyclerView rvActiveOrders;
    private ActiveOrdersAdapter adapter;
    private LinearLayout emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_orders);

        orderDao = new OrderDao(this);
        
        rvActiveOrders = findViewById(R.id.rvActiveOrders);
        rvActiveOrders.setLayoutManager(new LinearLayoutManager(this));
        emptyState = findViewById(R.id.emptyState);
        
        findViewById(R.id.fabNewOrder).setOnClickListener(v -> {
            // Go to WaiterMainActivity (Table Selection) to start new order
            Intent intent = new Intent(ActiveOrdersActivity.this, WaiterMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Simple navigation
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        List<ActiveOrderDto> orders = orderDao.getAllActiveOrders();
        
        if (orders.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            rvActiveOrders.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            rvActiveOrders.setVisibility(View.VISIBLE);
            
            if (adapter == null) {
                adapter = new ActiveOrdersAdapter(this, orders, this::openOrderDetail);
                rvActiveOrders.setAdapter(adapter);
            } else {
                adapter.updateList(orders);
            }
        }
    }

    private void openOrderDetail(ActiveOrderDto order) {
        Intent intent = new Intent(ActiveOrdersActivity.this, OrderDetailActivity.class);
        intent.putExtra("ORDER_ID", order.getOrderId());
        intent.putExtra("TABLE_NAME", order.getTableName());
        startActivity(intent);
    }
}

