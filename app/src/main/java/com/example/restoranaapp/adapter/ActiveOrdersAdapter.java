package com.example.restoranaapp.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restoranaapp.R;
import com.example.restoranaapp.model.ActiveOrderDto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActiveOrdersAdapter extends RecyclerView.Adapter<ActiveOrdersAdapter.ViewHolder> {

    private Context context;
    private List<ActiveOrderDto> list;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(ActiveOrderDto order);
    }

    public ActiveOrdersAdapter(Context context, List<ActiveOrderDto> list, OnOrderClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void updateList(List<ActiveOrderDto> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_active_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActiveOrderDto order = list.get(position);
        
        holder.tvTableName.setText(order.getTableName());
        holder.tvStatus.setText("Durum: " + order.getStatus());
        holder.tvTotal.setText(String.format("₺%.2f", order.getTotalPrice()));
        
        // Parse time
        String timeStr = order.getCreatedAt();
        try {
            SimpleDateFormat dbFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = dbFmt.parse(timeStr);
            SimpleDateFormat displayFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
            timeStr = displayFmt.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tvTime.setText("Zaman: " + timeStr);
        
        // Colors
        if ("PENDING".equals(order.getStatus())) {
            int color = Color.parseColor("#FFA000"); // Orange
            holder.tvStatus.setTextColor(color);
            holder.imgStatusDot.setImageTintList(ColorStateList.valueOf(color));
        } else if ("SERVED".equals(order.getStatus())) {
             int color = Color.parseColor("#4CAF50"); // Green
            holder.tvStatus.setTextColor(color);
            holder.imgStatusDot.setImageTintList(ColorStateList.valueOf(color));
        } else {
             int color = Color.parseColor("#1976D2"); // Blue (for others)
            holder.tvStatus.setTextColor(color);
            holder.imgStatusDot.setImageTintList(ColorStateList.valueOf(color));
        }

        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTableName, tvStatus, tvTotal, tvTime;
        ImageView imgStatusDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTableName = itemView.findViewById(R.id.tvTableName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvTime = itemView.findViewById(R.id.tvTime);
            imgStatusDot = itemView.findViewById(R.id.imgStatusDot);
        }
    }
}

