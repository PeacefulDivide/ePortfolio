package com.alttabtechnologies.inventorytracker.view

import androidx.recyclerview.widget.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alttabtechnologies.inventorytracker.databinding.ItemInventoryBinding
import com.alttabtechnologies.inventorytracker.model.InventoryItem
import java.util.Locale

/**
 * ListAdapter class utilizing DiffUtil to eliminate redundant view redraws.
 */

class InventoryAdapter(
    private val onItemClick: (InventoryItem) -> Unit,
    private val onItemLongClick: (InventoryItem) -> Unit
) : ListAdapter<InventoryItem, InventoryAdapter.InventoryViewHolder>(InventoryDiffCallback()) {

    // Inflates individual row layout using ViewBinding and is only called when RecyclerView needs a new instance.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val binding = ItemInventoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InventoryViewHolder(binding, onItemClick, onItemLongClick)
    }

    // Binds incoming data from the list to the ViewHolder.
    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // Encapsulates item view state and isolates event click bindings.
    class InventoryViewHolder(
        private val binding: ItemInventoryBinding,
        private val onItemClick: (InventoryItem) -> Unit,
        private val onItemLongClick: (InventoryItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        // Binds properties of InventoryItem directly to target View.
        fun bind(item: InventoryItem) {
            binding.apply {
                tvItemName.text = item.name
                tvItemSku.text = "SKU: ${item.sku}"
                tvItemQuantity.text = "Qty: ${item.quantity}"
                tvItemSelection.text = String.format(Locale.US, "$%.2f", item.price)

                // Highlight low stock warnings dynamically.
                val context = root.context
                if (item.isLowStock) {
                    // Set warning color.
                    tvItemQuantity.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                } else {
                    tvItemQuantity.setTextColor(ContextCompat.getColor(context, android.R.color.tab_indicator_text))
                }

                // Make entire row clickable to open detail fragment.
                root.setOnClickListener {
                    onItemClick(item)
                }

                // Handle long press action for item deletion.
                root.setOnLongClickListener {
                    onItemLongClick(item)

                    // Return true to indicate long click event.
                    true
                }
            }
        }
    }

    // DiffUtil logic checking for differences between items.
    private class InventoryDiffCallback : DiffUtil.ItemCallback<InventoryItem>() {
        override fun areItemsTheSame(oldItem: InventoryItem, newItem: InventoryItem): Boolean {
        return oldItem.id == newItem.id
        }

        // Checks to see if row item needs to visually update changes.
        override fun areContentsTheSame(oldItem: InventoryItem, newItem: InventoryItem): Boolean {
            return oldItem == newItem
        }
    }
}
