//RecyclerViewAdapter.kt

package com.example.android_jsonfetcher.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android_jsonfetcher.data.Item
import com.example.android_jsonfetcher.R

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private var items: MutableList<Item> = mutableListOf()

    // Set new data and total rows
    fun setItems(newItems: List<Item>) {
        val diffResult = calculateDiff(items, newItems)
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    private fun calculateDiff(oldList: List<Item>, newList: List<Item>): DiffUtil.DiffResult {
        return DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldList.size
            override fun getNewListSize(): Int = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                // Use a combination of listId and name to determine if items are the same
                val oldItem = oldList[oldItemPosition]
                val newItem = newList[newItemPosition]
                return oldItem.listId == newItem.listId && oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = oldList[oldItemPosition]
                val newItem = newList[newItemPosition]
                return oldItem == newItem
            }
        })
    }

    // Create a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_layout, parent, false)
        return ViewHolder(view)
    }

    // Bind data to ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    // Return the number of items in the list
    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Bind view elements and data here
        fun bind(item: Item) {
            val listIdTextView: TextView = itemView.findViewById(R.id.listIdTextView)
            val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)

            listIdTextView.text = item.listId.toString()
            nameTextView.text = item.name
        }
    }
}
