package com.example.android_stockapp.data

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class WebSocketManager(private val url: String) {
    private var webSocket: WebSocket? = null
    private var listener: ((List<StockData>) -> Unit)? = null
    private val client = OkHttpClient.Builder().build()

    private var reconnectAttempts = 0
    private var pongReceived = true
    private var pingJob: Job? = null

    fun connect(listener: (List<StockData>) -> Unit) {
        this.listener = listener
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, StockWebSocketListener())
    }

    fun disconnect() {
        pingJob?.cancel()
        webSocket?.close(1000, "Closing connection")
        webSocket = null
    }

    private fun sendPing() {
        if (webSocket != null) {
            if (!pongReceived) {
                Log.w("WebSocketManager", "No Pong received, reconnecting...")
                reconnect()
            } else {
                webSocket?.send(ByteString.EMPTY)
                pongReceived = false
                Log.d("WebSocketManager", "Ping sent")
            }
        }
    }

    private fun startPingRoutine() {
        pingJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(10000)
                sendPing()
            }
        }
    }

    private fun reconnect() {
        if (reconnectAttempts < 5) {
            reconnectAttempts++
            Log.w("WebSocketManager", "Reconnecting... Attempt $reconnectAttempts")
            connect(listener!!)
        } else {
            Log.e("WebSocketManager", "Max reconnect attempts reached")
        }
    }

    private inner class StockWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("WebSocketManager", "Connected to WebSocket")
            reconnectAttempts = 0
            startPingRoutine()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            val stockList = parseStockData(text)
            listener?.invoke(stockList)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("WebSocketManager", "WebSocket Failure: ${t.message}")
            reconnect()
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.w("WebSocketManager", "WebSocket Closing: $reason")
            reconnect()
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.w("WebSocketManager", "WebSocket Closed: $reason")
            reconnect()
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d("WebSocketManager", "Pong received")
            pongReceived = true
        }
    }

    private fun parseStockData(jsonString: String): List<StockData> {
        val jsonObject = JSONObject(jsonString)
        val stocks = mutableListOf<StockData>()
        val symbol = jsonObject.getString("symbol")
        val price = jsonObject.getDouble("price")
        val change = jsonObject.getDouble("change")
        stocks.add(StockData(symbol, price, change))
        return stocks
    }
}
