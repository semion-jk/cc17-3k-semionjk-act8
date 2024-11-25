package com.example.bookshelfapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {

    lateinit var mRequestQueue: RequestQueue
    lateinit var booksList: ArrayList<BookRVModal>
    lateinit var loadingPB: ProgressBar
    lateinit var searchEdt: EditText
    lateinit var searchBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadingPB = findViewById(R.id.idLoadingPB)
        searchEdt = findViewById(R.id.idEdtSearchBooks)
        searchBtn = findViewById(R.id.idBtnSearch)

        searchBtn.setOnClickListener {
            loadingPB.visibility = View.VISIBLE
            if (searchEdt.text.toString().isNullOrEmpty()) {
                searchEdt.setError("Please enter a book")
            }
            getBooksData(searchEdt.getText().toString());
        }

    }

    private fun getBooksData(searchQuery: String) {
        booksList = ArrayList()
        mRequestQueue = Volley.newRequestQueue(this@MainActivity)
        mRequestQueue.cache.clear()

        val url = "https://www.googleapis.com/books/v1/volumes?q=$searchQuery"
        val queue = Volley.newRequestQueue(this@MainActivity)
        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            loadingPB.visibility = View.GONE;
            try {
                val itemsArray = response.getJSONArray("items")
                for (i in 0 until itemsArray.length()) {
                    val itemsObj = itemsArray.getJSONObject(i)
                    val volumeObj = itemsObj.getJSONObject("volumeInfo")
                    val title = volumeObj.optString("title")
                    val subtitle = volumeObj.optString("subtitle")
                    val authorsArray = volumeObj.getJSONArray("authors")
                    val publisher = volumeObj.optString("publisher")
                    val publishedDate = volumeObj.optString("publishedDate")
                    val description = volumeObj.optString("description")
                    val pageCount = volumeObj.optInt("pageCount")
                    val imageLinks = volumeObj.optJSONObject("imageLinks")
                    val thumbnail = imageLinks.optString("thumbnail")
                    val previewLink = volumeObj.optString("previewLink")
                    val infoLink = volumeObj.optString("infoLink")
                    val saleInfoObj = itemsObj.optJSONObject("saleInfo")
                    val buyLink = saleInfoObj.optString("buyLink")
                    val authorsArrayList: ArrayList<String> = ArrayList()
                    if (authorsArray.length() != 0) {
                        for (j in 0 until authorsArray.length()) {
                            authorsArrayList.add(authorsArray.optString(i))
                        }
                    }
                    val bookInfo = BookRVModal(
                        title,
                        subtitle,
                        authorsArrayList,
                        publisher,
                        publishedDate,
                        description,
                        pageCount,
                        thumbnail,
                        previewLink,
                        infoLink,
                        buyLink
                    )
                    booksList.add(bookInfo)
                    val adapter = BookRVAdapter(booksList, this@MainActivity)
                    val layoutManager = GridLayoutManager(this, 3)
                    val mRecyclerView = findViewById<RecyclerView>(R.id.idRVBooks) as RecyclerView
                    mRecyclerView.layoutManager = layoutManager
                    mRecyclerView.adapter = adapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, { error ->
            Toast.makeText(this@MainActivity, "No books found..", Toast.LENGTH_SHORT)
                .show()
        })
        queue.add(request)

    }
}