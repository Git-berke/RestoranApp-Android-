package com.example.restoranaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restoranaapp.R;
import com.example.restoranaapp.model.Ingredient;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    private Context context;
    private List<Ingredient> ingredientList;
    private OnStockItemClickListener listener;

    public interface OnStockItemClickListener {
        void onEditClick(Ingredient ingredient);
    }

    public StockAdapter(Context context, List<Ingredient> ingredientList, OnStockItemClickListener listener) {
        this.context = context;
        this.ingredientList = ingredientList;
        this.listener = listener;
    }

    public void updateList(List<Ingredient> newList) {
        this.ingredientList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stock_list, parent, false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Ingredient ingredient = ingredientList.get(position);
        
        holder.tvIngredientName.setText(ingredient.getName());
        
        // Format stock amount with unit
        String stockText = String.format("%.1f %s", ingredient.getQuantity(), ingredient.getUnit());
        holder.tvStockAmount.setText(stockText);
        
        // Check if stock is low
        boolean isLowStock = ingredient.isLowStock();
        
        // Set stock status text and color
        if (isLowStock) {
            holder.tvStockStatus.setText("Stok Durumu: Kritik Seviye!");
            holder.tvStockStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            holder.stockIndicator.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvStockStatus.setText("Stok Durumu: Normal");
            holder.tvStockStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.stockIndicator.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
        }
        
        // Calculate progress percentage (stock vs critical level * 2 as max)
        double maxForProgress = ingredient.getCriticalThreshold() * 2;
        int progressPercent = (int) ((ingredient.getQuantity() / maxForProgress) * 100);
        progressPercent = Math.min(100, Math.max(0, progressPercent)); // Clamp between 0-100
        
        holder.stockProgressBar.setProgress(progressPercent);
        
        // Set progress bar color based on stock level
        if (isLowStock) {
            holder.stockProgressBar.setProgressTintList(
                context.getResources().getColorStateList(android.R.color.holo_red_dark));
        } else {
            holder.stockProgressBar.setProgressTintList(
                context.getResources().getColorStateList(android.R.color.holo_green_dark));
        }
        
        // Edit button click
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(ingredient);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    public static class StockViewHolder extends RecyclerView.ViewHolder {
        TextView tvIngredientName, tvStockAmount, tvStockStatus;
        ImageView btnEdit;
        View stockIndicator;
        ProgressBar stockProgressBar;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngredientName = itemView.findViewById(R.id.tvIngredientName);
            tvStockAmount = itemView.findViewById(R.id.tvStockAmount);
            tvStockStatus = itemView.findViewById(R.id.tvStockStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            stockIndicator = itemView.findViewById(R.id.stockIndicator);
            stockProgressBar = itemView.findViewById(R.id.stockProgressBar);
        }
    }
}

