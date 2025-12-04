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

import com.bumptech.glide.Glide;
import com.example.restoranaapp.R;
import com.example.restoranaapp.model.OrderItem;
import com.example.restoranaapp.model.Product;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private List<OrderItem> cartItems;
    private List<Product> allProducts; 

    public CartAdapter(Context context, List<OrderItem> cartItems, List<Product> allProducts) {
        this.context = context;
        this.cartItems = cartItems;
        this.allProducts = allProducts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = cartItems.get(position);
        
        String name = "Ürün";
        String imagePath = null;
        
        for(Product p : allProducts) {
            if(p.getId() == item.getProductId()) {
                name = p.getName();
                imagePath = p.getImagePath();
                break;
            }
        }

        holder.tvName.setText(name);
        holder.tvQtyPrice.setText(item.getQuantity() + " x " + String.format("%.2f ₺", item.getUnitPrice()));
        holder.tvPrice.setText(String.format("%.2f ₺", item.getLineTotal()));
        
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(context)
                .load(imagePath)
                .placeholder(R.drawable.ic_food_placeholder)
                .error(R.drawable.ic_food_placeholder)
                .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_food_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQtyPrice, tvPrice;
        ImageView imgProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvQtyPrice = itemView.findViewById(R.id.tvQtyPrice);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imgProduct = itemView.findViewById(R.id.imgProduct);
        }
    }
}
