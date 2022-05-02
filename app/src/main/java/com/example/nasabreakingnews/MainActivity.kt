package com.example.nasabreakingnews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity(), NasaRecyclerViewAdapter.RecyclerViewClickListener {

    // val titles = mutableListOf<String>()
    val adapter: NasaRecyclerViewAdapter by lazy {
        NasaRecyclerViewAdapter(listOf<NewsData>(), this)
    }

    val swipeRefreshLayout by lazy {
        findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
    }

    private fun loadList() {
        NasaSAX(object: ParserListener{ //用object作為語法
            override fun start() {
                swipeRefreshLayout.isRefreshing = true //出現畫面轉動的圖案
            }
            override fun finish(news: List<NewsData>) {
                adapter.news = news
                swipeRefreshLayout.isRefreshing = false //停止畫面轉動的圖案
            }
        }).parseURL("nasa.gov/rss/dyn/breaking_news.rss")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swiperefresh)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView) //取得recyclerView的reference
        recyclerView.adapter = adapter //設定他的adapter
        recyclerView.layoutManager = LinearLayoutManager(this) //設定layout manager，此處使用LinearLayoutManager
        recyclerView.addItemDecoration(DividerItemDecoration( //設定recyclerView的裝飾
            recyclerView.context,
            DividerItemDecoration.HORIZONTAL
        ))

        swipeRefreshLayout.setOnRefreshListener {
            loadList()
        }
        loadList()
    }
    override fun onItemClick(view: View, position: Int) {
        Toast.makeText(this, adapter.news[position].title, Toast.LENGTH_LONG).show()
        Log.i("click", position.toString())
    }
}