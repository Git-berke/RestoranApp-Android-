package com.example.restoranaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_table_grid, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        RestaurantTable table = tableList.get(position);
        
        // Table Number
        holder.tvTableNumber.setText(String.valueOf(table.getTableNumber()));
        
        // Table Name
        String name = table.getTableName();
        if (name == null || name.isEmpty()) {
            holder.tvTableName.setText("Masa " + table.getTableNumber());
        } else {
            holder.tvTableName.setText(name);
        }
        
        // Area
        String area = table.getArea();
        if (area != null && !area.isEmpty()) {
            holder.tvTableArea.setText(area);
            holder.tvTableArea.setVisibility(View.VISIBLE);
        } else {
            holder.tvTableArea.setVisibility(View.GONE);
        }
        
        // Apply status visual based on isActive field and status
        // isActive: 1 = Active/Enabled (Green), 0 = Passive/Disabled (Grey/Dimmed)
        String status = table.getStatus();
        
        if (table.getIsActive() == 0) {
            // Passive/Disabled table - Grey/Dimmed to indicate it's disabled
            holder.cardTable.setBackground(context.getResources().getDrawable(R.drawable.table_status_empty));
            holder.cardTable.setAlpha(0.4f); // Dimmed opacity
            holder.tvTableNumber.setTextColor(context.getResources().getColor(R.color.text_gray));
            holder.tvTableName.setTextColor(context.getResources().getColor(R.color.text_gray));
            holder.tvTableArea.setTextColor(context.getResources().getColor(R.color.text_gray));
        } else {
            // Active/Enabled table - check occupancy status
            holder.cardTable.setAlpha(1.0f); // Full opacity
            holder.tvTableNumber.setTextColor(context.getResources().getColor(R.color.text_dark));
            holder.tvTableName.setTextColor(context.getResources().getColor(R.color.text_gray));
            holder.tvTableArea.setTextColor(context.getResources().getColor(R.color.text_gray));
            
            if (status != null && "ACTIVE".equalsIgnoreCase(status.trim())) {
                // Occupied - Red background and border
                holder.cardTable.setBackground(context.getResources().getDrawable(R.drawable.table_status_active));
            } else if (status != null && "RESERVED".equalsIgnoreCase(status.trim())) {
                // Reserved - Orange background and border
                holder.cardTable.setBackground(context.getResources().getDrawable(R.drawable.table_status_reserved));
            } else {
                // Empty/Available - White with green border (default for EMPTY or null)
                holder.cardTable.setBackground(context.getResources().getDrawable(R.drawable.table_status_empty));
            }
        }

        // More options menu
        holder.btnMore.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.btnMore);
            popup.inflate(R.menu.menu_table_options);
            
            if (table.getIsActive() == 0) {
                popup.getMenu().findItem(R.id.action_delete).setTitle("Aktif Yap");
            } else {
                popup.getMenu().findItem(R.id.action_delete).setTitle("Pasif Yap");
            }
            
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_edit) {
                    listener.onEditClick(table);
                    return true;
                } else if (id == R.id.action_delete) {
                    listener.onDeleteClick(table);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder {
        CardView cardTable;
        TextView tvTableNumber, tvTableName, tvTableArea;
        ImageView btnMore;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTable = itemView.findViewById(R.id.cardTable);
            tvTableNumber = itemView.findViewById(R.id.tvTableNumber);
            tvTableName = itemView.findViewById(R.id.tvTableName);
            tvTableArea = itemView.findViewById(R.id.tvTableArea);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}

