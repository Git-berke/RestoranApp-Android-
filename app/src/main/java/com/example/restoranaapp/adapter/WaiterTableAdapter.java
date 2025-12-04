package com.example.restoranaapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restoranaapp.R;
import com.example.restoranaapp.model.RestaurantTable;

import java.util.List;

public class WaiterTableAdapter extends RecyclerView.Adapter<WaiterTableAdapter.ViewHolder> {

    private Context context;
    private List<RestaurantTable> tableList;
    private OnTableClickListener listener;

    public interface OnTableClickListener {
        void onTableClick(RestaurantTable table);
    }

    public WaiterTableAdapter(Context context, List<RestaurantTable> tableList, OnTableClickListener listener) {
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_table_waiter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RestaurantTable table = tableList.get(position);
        
        // Display large table number
        holder.tvTableNumber.setText(String.valueOf(table.getTableNumber()));
        
        // Optional: Show table name if not empty
        String name = table.getTableName();
        if (name != null && !name.isEmpty() && !name.equals("Masa " + table.getTableNumber())) {
            holder.tvTableName.setText(name);
            holder.tvTableName.setVisibility(View.VISIBLE);
        } else {
            holder.tvTableName.setVisibility(View.GONE);
        }

        // Simplified Status Colors - Clean and Modern
        String status = table.getStatus();
        if (status != null && "ACTIVE".equalsIgnoreCase(status.trim())) {
            // Occupied - Red background and border
            holder.cardTable.setBackground(context.getResources().getDrawable(R.drawable.table_status_active));
            holder.tvStatus.setText("DOLU");
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#D32F2F")));
        } else {
            // Empty - White with green border
            holder.cardTable.setBackground(context.getResources().getDrawable(R.drawable.table_status_empty));
            holder.tvStatus.setText("BOŞ");
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        }

        holder.itemView.setOnClickListener(v -> listener.onTableClick(table));
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardTable;
        TextView tvTableNumber, tvTableName, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTable = itemView.findViewById(R.id.cardTable);
            tvTableNumber = itemView.findViewById(R.id.tvTableNumber);
            tvTableName = itemView.findViewById(R.id.tvTableName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}

