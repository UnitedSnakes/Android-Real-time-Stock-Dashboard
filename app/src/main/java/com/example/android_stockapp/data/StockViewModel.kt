package com.example.android_stockapp.data

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.util.concurrent.CopyOnWriteArrayList

class StockViewModel : ViewModel() {

    private val _stockData = MutableLiveData<List<StockData>>()
    val stockData: LiveData<List<StockData>> = _stockData

    private val stockBuffer = mutableListOf<StockData>()
    private val stockList = CopyOnWriteArrayList<StockData>()

    private val webSocketManager = WebSocketManager("ws://10.0.2.2:8765")

    init {
        startWebSocket()
        startUIUpdater()
    }

    fun startWebSocket() {
        webSocketManager.connect { newStocks ->
            updateStockList(newStocks)
        }
    }

    private fun updateStockList(newStocks: List<StockData>) {
        val stockMap = stockList.associateBy { it.symbol }.toMutableMap()
        for (stock in newStocks) {
            stockMap[stock.symbol] = stock
        }

        stockList.clear()
        stockList.addAll(stockMap.values.sortedBy { it.symbol })

        Handler(Looper.getMainLooper()).post {
            _stockData.value = ArrayList(stockList)
        }
    }



    private fun startUIUpdater() {
        CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(1000)
                if (stockBuffer.isNotEmpty()) {
                    _stockData.value = stockBuffer.toList()
                    stockBuffer.clear()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketManager.disconnect()
    }
}
