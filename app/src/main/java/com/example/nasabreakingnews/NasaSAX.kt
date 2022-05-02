package com.example.nasabreakingnews

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.net.URL
import javax.xml.parsers.SAXParserFactory

class NasaSAX(val listener: ParserListener): DefaultHandler() {
    // 繼承DefaultHandler class
    private val factory = SAXParserFactory.newInstance()
    private val parser = factory.newSAXParser()
    private var itemFound = false //是否找到了某篇新聞
    private var titleFound = false //是否找到了某篇新聞的標題
    private var element: String = "" //存放每個element的資料
    private var newsTitle: String = "" //存放新聞標題
    private var data = mutableListOf<NewsData>() //存放所有新聞的一個List，資料型態都是NewsData的物件

    private var imageFound = false //是否找到了某篇新聞的圖片
    private var newsCover: Bitmap? = null //存放新聞圖片


    fun parseURL(url: String) {
        listener.start()
        GlobalScope.launch() {
            try {
                val inputStream = URL(url).openStream() // 為指定的URL開啟資料流dataStream的連結
                parser.parse(inputStream, this@NasaSAX) // 通知本身this@NasaSAX來處理parse的事件
                withContext(Dispatchers.Main) {
                    listener.finish(data)
                }
            } catch (e: Throwable) {
                e.printStackTrace() // 不做特別的處理，單純把錯誤訊息印出
            }
        }
    }

    override fun startElement(
        uri: String?,
        localName: String?,
        qName: String?,
        attributes: Attributes?
    ) {
        super.startElement(uri, localName, qName, attributes)
        if (localName == "item") { //因為NASA的rss檔中是以item作為每一篇新聞的標籤
            itemFound = true
        }
        if (itemFound) {
            if (localName == "title") {
                titleFound = true
            } else if (localName == "enclosure" && attributes?.getValue("type") == "image/jpeg" || attributes?.getValue("type") == "image/png") {
                imageFound = true
                val url = attributes?.getValue("url") //將圖片的url擷取下來
                Log.i("Url: ", url)
                val inputStream = URL(url).openStream()
                newsCover = BitmapFactory.decodeStream(inputStream)
                if (newsCover != null) {
                    Log.i("newsCover: ","not null" )
                } else {
                    Log.i("newsCover: ","null" )
                }
            }
        }
        element = "" //先將element清空
    }


    override fun endElement(uri: String?, localName: String?, qName: String?) {
        super.endElement(uri, localName, qName)
        if (itemFound) {
            if (titleFound) {
                titleFound = false //將titleFound重置為false
                newsTitle = element
                Log.i("Title: ", newsTitle) //除錯，印出歌名
            } else if (imageFound) {
                imageFound = false
            }
        }
        if (localName == "item") {
            itemFound = false //將itemFound重置為false
            data.add(NewsData(newsTitle, newsCover)) //將這篇新聞的標題包成一個NewsData物件，並放進data這個List中
        }
    }

    override fun characters(ch: CharArray?, start: Int, length: Int) {
        super.characters(ch, start, length)
        ch?.let { //如果ch變數不是null就執行這段程式碼，如果是null就不執行
            element += String(it, start, length) //it指的是前面的變數，此處即為ch
            //當資料完整讀取完畢，新聞標題會存在element中
        }
    }
}