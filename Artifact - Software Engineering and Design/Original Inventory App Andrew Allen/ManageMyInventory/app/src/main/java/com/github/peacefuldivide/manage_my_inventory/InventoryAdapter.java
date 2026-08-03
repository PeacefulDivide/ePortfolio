package com.github.peacefuldivide.manage_my_inventory;

import androidx.annotation.NonNull;
import android.widget.ImageButton;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private final List<InventoryItem> itemList;

    // Constructor to pass data to the adapter
    public InventoryAdapter(List<InventoryItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Fill item_layout.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inventory_item, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem currentItem = itemList.get(position);

        // Map data to UI
        holder.itemName.setText(currentItem.getName());
        String quantityText = holder.itemView.getContext().getString(R.string.quantityText, currentItem.getQuantity());
        holder.itemQuantity.setText(quantityText);

        // Feedback loop for low stock
        if (currentItem.getQuantity() <= currentItem.getThreshold()) {
            // Set red color for low inventory level
            holder.itemQuantity.setTextColor(0xFFFF0000);

        } else {
            // Set color black for normal levels
            holder.itemQuantity.setTextColor(0xFF000000);
        }

        // Delete button logic
        holder.deleteBtn.setOnClickListener(v -> {
            DatabaseHelper db = new DatabaseHelper(v.getContext());
            db.deleteItem(currentItem.getId());
            itemList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itemList.size());
        });

        // Click card to edit
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ItemDetailActivity.class);
            intent.putExtra("ITEM_ID", currentItem.getId());
            intent.putExtra("ITEM_NAME", currentItem.getName());
            intent.putExtra("ITEM_QTY", currentItem.getQuantity());
            intent.putExtra("ITEM_THRESHOLD", currentItem.getThreshold());
            v.getContext().startActivity(intent);
        });
    }

    // Cache for references
    public static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity;
        ImageButton deleteBtn;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            deleteBtn = itemView.findViewById(R.id.deleteButton);
        }
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }
}
