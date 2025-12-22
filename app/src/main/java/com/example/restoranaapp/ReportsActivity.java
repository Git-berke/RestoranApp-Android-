package com.example.restoranaapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restoranaapp.adapter.SalesReportAdapter;
import com.example.restoranaapp.database.dao.OrderDao;
import com.example.restoranaapp.model.ProductSalesReport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReportsActivity extends AppCompatActivity {

    private OrderDao orderDao;
    private TextView tvRevenue, tvTotalItems, tvTopProduct, tvCurrentDate;
    private Button btnDaily, btnWeekly, btnMonthly;
    private ImageButton btnPreviousDate, btnNextDate;
    private RecyclerView rvSales;
    private SalesReportAdapter adapter;

    private int selectedTab = 0; // 0=Daily, 1=Weekly, 2=Monthly
    private Calendar currentCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        orderDao = new OrderDao(this);
        currentCalendar = Calendar.getInstance();

        initViews();
        updateData();
        updateDateDisplay();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        tvRevenue = findViewById(R.id.tvRevenue);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvTopProduct = findViewById(R.id.tvTopProduct);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        
        btnDaily = findViewById(R.id.btnDaily);
        btnWeekly = findViewById(R.id.btnWeekly);
        btnMonthly = findViewById(R.id.btnMonthly);
        btnPreviousDate = findViewById(R.id.btnPreviousDate);
        btnNextDate = findViewById(R.id.btnNextDate);

        rvSales = findViewById(R.id.rvSales);
        rvSales.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SalesReportAdapter(this, new ArrayList<>());
        rvSales.setAdapter(adapter);

        btnDaily.setOnClickListener(v -> {
            setTab(0);
            updateData();
        });
        btnWeekly.setOnClickListener(v -> {
            setTab(1);
            updateData();
        });
        btnMonthly.setOnClickListener(v -> {
            setTab(2);
            updateData();
        });
        
        btnPreviousDate.setOnClickListener(v -> {
            navigateDate(-1);
        });
        
        btnNextDate.setOnClickListener(v -> {
            navigateDate(1);
        });
    }

    private void setTab(int tabIndex) {
        selectedTab = tabIndex;
        
        // Use hardcoded color or standard resource if design lib color is missing
        int colorActive = Color.parseColor("#1976D2"); // Standard blue equivalent
        int colorInactive = Color.parseColor("#F1F3F4");
        int textActive = Color.WHITE;
        int textInactive = Color.BLACK;
        
        // Manual toggle style update
        btnDaily.setBackgroundTintList(ColorStateList.valueOf(tabIndex == 0 ? colorActive : colorInactive));
        btnDaily.setTextColor(tabIndex == 0 ? textActive : textInactive);
        
        btnWeekly.setBackgroundTintList(ColorStateList.valueOf(tabIndex == 1 ? colorActive : colorInactive));
        btnWeekly.setTextColor(tabIndex == 1 ? textActive : textInactive);
        
        btnMonthly.setBackgroundTintList(ColorStateList.valueOf(tabIndex == 2 ? colorActive : colorInactive));
        btnMonthly.setTextColor(tabIndex == 2 ? textActive : textInactive);
    }

    private void updateData() {
        String[] range = getDateRange(selectedTab);
        String start = range[0];
        String end = range[1];
        
        // Update cards
        double revenue = orderDao.getTotalRevenue(start, end);
        int totalItems = orderDao.getTotalItemsSold(start, end);
        String topProduct = orderDao.getTopSellingProduct(start, end);

        tvRevenue.setText(String.format("₺%.2f", revenue));
        tvTotalItems.setText(String.valueOf(totalItems));
        tvTopProduct.setText(topProduct);

        // Update list
        List<ProductSalesReport> list = orderDao.getProductSalesReports(start, end);
        adapter.updateList(list);
    }

    private String[] getDateRange(int tabIndex) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        
        // End date is always "now"
        String end = sdf.format(cal.getTime());
        
        // Calculate start
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        
        if (tabIndex == 0) {
            // Daily: Start of today
            // Already set to 00:00 today above
        } else if (tabIndex == 1) {
            // Weekly: Start of week (Monday or Sunday depending on locale, let's say 7 days ago)
            cal.add(Calendar.DAY_OF_YEAR, -7);
        } else if (tabIndex == 2) {
            // Monthly: Start of month or 30 days ago
            cal.add(Calendar.MONTH, -1); 
            // or cal.set(Calendar.DAY_OF_MONTH, 1) for 1st of this month
            // Requirement "Aylık" usually means this month. 
            // Let's use "Last 30 days" logic or "Start of Month"?
            // Usually filters are "Today", "This Week", "This Month".
            // Let's reset to 1st of month if we want "This Month"
            // cal.set(Calendar.DAY_OF_MONTH, 1);
        }
        
        String start = sdf.format(cal.getTime());
        return new String[]{start, end};
    }
    
    private void navigateDate(int direction) {
        // Navigate by day, week, or month based on selected tab
        if (selectedTab == 0) {
            currentCalendar.add(Calendar.DAY_OF_YEAR, direction);
        } else if (selectedTab == 1) {
            currentCalendar.add(Calendar.WEEK_OF_YEAR, direction);
        } else {
            currentCalendar.add(Calendar.MONTH, direction);
        }
        
        updateDateDisplay();
        updateData();
    }
    
    private void updateDateDisplay() {
        SimpleDateFormat sdf;
        if (selectedTab == 0) {
            sdf = new SimpleDateFormat("dd MMM yyyy", new Locale("tr"));
        } else if (selectedTab == 1) {
            sdf = new SimpleDateFormat("'Hafta' w, yyyy", new Locale("tr"));
        } else {
            sdf = new SimpleDateFormat("MMMM yyyy", new Locale("tr"));
        }
        
        tvCurrentDate.setText(sdf.format(currentCalendar.getTime()));
    }
}
