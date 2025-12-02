package com.example.restoranaapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        
        String name = table.getTableName();
        if (name == null || name.isEmpty()) {
            holder.tvName.setText("Masa " + table.getTableNumber());
        } else {
            holder.tvName.setText(name);
        }

        // Status Colors based on G30
        if ("ACTIVE".equals(table.getStatus())) {
            holder.tvStatus.setText("DOLU");
            holder.tvStatus.setTextColor(Color.RED); // Text Red
            holder.imgTable.setColorFilter(Color.RED); // Icon Red
            holder.container.setBackgroundColor(Color.parseColor("#FFEBEE")); // Light Red BG
        } else {
            holder.tvStatus.setText("BOŞ");
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Text Green
            holder.imgTable.setColorFilter(Color.parseColor("#4CAF50")); // Icon Green
            holder.container.setBackgroundColor(Color.WHITE); // White BG
        }

        holder.itemView.setOnClickListener(v -> listener.onTableClick(table));
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus;
        ImageView imgTable;
        LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTableName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            imgTable = itemView.findViewById(R.id.imgTable);
            container = itemView.findViewById(R.id.container);
        }
    }
}

