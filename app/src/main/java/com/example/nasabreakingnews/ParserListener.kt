package com.example.nasabreakingnews

interface ParserListener {
    // 兩個function用來通知開始parse和結束parse的時候給observer
    // 並且在結束parse的時候順便把parse到的新聞傳給observer
    fun start()
    fun finish(news: List<NewsData>)
}