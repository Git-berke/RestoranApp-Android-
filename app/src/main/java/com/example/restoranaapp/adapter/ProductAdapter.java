package com.example.restoranaapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restoranaapp.R;
import com.example.restoranaapp.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(String.format("₺%.2f", product.getPrice()));
        
        if(product.getIsActive() == 0) {
            holder.tvStatus.setText("Pasif");
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        } else {
            holder.tvStatus.setText("Aktif");
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        }

        if (product.getImagePath() != null) {
            holder.imgProduct.setImageURI(Uri.parse(product.getImagePath()));
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_food_placeholder);
        }
        
        holder.btnMore.setOnClickListener(v -> {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(context, holder.btnMore);
            popup.getMenuInflater().inflate(R.menu.menu_product_item, popup.getMenu());
            
            if (product.getIsActive() == 0) {
                popup.getMenu().findItem(R.id.action_delete).setTitle("Aktif Yap");
            } else {
                popup.getMenu().findItem(R.id.action_delete).setTitle("Pasif Yap");
            }

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_edit) {
                    listener.onEditClick(product);
                    return true;
                } else if (id == R.id.action_delete) {
                    listener.onDeleteClick(product);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvStatus;
        ImageView btnMore, imgProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvStatus = itemView.findViewById(R.id.tvProductStatus);
            btnMore = itemView.findViewById(R.id.btnMore);
            imgProduct = itemView.findViewById(R.id.imgProduct);
        }
    }
}
