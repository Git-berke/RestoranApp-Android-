package com.example.restoranaapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restoranaapp.adapter.TableAdapter;
import com.example.restoranaapp.database.dao.TableDao;
import com.example.restoranaapp.model.RestaurantTable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TableManagementActivity extends AppCompatActivity {

    private TableDao tableDao;
    private RecyclerView rvTables;
    private TableAdapter adapter;
    private List<RestaurantTable> tableList;
    private FloatingActionButton fabAdd;
    private TextView tvEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_management);

        tableDao = new TableDao(this);

        initViews();
        loadTables();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvTables = findViewById(R.id.rvTables);
        // Use GridLayoutManager with 2 columns for mobile
        rvTables.setLayoutManager(new GridLayoutManager(this, 2));
        
        tvEmptyState = findViewById(R.id.tvEmptyState);
        fabAdd = findViewById(R.id.fabAddTable);

        fabAdd.setOnClickListener(v -> showAddEditTableDialog(null));
    }

    private void loadTables() {
        tableList = tableDao.getAllTables();
        
        if (adapter == null) {
            adapter = new TableAdapter(this, tableList, new TableAdapter.OnTableClickListener() {
                @Override
                public void onEditClick(RestaurantTable table) {
                    showAddEditTableDialog(table);
                }

                @Override
                public void onDeleteClick(RestaurantTable table) {
                    showDeleteConfirmation(table);
                }
            });
            rvTables.setAdapter(adapter);
        } else {
            adapter.updateList(tableList);
        }
        
        tvEmptyState.setVisibility(tableList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showDeleteConfirmation(RestaurantTable table) {
        int position = tableList.indexOf(table);
        new AlertDialog.Builder(this)
                .setTitle(table.getIsActive() == 1 ? "Pasif Yap" : "Aktif Yap")
                .setMessage(table.getIsActive() == 1 ? "Bu masa pasif duruma getirilecek. Emin misiniz?" : "Bu masa aktif duruma getirilecek. Emin misiniz?")
                .setPositiveButton("Evet", (dialog, which) -> {
                    // Toggle isActive status
                    int newStatus = table.getIsActive() == 1 ? 0 : 1;
                    table.setIsActive(newStatus);
                    
                    // Update database
                    tableDao.updateTable(table);
                    
                    // Update UI immediately without full reload
                    if (position != -1 && adapter != null) {
                        adapter.notifyItemChanged(position);
                    }
                    
                    Toast.makeText(this, table.getIsActive() == 1 ? "Masa aktif yapıldı ✓" : "Masa pasif yapıldı ✓", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hayır", null)
                .show();
    }

    private void showAddEditTableDialog(RestaurantTable table) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(table == null ? "Yeni Masa Ekle" : "Masa Düzenle");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_table, null);
        builder.setView(view);

        EditText etNumber = view.findViewById(R.id.etTableNumber);
        EditText etName = view.findViewById(R.id.etTableName);
        Spinner spinnerArea = view.findViewById(R.id.spinnerArea);
        CheckBox cbActive = view.findViewById(R.id.cbActive);

        // Setup area spinner
        String[] areas = {"Salon", "Bahçe", "Teras"};
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, areas);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArea.setAdapter(areaAdapter);

        if (table != null) {
            etNumber.setText(String.valueOf(table.getTableNumber()));
            etName.setText(table.getTableName());
            cbActive.setChecked(table.getIsActive() == 1);
            etNumber.setEnabled(false); // Usually table number is unique ID, don't change easily or need validation
            
            // Set area selection
            String currentArea = table.getArea();
            if (currentArea != null) {
                for (int i = 0; i < areas.length; i++) {
                    if (areas[i].equals(currentArea)) {
                        spinnerArea.setSelection(i);
                        break;
                    }
                }
            }
        } else {
            cbActive.setChecked(true);
        }

        builder.setPositiveButton("Kaydet", (dialog, which) -> {
            String numStr = etNumber.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String area = spinnerArea.getSelectedItem().toString();
            boolean isActive = cbActive.isChecked();

            if (numStr.isEmpty()) {
                Toast.makeText(this, "Masa numarası zorunludur", Toast.LENGTH_SHORT).show();
                return;
            }

            int number = Integer.parseInt(numStr);

            if (table == null) {
                // Add
                RestaurantTable newTable = new RestaurantTable();
                newTable.setTableNumber(number);
                newTable.setTableName(name);
                newTable.setStatus("EMPTY");
                newTable.setArea(area);
                newTable.setIsActive(isActive ? 1 : 0);

                try {
                    long id = tableDao.addTable(newTable);
                    if (id != -1) {
                        Toast.makeText(this, "Masa eklendi ✓", Toast.LENGTH_SHORT).show();
                        loadTables();
                    } else {
                        Toast.makeText(this, "Hata: Masa numarası benzersiz olmalı", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                // Update
                table.setTableName(name);
                table.setArea(area);
                table.setIsActive(isActive ? 1 : 0);
                tableDao.updateTable(table);
                Toast.makeText(this, "Masa güncellendi ✓", Toast.LENGTH_SHORT).show();
                loadTables();
            }
        });

        builder.setNegativeButton("İptal", null);
        builder.show();
    }
}
