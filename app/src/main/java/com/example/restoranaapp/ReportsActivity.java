package com.example.restoranaapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restoranaapp.adapter.SalesReportAdapter;
import com.example.restoranaapp.database.dao.OrderDao;
import com.example.restoranaapp.model.ProductSalesReport;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportsActivity extends AppCompatActivity {

    private OrderDao orderDao;
    private TextView tvRevenue, tvTotalItems, tvTopProduct, tvCurrentDate;
    private Button btnDaily, btnWeekly, btnMonthly;
    private ImageButton btnPreviousDate, btnNextDate;
    private RecyclerView rvSales;
    private SalesReportAdapter adapter;
    
    // Charts
    private LineChart lineChartRevenue;
    private PieChart pieChartCategory;

    private int selectedTab = 0; // 0=Daily, 1=Weekly, 2=Monthly
    private Calendar currentCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        orderDao = new OrderDao(this);
        currentCalendar = Calendar.getInstance();

        initViews();
        initCharts();
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
        
        // Initialize charts
        lineChartRevenue = findViewById(R.id.lineChartRevenue);
        pieChartCategory = findViewById(R.id.pieChartCategory);

        rvSales = findViewById(R.id.rvSales);
        rvSales.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SalesReportAdapter(this, new ArrayList<>());
        rvSales.setAdapter(adapter);

        btnDaily.setOnClickListener(v -> {
            setTab(0);
            updateData();
            generateDummyChartData();
        });
        btnWeekly.setOnClickListener(v -> {
            setTab(1);
            updateData();
            generateDummyChartData();
        });
        btnMonthly.setOnClickListener(v -> {
            setTab(2);
            updateData();
            generateDummyChartData();
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
    
    private void initCharts() {
        // Configure LineChart
        lineChartRevenue.getDescription().setEnabled(false);
        lineChartRevenue.setTouchEnabled(true);
        lineChartRevenue.setDragEnabled(true);
        lineChartRevenue.setScaleEnabled(false);
        lineChartRevenue.setPinchZoom(false);
        lineChartRevenue.setDrawGridBackground(false);
        lineChartRevenue.getXAxis().setDrawGridLines(false);
        lineChartRevenue.getAxisLeft().setDrawGridLines(true);
        lineChartRevenue.getAxisRight().setEnabled(false);
        lineChartRevenue.getLegend().setEnabled(false);
        lineChartRevenue.animateY(1000);
        
        // Configure PieChart
        pieChartCategory.getDescription().setEnabled(false);
        pieChartCategory.setRotationEnabled(true);
        pieChartCategory.setHighlightPerTapEnabled(true);
        pieChartCategory.setEntryLabelColor(Color.BLACK);
        pieChartCategory.setEntryLabelTextSize(12f);
        pieChartCategory.animateY(1000);
        pieChartCategory.getLegend().setEnabled(true);
        
        // Generate initial dummy data
        generateDummyChartData();
    }
    
    private void generateDummyChartData() {
        // LineChart - Revenue Trend (Dummy Data)
        ArrayList<Entry> lineEntries = new ArrayList<>();
        
        if (selectedTab == 0) {
            // Daily: Hourly data (24 hours)
            lineEntries.add(new Entry(9f, 200f));
            lineEntries.add(new Entry(10f, 350f));
            lineEntries.add(new Entry(11f, 480f));
            lineEntries.add(new Entry(12f, 950f));
            lineEntries.add(new Entry(13f, 1200f));
            lineEntries.add(new Entry(14f, 850f));
            lineEntries.add(new Entry(15f, 600f));
            lineEntries.add(new Entry(16f, 400f));
            lineEntries.add(new Entry(17f, 550f));
            lineEntries.add(new Entry(18f, 800f));
            lineEntries.add(new Entry(19f, 1350f));
            lineEntries.add(new Entry(20f, 1500f));
            lineEntries.add(new Entry(21f, 1100f));
            lineEntries.add(new Entry(22f, 750f));
        } else if (selectedTab == 1) {
            // Weekly: Daily data (7 days)
            lineEntries.add(new Entry(1f, 3200f));
            lineEntries.add(new Entry(2f, 4100f));
            lineEntries.add(new Entry(3f, 3800f));
            lineEntries.add(new Entry(4f, 5200f));
            lineEntries.add(new Entry(5f, 6800f));
            lineEntries.add(new Entry(6f, 7500f));
            lineEntries.add(new Entry(7f, 6200f));
        } else {
            // Monthly: Weekly data (4 weeks)
            lineEntries.add(new Entry(1f, 18500f));
            lineEntries.add(new Entry(2f, 22300f));
            lineEntries.add(new Entry(3f, 25800f));
            lineEntries.add(new Entry(4f, 28900f));
        }
        
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Ciro (₺)");
        lineDataSet.setColor(Color.parseColor("#ec7813")); // Primary orange
        lineDataSet.setCircleColor(Color.parseColor("#ec7813"));
        lineDataSet.setLineWidth(3f);
        lineDataSet.setCircleRadius(5f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.parseColor("#ec781333")); // Light orange fill
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        
        LineData lineData = new LineData(lineDataSet);
        lineChartRevenue.setData(lineData);
        lineChartRevenue.invalidate();
        
        // PieChart - Category Distribution (Dummy Data)
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(40f, "Yemek"));
        pieEntries.add(new PieEntry(30f, "İçecek"));
        pieEntries.add(new PieEntry(15f, "Tatlı"));
        pieEntries.add(new PieEntry(10f, "Aperatif"));
        pieEntries.add(new PieEntry(5f, "Diğer"));
        
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Kategoriler");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(14f);
        pieDataSet.setSliceSpace(3f);
        
        PieData pieData = new PieData(pieDataSet);
        pieChartCategory.setData(pieData);
        pieChartCategory.invalidate();
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
        generateDummyChartData();
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
