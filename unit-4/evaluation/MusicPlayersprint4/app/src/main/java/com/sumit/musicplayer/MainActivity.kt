package com.sumit.musicplayer

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var listItems: MutableList<ListItem>? = null

    val TAG = "MyTag"
    var searchBtn: Button? = null
    var editTextSearch: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        listItems = ArrayList<ListItem>()
        val textView = findViewById<View>(R.id.text) as EditText
        searchBtn = findViewById(R.id.searchButton)
        val queue: RequestQueue = Volley.newRequestQueue(this)
        searchBtn.setOnClickListener(View.OnClickListener {
            if (listItems!!.size > 0) {
                listItems!!.removeAll(listItems!!)
                adapter!!.notifyDataSetChanged()
            }
            editTextSearch = textView.text.toString()
            if (editTextSearch!!.isEmpty()) {
                Toast.makeText(this@MainActivity, "Please write something", Toast.LENGTH_LONG)
                    .show()
            } else {
                val url =
                    "https://itunes.apple.com/search?term=$editTextSearch&entity=musicTrack"
                val progressDialog = ProgressDialog(this@MainActivity)
                progressDialog.setMessage("Loading data...")
                progressDialog.show()
                val stringRequest = StringRequest(Request.Method.GET, url,
                    object : Listener<String?>() {
                        fun onResponse(response: String?) {
                            progressDialog.dismiss()
                            try {
                                val jsonObject = JSONObject(response)
                                val jsonArray = jsonObject.getJSONArray("results")
                                for (i in 0 until jsonArray.length()) {
                                    val o = jsonArray.getJSONObject(i)
                                    val item = ListItem(
                                        o.getString("artistName"),
                                        o.getString("artistId")
                                    )
                                    listItems!!.add(item)
                                }
                                adapter = MyAdapter(listItems, applicationContext)
                                recyclerView!!.adapter = adapter
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }, object : ErrorListener() {
                        fun onErrorResponse(error: VolleyError?) {
                            progressDialog.dismiss()
                            textView.setText("error")
                        }
                    })
                stringRequest.setTag(TAG)
                queue.add(stringRequest)
            }
        })
        if (queue != null) {
            queue.cancelAll(TAG)
        }
    }
}