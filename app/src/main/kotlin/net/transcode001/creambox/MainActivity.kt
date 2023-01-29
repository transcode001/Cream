package net.transcode001.creambox

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import net.transcode001.creambox.async.LoadTimelineTask
import net.transcode001.creambox.async.ReloadTimelineTask
import net.transcode001.creambox.util.TwitterUtils

import twitter4j.*
import twitter4j.conf.Configuration

//import android.support.design.widget.FloatingActionButton;

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        if (!TwitterUtils.hasAccessToken(applicationContext)) {
            val intent = Intent(applicationContext, Authorization::class.java)
            startActivity(intent)
            return;
        }

        val mTweetAdapter = TweetAdapter(this)
        val listView = findViewById<View>(R.id.listView_timeline) as ListView
//        val mHandler = Handler()
        listView.visibility = View.GONE
//        val mConfiguration = TwitterUtils.getConfigurationInstance(this)
        val mTwitter = TwitterUtils.getInstance(this)
        val mSwipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.refresh)

        mSwipeRefreshLayout.setOnRefreshListener {
            reloadTimeLine(mTwitter, mTweetAdapter, mSwipeRefreshLayout)
        }

        listView.adapter = mTweetAdapter
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
        loadTimeLine(mTwitter,mTweetAdapter)

        listView.visibility = View.VISIBLE
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    private fun loadTimeLine(mTwitter:Twitter, mTweetAdapter:TweetAdapter) {
        val task = LoadTimelineTask(mTwitter, mTweetAdapter)
        task.execute()
    }

    private fun reloadTimeLine(mTwitter: Twitter,mTweetAdapter: TweetAdapter,mSwipeRefreshLayout:SwipeRefreshLayout) {
        val task = ReloadTimelineTask(mTwitter, mTweetAdapter)
        task.execute()
//        mSwipeRefreshLayout.isRefreshing = false
    }

    private fun showToast(text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }

}