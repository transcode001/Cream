package net.transcode001.creambox

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import net.transcode001.creambox.asyncs.LoadTimelineTask
import net.transcode001.creambox.asyncs.ReloadTimelineTask

import twitter4j.*
import twitter4j.conf.Configuration

//import android.support.design.widget.FloatingActionButton;

class MainActivity : AppCompatActivity() {


    var mTwitter: Twitter
    var mConfiguration: Configuration
    var mTweetAdapter: TweetAdapter
    private var listView: ListView? = null
    private var mHandler: Handler? = null
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        if (!TwitterUtils.hasAccessToken(applicationContext)) {
            val intent = Intent(applicationContext, Authorization::class.java)
            startActivity(intent)
            finish()
        } else {

            mTweetAdapter = TweetAdapter(this)
            listView = findViewById<View>(R.id.listView_timeline) as ListView
            mHandler = Handler()
            listView!!.visibility = View.GONE
            mConfiguration = TwitterUtils.getConfigurationInstance(this)
            mTwitter = TwitterUtils.getInstance(this)
            mSwipeRefreshLayout = findViewById(R.id.refresh)
            mSwipeRefreshLayout!!.isRefreshing = false
            mSwipeRefreshLayout!!.setOnRefreshListener { reloadTimeLine() }

            listView!!.adapter = mTweetAdapter
            /*
            FloatingActionButton fab = findViewById(R.id.float_icon);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(),TweetActivity.class);
                    startActivity(intent);
                }
            });
            */

            loadTimeLine()

            listView!!.visibility = View.VISIBLE
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    private fun loadTimeLine() {
        val task = LoadTimelineTask(mTwitter, mTweetAdapter)
        task.execute()
    }

    private fun reloadTimeLine() {
        val task = ReloadTimelineTask(mTwitter, mTweetAdapter)
        task.execute()
        mSwipeRefreshLayout!!.isRefreshing = false
    }

    private fun showToast(text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }

}