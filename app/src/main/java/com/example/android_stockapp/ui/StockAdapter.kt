package com.example.android_stockapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android_stockapp.R
import com.example.android_stockapp.data.StockData
import com.example.android_stockapp.databinding.ItemStockBinding
import kotlin.random.Random
import com.robinhood.spark.SparkAdapter

class StockAdapter : ListAdapter<StockData, StockAdapter.StockViewHolder>(StockDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val binding = ItemStockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val stock = getItem(position)

        holder.binding.symbol.text = stock.symbol
        holder.binding.price.text = "$${stock.price}"
        holder.binding.change.text = "${stock.change}%"

        val context = holder.binding.root.context
        if (stock.change >= 0) {
            holder.binding.change.setTextColor(context.getColor(R.color.green))
            holder.binding.change.setBackgroundResource(R.drawable.green_bg)
            holder.binding.stockChart.setLineColor(context.getColor(R.color.green))
        } else {
            holder.binding.change.setTextColor(context.getColor(R.color.red))
            holder.binding.change.setBackgroundResource(R.drawable.red_bg)
            holder.binding.stockChart.setLineColor(context.getColor(R.color.red))
        }

        val randomData = FloatArray(10) { kotlin.random.Random.nextFloat() * 100 }
        val sparkAdapter = object : SparkAdapter() {
            override fun getCount() = randomData.size
            override fun getItem(index: Int) = randomData[index].toDouble()
            override fun getY(index: Int): Float = randomData[index]
        }
        holder.binding.stockChart.adapter = sparkAdapter
    }

    inner class StockViewHolder(val binding: ItemStockBinding) :
        RecyclerView.ViewHolder(binding.root)
}

class StockDiffCallback : DiffUtil.ItemCallback<StockData>() {
    override fun areItemsTheSame(oldItem: StockData, newItem: StockData): Boolean {
        return oldItem.symbol == newItem.symbol
    }

    override fun areContentsTheSame(oldItem: StockData, newItem: StockData): Boolean {
        return oldItem == newItem
    }
}
