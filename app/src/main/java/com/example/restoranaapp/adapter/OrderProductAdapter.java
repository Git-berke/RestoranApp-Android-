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

import java.util.List;
import java.util.Map;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;
    private Map<Integer, Integer> cartMap;
    private OnQuantityChangeListener listener;

    public interface OnQuantityChangeListener {
        void onQuantityChanged(Product product, int newQuantity);
    }

    public OrderProductAdapter(Context context, List<Product> productList, Map<Integer, Integer> cartMap, OnQuantityChangeListener listener) {
        this.context = context;
        this.productList = productList;
        this.cartMap = cartMap;
        this.listener = listener;
    }

    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }
    
    public void updateCart(Map<Integer, Integer> newCart) {
        this.cartMap = newCart;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(String.format("₺%.2f", product.getPrice()));
        
        int qty = 0;
        if (cartMap.containsKey(product.getId())) {
            qty = cartMap.get(product.getId());
        }
        holder.tvQuantity.setText(String.valueOf(qty));

        if (product.getImagePath() != null) {
            holder.imgProduct.setImageURI(Uri.parse(product.getImagePath()));
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_food_placeholder);
        }

        holder.btnPlus.setOnClickListener(v -> {
            int current = 0;
            if (cartMap.containsKey(product.getId())) {
                current = cartMap.get(product.getId());
            }
            listener.onQuantityChanged(product, current + 1);
        });

        holder.btnMinus.setOnClickListener(v -> {
            int current = 0;
            if (cartMap.containsKey(product.getId())) {
                current = cartMap.get(product.getId());
            }
            if (current > 0) {
                listener.onQuantityChanged(product, current - 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity;
        ImageView btnMinus, btnPlus, imgProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            imgProduct = itemView.findViewById(R.id.imgProduct);
        }
    }
}
