import asyncio
import websockets

async def receive_stock_data():
    uri = "ws://localhost:8765"
    async with websockets.connect(uri) as websocket:
        print("Connected to WebSocket server!")
        try:
            while True:
                data = await websocket.recv()
                print(f"Received: {data}")
        except websockets.exceptions.ConnectionClosed:
            print("Connection closed by server!")
        except Exception as e:
            print(f"Error: {e}")

asyncio.run(receive_stock_data())
