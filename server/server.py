import asyncio
import websockets
import json
import random

async def stock_data(websocket):
    try:
        # ✅ 监听 Pong 消息
        async def handle_pong(message):
            print(f"Pong received: {message}")

        websocket.pong_handler = handle_pong  # 绑定 Pong 处理函数

        while True:
            # 生成随机股票数据
            stock = random.choice(["AAPL", "GOOGL", "AMZN", "TSLA", "MSFT"])
            price = round(random.uniform(100, 2000), 2)
            change = round(random.uniform(-5, 5), 2)

            data = json.dumps({"symbol": stock, "price": price, "change": change})
            print(f"Sending: {data}")
            await websocket.send(data)

            await asyncio.sleep(1)  # 每秒推送
    except websockets.exceptions.ConnectionClosed:
        print("Client disconnected!")

async def main():
    # ✅ 启用 Ping-Pong
    server = await websockets.serve(stock_data, "0.0.0.0", 8765, ping_interval=10, ping_timeout=5)
    print("WebSocket Server Started on ws://0.0.0.0:8765")
    await server.wait_closed()

if __name__ == "__main__":
    asyncio.run(main())
