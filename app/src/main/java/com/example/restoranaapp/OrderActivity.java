package com.example.restoranaapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restoranaapp.adapter.CartAdapter;
import com.example.restoranaapp.adapter.OrderProductAdapter;
import com.example.restoranaapp.database.dao.CategoryDao;
import com.example.restoranaapp.database.dao.OrderDao;
import com.example.restoranaapp.database.dao.ProductDao;
import com.example.restoranaapp.model.Category;
import com.example.restoranaapp.model.Order;
import com.example.restoranaapp.model.OrderItem;
import com.example.restoranaapp.model.Product;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    private int tableId;
    private String tableName;
    
    private ProductDao productDao;
    private CategoryDao categoryDao;
    private OrderDao orderDao;

    private RecyclerView rvProducts;
    private OrderProductAdapter productAdapter;
    private ChipGroup chipGroupCategories;
    private EditText etSearch;
    private TextView tvTotalTop, tvCartTotal, tvTableName;
    private View bottomCartBar;

    private List<Product> allProducts;
    private List<Category> allCategories;
    
    private Map<Integer, Integer> cartMap = new HashMap<>(); 
    private Order currentOrder; 

    private int selectedCategoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        tableId = getIntent().getIntExtra("TABLE_ID", -1);
        tableName = getIntent().getStringExtra("TABLE_NAME");

        if (tableId == -1) {
            finish();
            return;
        }

        productDao = new ProductDao(this);
        categoryDao = new CategoryDao(this);
        orderDao = new OrderDao(this);

        initViews();
        loadData();
        checkForExistingOrder();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        tvTableName = findViewById(R.id.tvTableName);
        tvTableName.setText(tableName);
        
        tvTotalTop = findViewById(R.id.tvTotalTop);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        
        rvProducts = findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        
        chipGroupCategories = findViewById(R.id.chipGroupCategories);
        etSearch = findViewById(R.id.etSearch);
        bottomCartBar = findViewById(R.id.bottomCartBar);

        bottomCartBar.setOnClickListener(v -> showCartBottomSheet());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        chipGroupCategories.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == View.NO_ID) {
                selectedCategoryId = -1;
            } else {
                Chip chip = findViewById(checkedId);
                Object tag = chip.getTag();
                if (tag != null) {
                    selectedCategoryId = (int) tag;
                } else {
                    selectedCategoryId = -1;
                }
            }
            filterProducts();
        });
    }

    private void loadData() {
        allCategories = categoryDao.getAllCategories();
        allProducts = productDao.getAllProducts();

        List<Product> activeProducts = new ArrayList<>();
        for(Product p : allProducts) {
            if(p.getIsActive() == 1) activeProducts.add(p);
        }
        allProducts = activeProducts;

        Chip allChip = findViewById(R.id.chipAll);
        allChip.setTag(-1);

        for (Category cat : allCategories) {
            if (cat.getIsActive() == 1) {
                Chip chip = new Chip(this);
                chip.setText(cat.getName());
                chip.setCheckable(true);
                chip.setClickable(true);
                chip.setTag(cat.getId());
                chip.setChipBackgroundColor(allChip.getChipBackgroundColor());
                chip.setTextColor(allChip.getTextColors());
                chipGroupCategories.addView(chip);
            }
        }

        productAdapter = new OrderProductAdapter(this, allProducts, cartMap, (product, newQuantity) -> {
            if (newQuantity <= 0) {
                cartMap.remove(product.getId());
            } else {
                cartMap.put(product.getId(), newQuantity);
            }
            productAdapter.updateCart(cartMap);
            updateTotal();
        });
        rvProducts.setAdapter(productAdapter);
    }

    private void checkForExistingOrder() {
        List<Order> orders = orderDao.getActiveOrdersByTable(tableId);
        if (!orders.isEmpty()) {
            currentOrder = orders.get(0);
            List<OrderItem> items = orderDao.getOrderItems(currentOrder.getId());
            for (OrderItem item : items) {
                cartMap.put(item.getProductId(), item.getQuantity());
            }
            productAdapter.updateCart(cartMap);
            updateTotal();
        }
    }

    private void filterProducts() {
        String query = etSearch.getText().toString().toLowerCase();
        List<Product> filtered = new ArrayList<>();

        for (Product p : allProducts) {
            boolean catMatch = (selectedCategoryId == -1) || (p.getCategoryId() == selectedCategoryId);
            boolean searchMatch = p.getName().toLowerCase().contains(query);

            if (catMatch && searchMatch) {
                filtered.add(p);
            }
        }
        productAdapter.updateList(filtered);
    }

    private void updateTotal() {
        double total = 0;
        for (Map.Entry<Integer, Integer> entry : cartMap.entrySet()) {
            int pId = entry.getKey();
            int qty = entry.getValue();
            for(Product p : allProducts) {
                if(p.getId() == pId) {
                    total += (p.getPrice() * qty);
                    break;
                }
            }
        }
        String totalStr = String.format("₺%.2f", total);
        tvTotalTop.setText(totalStr);
        tvCartTotal.setText(totalStr);
    }

    private void showCartBottomSheet() {
        if (cartMap.isEmpty() && currentOrder == null) {
            Toast.makeText(this, "Sepet boş", Toast.LENGTH_SHORT).show();
            return;
        }

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_cart, null);
        
        RecyclerView rvCartItems = view.findViewById(R.id.rvCartItems);
        TextView tvSheetTotal = view.findViewById(R.id.tvSheetTotal);
        View btnConfirm = view.findViewById(R.id.btnConfirmOrder);
        View btnCancel = view.findViewById(R.id.btnCancelSheet);

        tvSheetTotal.setText(tvCartTotal.getText());

        List<OrderItem> items = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : cartMap.entrySet()) {
            int pId = entry.getKey();
            int qty = entry.getValue();
            double price = 0;
            for(Product p : allProducts) {
                if(p.getId() == pId) {
                    price = p.getPrice();
                    break;
                }
            }
            OrderItem item = new OrderItem();
            item.setProductId(pId);
            item.setQuantity(qty);
            item.setUnitPrice(price);
            item.setLineTotal(price * qty);
            items.add(item);
        }

        CartAdapter adapter = new CartAdapter(this, items, allProducts);
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(adapter);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        btnConfirm.setOnClickListener(v -> {
            saveOrder(items);
            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void saveOrder(List<OrderItem> items) {
        double total = 0;
        for(OrderItem i : items) total += i.getLineTotal();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        if (currentOrder == null) {
            Order order = new Order();
            order.setTableId(tableId);
            SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
            int waiterId = prefs.getInt("userId", 0);
            order.setWaiterId(waiterId);
            order.setStatus("PENDING");
            order.setTotalPrice(total);
            order.setCreatedAt(now);
            
            long id = orderDao.createOrder(order, items);
            if (id != -1) {
                Toast.makeText(this, "Sipariş Oluşturuldu", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Hata oluştu", Toast.LENGTH_SHORT).show();
            }
        } else {
            currentOrder.setTotalPrice(total);
            currentOrder.setUpdatedAt(now);
            orderDao.updateOrder(currentOrder, items);
            Toast.makeText(this, "Sipariş Güncellendi", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}

