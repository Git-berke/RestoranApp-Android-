package com.example.restoranaapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restoranaapp.adapter.StockAdapter;
import com.example.restoranaapp.database.dao.IngredientDao;
import com.example.restoranaapp.model.Ingredient;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StockTrackingActivity extends AppCompatActivity implements StockAdapter.OnStockItemClickListener {

    private RecyclerView recyclerViewStock;
    private StockAdapter stockAdapter;
    private List<Ingredient> ingredientList;
    private List<Ingredient> filteredList;
    private IngredientDao ingredientDao;
    
    private TextView tvTotalProducts, tvLowStockCount;
    private EditText etSearch;
    private ImageView btnBack;
    private LinearLayout emptyState;
    private ChipGroup chipGroupCategories;
    private FloatingActionButton fabAddIngredient;
    
    private String currentCategory = "Tümü";
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_tracking);

        initViews();
        setupRecyclerView();
        loadData();
        setupSearchFilter();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvLowStockCount = findViewById(R.id.tvLowStockCount);
        etSearch = findViewById(R.id.etSearch);
        recyclerViewStock = findViewById(R.id.recyclerViewStock);
        emptyState = findViewById(R.id.emptyState);
        chipGroupCategories = findViewById(R.id.chipGroupCategories);
        fabAddIngredient = findViewById(R.id.fabAddIngredient);
        
        btnBack.setOnClickListener(v -> finish());
        fabAddIngredient.setOnClickListener(v -> showAddIngredientDialog());
        setupCategoryChips();
    }

    private void setupRecyclerView() {
        ingredientList = new ArrayList<>();
        filteredList = new ArrayList<>();
        stockAdapter = new StockAdapter(this, filteredList, this);
        
        recyclerViewStock.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewStock.setAdapter(stockAdapter);
    }

    private void loadData() {
        ingredientDao = new IngredientDao(this);
        
        // Insert dummy data on first run
        ingredientDao.insertDummyData();
        
        // Load all ingredients
        ingredientList = ingredientDao.getAllIngredients();
        applyFilters();
        
        updateUI();
    }

    private void setupCategoryChips() {
        chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                return;
            }
            
            int checkedId = checkedIds.get(0);
            Chip chip = findViewById(checkedId);
            if (chip != null) {
                currentCategory = chip.getText().toString();
                applyFilters();
            }
        });
    }

    private void updateUI() {
        stockAdapter.notifyDataSetChanged();
        
        // Update summary cards based on ALL ingredients (not filtered)
        tvTotalProducts.setText(String.valueOf(ingredientList.size()));
        tvLowStockCount.setText(String.valueOf(ingredientDao.getLowStockCount()));
        
        // Show/hide empty state
        if (filteredList.isEmpty()) {
            recyclerViewStock.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerViewStock.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    private void setupSearchFilter() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void applyFilters() {
        filteredList.clear();
        
        List<Ingredient> baseList = ingredientList;
        
        // Filter by category first
        if (!currentCategory.equals("Tümü")) {
            List<Ingredient> categoryFiltered = new ArrayList<>();
            for (Ingredient ingredient : baseList) {
                if (ingredient.getCategory().equals(currentCategory)) {
                    categoryFiltered.add(ingredient);
                }
            }
            baseList = categoryFiltered;
        }
        
        // Then filter by search query
        if (currentSearchQuery.isEmpty()) {
            filteredList.addAll(baseList);
        } else {
            String lowerCaseQuery = currentSearchQuery.toLowerCase(Locale.getDefault());
            for (Ingredient ingredient : baseList) {
                if (ingredient.getName().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)) {
                    filteredList.add(ingredient);
                }
            }
        }
        
        stockAdapter.notifyDataSetChanged();
        updateUI();
    }

    @Override
    public void onEditClick(Ingredient ingredient) {
        showEditStockDialog(ingredient);
    }

    private void showEditStockDialog(Ingredient ingredient) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_stock, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText etStockQuantity = dialogView.findViewById(R.id.etStockQuantity);
        Spinner spinnerUnit = dialogView.findViewById(R.id.spinnerUnit);
        EditText etCriticalLevel = dialogView.findViewById(R.id.etCriticalLevel);

        tvTitle.setText("Stok Düzenle: " + ingredient.getName());
        etStockQuantity.setText(String.valueOf(ingredient.getQuantity()));
        etCriticalLevel.setText(String.valueOf(ingredient.getCriticalThreshold()));

        // Setup unit spinner
        String[] units = {"adet", "kg", "lt", "gr", "ml", "paket"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(adapter);
        
        // Set current unit
        for (int i = 0; i < units.length; i++) {
            if (units[i].equalsIgnoreCase(ingredient.getUnit())) {
                spinnerUnit.setSelection(i);
                break;
            }
        }

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String quantityStr = etStockQuantity.getText().toString().trim();
            String criticalStr = etCriticalLevel.getText().toString().trim();

            if (quantityStr.isEmpty() || criticalStr.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double quantity = Double.parseDouble(quantityStr);
                double criticalLevel = Double.parseDouble(criticalStr);
                String unit = spinnerUnit.getSelectedItem().toString();

                ingredient.setQuantity(quantity);
                ingredient.setUnit(unit);
                ingredient.setCriticalThreshold(criticalLevel);

                ingredientDao.updateIngredient(ingredient);
                loadData();
                
                Toast.makeText(this, "Stok bilgisi güncellendi", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Geçersiz sayı formatı", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete button click
        dialogView.findViewById(R.id.btnDelete).setOnClickListener(v -> {
            dialog.dismiss();
            showDeleteConfirmationDialog(ingredient);
        });

        dialog.show();
    }

    private void showAddIngredientDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_ingredient, null);
        builder.setView(dialogView);

        EditText etIngredientName = dialogView.findViewById(R.id.etIngredientName);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);
        EditText etQuantity = dialogView.findViewById(R.id.etQuantity);
        Spinner spinnerUnit = dialogView.findViewById(R.id.spinnerUnit);
        EditText etCriticalThreshold = dialogView.findViewById(R.id.etCriticalThreshold);

        // Setup category spinner
        String[] categories = {"Et", "Sebze", "İçecek", "Kuru Gıda", "Yağ", "Bakliyat", "Süt Ürünleri"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Setup unit spinner
        String[] units = {"adet", "kg", "lt", "gr", "ml", "paket"};
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String name = etIngredientName.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();
            String quantityStr = etQuantity.getText().toString().trim();
            String unit = spinnerUnit.getSelectedItem().toString();
            String criticalStr = etCriticalThreshold.getText().toString().trim();

            if (name.isEmpty() || quantityStr.isEmpty() || criticalStr.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double quantity = Double.parseDouble(quantityStr);
                double criticalLevel = Double.parseDouble(criticalStr);

                Ingredient newIngredient = new Ingredient(name, quantity, unit, category, criticalLevel);
                long result = ingredientDao.addIngredient(newIngredient);

                if (result > 0) {
                    Toast.makeText(this, "Malzeme başarıyla eklendi", Toast.LENGTH_SHORT).show();
                    loadData();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Ekleme başarısız", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Geçersiz sayı formatı", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showDeleteConfirmationDialog(Ingredient ingredient) {
        new AlertDialog.Builder(this)
            .setTitle("Malzeme Sil")
            .setMessage("'" + ingredient.getName() + "' malzemesini silmek istediğinizden emin misiniz?")
            .setPositiveButton("Sil", (dialog, which) -> {
                int result = ingredientDao.deleteIngredient(ingredient.getId());
                if (result > 0) {
                    Toast.makeText(this, "Malzeme silindi", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(this, "Silme başarısız", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("İptal", null)
            .show();
    }
}

