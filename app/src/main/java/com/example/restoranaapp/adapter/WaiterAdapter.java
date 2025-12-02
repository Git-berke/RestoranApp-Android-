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
import com.example.restoranaapp.model.User;

import java.util.List;

public class WaiterAdapter extends RecyclerView.Adapter<WaiterAdapter.WaiterViewHolder> {

    private Context context;
    private List<User> waiterList;
    private OnWaiterClickListener listener;

    public interface OnWaiterClickListener {
        void onEditClick(User waiter);
    }

    public WaiterAdapter(Context context, List<User> waiterList, OnWaiterClickListener listener) {
        this.context = context;
        this.waiterList = waiterList;
        this.listener = listener;
    }

    public void updateList(List<User> newList) {
        this.waiterList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WaiterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_waiter, parent, false);
        return new WaiterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaiterViewHolder holder, int position) {
        User waiter = waiterList.get(position);
        
        String displayName = waiter.getFullName();
        if (displayName == null || displayName.isEmpty()) {
            displayName = waiter.getUsername();
        }
        
        holder.tvName.setText(displayName);
        holder.tvUsername.setText(waiter.getUsername()); 
        
        if (waiter.getIsActive() == 1) {
            holder.imgStatus.setColorFilter(Color.parseColor("#4CAF50"));
        } else {
            holder.imgStatus.setColorFilter(Color.parseColor("#9E9E9E"));
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(waiter));
    }

    @Override
    public int getItemCount() {
        return waiterList.size();
    }

    public static class WaiterViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvUsername;
        ImageView imgStatus, btnEdit;

        public WaiterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvWaiterName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            imgStatus = itemView.findViewById(R.id.imgStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
