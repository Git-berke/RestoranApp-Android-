package com.example.restoranaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restoranaapp.adapter.CartAdapter;
import com.example.restoranaapp.database.dao.OrderDao;
import com.example.restoranaapp.database.dao.ProductDao;
import com.example.restoranaapp.model.Order;
import com.example.restoranaapp.model.OrderItem;
import com.example.restoranaapp.model.Product;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    private OrderDao orderDao;
    private ProductDao productDao;
    private int orderId;
    private String tableName;
    private Order order;

    private TextView tvTableName, tvStatus, tvTotal, tvTime;
    private RecyclerView rvItems;
    private Button btnComplete, btnUpdate, btnPay;
    private ImageView imgStatusDot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderId = getIntent().getIntExtra("ORDER_ID", -1);
        tableName = getIntent().getStringExtra("TABLE_NAME");

        orderDao = new OrderDao(this);
        productDao = new ProductDao(this);

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDetails();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        tvTableName = findViewById(R.id.tvTableName);
        tvStatus = findViewById(R.id.tvStatus);
        tvTotal = findViewById(R.id.tvTotal);
        tvTime = findViewById(R.id.tvTime);
        rvItems = findViewById(R.id.rvItems);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        
        btnComplete = findViewById(R.id.btnComplete);
        btnPay = findViewById(R.id.btnPay);
        btnUpdate = findViewById(R.id.btnUpdate);
        imgStatusDot = findViewById(R.id.imgStatusDot);
        
        tvTableName.setText("Sipariş Detayı\n" + (tableName != null ? tableName : ""));

        btnUpdate.setOnClickListener(v -> {
            if (order != null) {
                Intent intent = new Intent(OrderDetailActivity.this, OrderActivity.class);
                intent.putExtra("TABLE_ID", order.getTableId());
                intent.putExtra("TABLE_NAME", tableName);
                startActivity(intent);
            }
        });

        btnComplete.setOnClickListener(v -> {
            if (order != null) {
                orderDao.updateOrderStatus(orderId, "SERVED");
                Toast.makeText(this, "Sipariş Servis Edildi", Toast.LENGTH_SHORT).show();
                loadDetails(); 
            }
        });

        btnPay.setOnClickListener(v -> {
            if (order != null) {
                orderDao.payAndCloseOrder(orderId, order.getTableId());
                Toast.makeText(this, "Ödeme Alındı ve Masa Boşaltıldı", Toast.LENGTH_SHORT).show();
                finish(); 
            }
        });
    }

    private void loadDetails() {
        order = orderDao.getOrderById(orderId);
        if (order == null) {
            finish();
            return;
        }

        tvStatus.setText(order.getStatus());
        tvTotal.setText(String.format("₺%.2f", order.getTotalPrice()));

        String timeStr = order.getCreatedAt();
        try {
            SimpleDateFormat dbFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = dbFmt.parse(timeStr);
            SimpleDateFormat displayFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
            timeStr = displayFmt.format(date);
        } catch (ParseException e) { e.printStackTrace(); }
        tvTime.setText(timeStr);

        List<OrderItem> items = orderDao.getOrderItems(orderId);
        List<Product> products = productDao.getAllProducts();
        
        CartAdapter adapter = new CartAdapter(this, items, products);
        rvItems.setAdapter(adapter);
        
        // Button Visibility Logic
        if ("SERVED".equals(order.getStatus())) {
            btnComplete.setVisibility(View.GONE); // Already served
        } else {
            btnComplete.setVisibility(View.VISIBLE);
        }
    }
}

