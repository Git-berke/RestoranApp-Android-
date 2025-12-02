package com.example.restoranaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restoranaapp.R;
import com.example.restoranaapp.model.ProductSalesReport;

import java.util.List;

public class SalesReportAdapter extends RecyclerView.Adapter<SalesReportAdapter.ViewHolder> {

    private Context context;
    private List<ProductSalesReport> list;

    public SalesReportAdapter(Context context, List<ProductSalesReport> list) {
        this.context = context;
        this.list = list;
    }
    
    public void updateList(List<ProductSalesReport> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_report_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductSalesReport item = list.get(position);
        holder.tvName.setText(item.getProductName());
        holder.tvQty.setText(String.valueOf(item.getQuantity()));
        holder.tvAmount.setText(String.format("₺%.2f", item.getTotalAmount()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvQty = itemView.findViewById(R.id.tvQuantity);
            tvAmount = itemView.findViewById(R.id.tvTotalAmount);
        }
    }
}

