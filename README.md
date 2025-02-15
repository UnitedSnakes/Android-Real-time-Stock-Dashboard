# JSONFetcherApp

A simple Android app to evaluate architectural differences, implementing MVVM with Retrofit, Gson, and SQLite for paginated JSON retrieval. 

## Features
- **MVVM Architecture**: Demonstrates superior state and lifecycle management over MVP and MVC.
- **Efficient Pagination**: Uses SQLite to cache JSON data fetched via Retrofit.
- **Optimized RecyclerView**: Implements a custom `DiffUtil.Callback`, reducing refresh overhead by 20-50ms.

## Tech Stack
- **Kotlin**
- **Retrofit + Gson**
- **SQLite**
- **RecyclerView with DiffUtil**
