package com.example.restoranaapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restoranaapp.R;
import com.example.restoranaapp.model.RestaurantTable;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private Context context;
    private List<RestaurantTable> tableList;
    private OnTableClickListener listener;

    public interface OnTableClickListener {
        void onEditClick(RestaurantTable table);
        void onDeleteClick(RestaurantTable table);
    }

    public TableAdapter(Context context, List<RestaurantTable> tableList, OnTableClickListener listener) {
        this.context = context;
        this.tableList = tableList;
        this.listener = listener;
    }

    public void updateList(List<RestaurantTable> newList) {
        this.tableList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_table, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        RestaurantTable table = tableList.get(position);
        
        holder.tvTableNumber.setText(String.valueOf(table.getTableNumber()));
        
        String name = table.getTableName();
        if(name == null || name.isEmpty()){
            holder.tvTableName.setText("Masa " + table.getTableNumber());
        } else {
            holder.tvTableName.setText(name);
        }
        
        // Status Logic
        if ("ACTIVE".equals(table.getStatus())) {
            holder.tvTableStatus.setText("DOLU");
            holder.tvTableStatus.setTextColor(Color.RED);
        } else {
            holder.tvTableStatus.setText("BOŞ");
            holder.tvTableStatus.setTextColor(Color.parseColor("#4CAF50"));
        }

        // Active/Passive logic (different from occupied status)
        if (table.getIsActive() == 0) {
            holder.tvTableName.append(" (Pasif)");
            holder.tvTableName.setTextColor(Color.GRAY);
            // Can allow editing to make active again
        } else {
            holder.tvTableName.setTextColor(Color.BLACK);
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(table));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(table));
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder {
        TextView tvTableNumber, tvTableName, tvTableStatus;
        ImageView btnEdit, btnDelete;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTableNumber = itemView.findViewById(R.id.tvTableNumber);
            tvTableName = itemView.findViewById(R.id.tvTableName);
            tvTableStatus = itemView.findViewById(R.id.tvTableStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

