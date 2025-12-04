package com.example.restoranaapp;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.restoranaapp.adapter.ProductAdapter;
import com.example.restoranaapp.database.dao.CategoryDao;
import com.example.restoranaapp.database.dao.ProductDao;
import com.example.restoranaapp.model.Category;
import com.example.restoranaapp.model.Product;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MenuManagementActivity extends AppCompatActivity {

    private ProductDao productDao;
    private CategoryDao categoryDao;
    
    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private List<Product> allProducts;
    private List<Category> allCategories;
    
    private ChipGroup chipGroup;
    private EditText etSearch;
    private FloatingActionButton fabAdd;
    private TextView tvEmptyState;

    private int selectedCategoryId = -1;
    
    // Image Handling
    private ImageView dialogImgPreview;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_management);

        productDao = new ProductDao(this);
        categoryDao = new CategoryDao(this);

        setupImagePickers();
        initViews();
        loadCategories();
        loadProducts();
    }

    private void setupImagePickers() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (dialogImgPreview != null) {
                            dialogImgPreview.setImageURI(selectedImageUri);
                        }
                    }
                }
        );

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openGallery();
                    } else {
                        Toast.makeText(this, "Galeri izni gerekli", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void checkPermissionAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                openGallery();
            }
        }
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        rvProducts = findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        
        chipGroup = findViewById(R.id.chipGroupCategories);
        etSearch = findViewById(R.id.etSearch);
        fabAdd = findViewById(R.id.fabAdd);
        tvEmptyState = findViewById(R.id.tvEmptyState);

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

        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
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

        fabAdd.setOnClickListener(v -> showAddOptionsDialog());
    }

    private void loadCategories() {
        allCategories = categoryDao.getAllCategories();
        int count = chipGroup.getChildCount();
        if (count > 1) {
            chipGroup.removeViews(1, count - 1);
        }
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
                chipGroup.addView(chip);
            }
        }
    }

    private void loadProducts() {
        allProducts = productDao.getAllProducts();
        adapter = new ProductAdapter(this, new ArrayList<>(allProducts), new ProductAdapter.OnProductClickListener() {
            @Override
            public void onEditClick(Product product) {
                showAddEditProductDialog(product);
            }

            @Override
            public void onDeleteClick(Product product) {
                if (product.getIsActive() == 1) {
                    product.setIsActive(0);
                } else {
                    product.setIsActive(1);
                }
                productDao.updateProduct(product);
                loadProducts(); 
            }
        });
        rvProducts.setAdapter(adapter);
        filterProducts();
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

        adapter.updateList(filtered);
        tvEmptyState.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showAddOptionsDialog() {
        String[] options = {"Ürün Ekle", "Kategori Ekle"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("İşlem Seçin");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                showAddEditProductDialog(null);
            } else {
                showAddCategoryDialog();
            }
        });
        builder.show();
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Yeni Kategori Ekle");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);
        EditText etName = view.findViewById(R.id.etCategoryName);
        builder.setView(view);

        builder.setPositiveButton("Ekle", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            if (!name.isEmpty()) {
                Category cat = new Category();
                cat.setName(name);
                cat.setIsActive(1);
                categoryDao.addCategory(cat);
                loadCategories();
                Toast.makeText(this, "Kategori eklendi", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("İptal", null);
        builder.show();
    }

    private void showAddEditProductDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(product == null ? "Yeni Ürün Ekle" : "Ürün Düzenle");
        
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null);
        builder.setView(view);
        
        EditText etName = view.findViewById(R.id.etProdName);
        EditText etDesc = view.findViewById(R.id.etProdDesc);
        EditText etPrice = view.findViewById(R.id.etProdPrice);
        Spinner spinnerCat = view.findViewById(R.id.spinnerCategory);
        EditText etImageUrl = view.findViewById(R.id.etImageUrl);
        dialogImgPreview = view.findViewById(R.id.imgProductPreview);
        ImageView btnPaste = view.findViewById(R.id.btnPaste);

        // Setup Spinner
        List<String> catNames = new ArrayList<>();
        List<Integer> catIds = new ArrayList<>();
        for (Category c : allCategories) {
             if (c.getIsActive() == 1) {
                 catNames.add(c.getName());
                 catIds.add(c.getId());
             }
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, catNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCat.setAdapter(spinnerAdapter);

        selectedImageUri = null; // Reset

        if (product != null) {
            etName.setText(product.getName());
            etDesc.setText(product.getDescription());
            etPrice.setText(String.valueOf(product.getPrice()));
            int index = catIds.indexOf(product.getCategoryId());
            if (index != -1) spinnerCat.setSelection(index);
            
            if (product.getImagePath() != null) {
                etImageUrl.setText(product.getImagePath());
                Glide.with(this)
                    .load(product.getImagePath())
                    .placeholder(R.drawable.ic_food_placeholder)
                    .error(R.drawable.ic_food_placeholder)
                    .into(dialogImgPreview);
            }
        }
        
        // Paste Logic
        btnPaste.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null && clipboard.hasPrimaryClip()) {
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                if (item != null && item.getText() != null) {
                    String pasteData = item.getText().toString();
                    etImageUrl.setText(pasteData);
                    // Trigger load
                    if(!pasteData.isEmpty()){
                        Glide.with(this)
                            .load(pasteData)
                            .placeholder(R.drawable.ic_food_placeholder)
                            .error(R.drawable.ic_food_placeholder)
                            .into(dialogImgPreview);
                        Toast.makeText(this, "Yapıştırıldı!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Pano boş veya metin değil", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        etImageUrl.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                String url = etImageUrl.getText().toString().trim();
                if(!url.isEmpty()){
                    Glide.with(this)
                        .load(url)
                        .placeholder(R.drawable.ic_food_placeholder)
                        .error(R.drawable.ic_food_placeholder)
                        .into(dialogImgPreview);
                }
            }
        });
        
        dialogImgPreview.setOnClickListener(v -> checkPermissionAndOpenGallery());

        builder.setPositiveButton("Kaydet", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();
            
            if (name.isEmpty() || priceStr.isEmpty() || catIds.isEmpty()) {
                Toast.makeText(this, "Eksik bilgi", Toast.LENGTH_SHORT).show();
                return;
            }
            
            double price = Double.parseDouble(priceStr);
            int catId = catIds.get(spinnerCat.getSelectedItemPosition());
            
            String finalImagePath = imageUrl;
            if (selectedImageUri != null) {
                finalImagePath = saveImageToInternalStorage(selectedImageUri);
            } else if (finalImagePath.isEmpty() && product != null) {
                finalImagePath = product.getImagePath(); // keep old if nothing new
            }

            if (product == null) {
                Product newP = new Product();
                newP.setName(name);
                newP.setDescription(desc);
                newP.setPrice(price);
                newP.setCategoryId(catId);
                newP.setIsActive(1);
                newP.setImagePath(finalImagePath);
                productDao.addProduct(newP);
                Toast.makeText(this, "Ürün eklendi", Toast.LENGTH_SHORT).show();
            } else {
                product.setName(name);
                product.setDescription(desc);
                product.setPrice(price);
                product.setCategoryId(catId);
                product.setImagePath(finalImagePath);
                productDao.updateProduct(product);
                Toast.makeText(this, "Ürün güncellendi", Toast.LENGTH_SHORT).show();
            }
            loadProducts();
        });
        builder.setNegativeButton("İptal", null);
        builder.show();
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File directory = new File(getFilesDir(), "product_images");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = "img_" + UUID.randomUUID().toString() + ".jpg";
            File file = new File(directory, fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            
            outputStream.close();
            inputStream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
