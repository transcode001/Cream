package net.transcode001.creambox


import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView

import com.loopj.android.image.SmartImageView

import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL

import twitter4j.QueryResult
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.TwitterException


class TweetSearch : Activity() {

    private var mAdapter: TweetAdapter? = null
    private var mTwitter: Twitter? = null
    private var et: EditText? = null
    private var mQuery: twitter4j.Query? = null
    private var listView: ListView? = null
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_main)
        listView = findViewById<View>(R.id.listView1) as ListView
        mAdapter = TweetAdapter(this)
        listView!!.adapter = mAdapter
        mTwitter = TwitterUtils.getInstance(this)
        et = findViewById<View>(R.id.word_search) as EditText

        mSwipeRefreshLayout = findViewById<View>(R.id.refresh_search) as SwipeRefreshLayout
        mSwipeRefreshLayout!!.setOnRefreshListener {
            Handler().postDelayed({
                if (et!!.text.toString() != "") {

                    mQuery!!.query = et!!.text.toString()
                    searchWord(mQuery)
                }
                mSwipeRefreshLayout!!.isRefreshing = false
            }, 3000)
        }


        mQuery = twitter4j.Query()
        findViewById<View>(R.id.search_button).setOnClickListener {
            if (et!!.text.toString() != "") {

                mQuery!!.query = et!!.text.toString()
                searchWord(mQuery)
            }
        }

    }

    private fun searchWord(query: twitter4j.Query?) {
        mAdapter!!.clear()
        val mHandler = Handler()
        val task = object : AsyncTask<Void, Void, QueryResult>() {
            override fun doInBackground(vararg voids: Void): QueryResult? {
                try {
                    val result = mTwitter!!.search(query)
                    mHandler.post {
                        for (status in result.tweets) {
                            mAdapter!!.insert(status, 0)
                            println(result.tweets.toString())
                        }
                    }

                } catch (e: TwitterException) {
                    Log.e("Failed to search method", e.toString())
                }

                return null
            }
        }

        task.execute()

    }


}
