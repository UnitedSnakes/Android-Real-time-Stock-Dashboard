package com.example.android_stockapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_stockapp.databinding.ActivityMainBinding
import com.example.android_stockapp.data.StockViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: StockViewModel
    private lateinit var adapter: StockAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = StockAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        viewModel = ViewModelProvider(this)[StockViewModel::class.java]
        viewModel.startWebSocket()

        viewModel.stockData.observe(this) { newData ->
            adapter.submitList(newData.toList()) {
                binding.recyclerView.scrollToPosition(0)
            }
        }
    }
}
