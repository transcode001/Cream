package net.transcode001.creambox.async

import android.os.AsyncTask
import android.util.Log

import net.transcode001.creambox.TweetAdapter

import twitter4j.Twitter
import twitter4j.TwitterException


class LoadTimelineTask(private val mTwitter: Twitter, private val mTweetAdapter: TweetAdapter) : AsyncTask<Void, Void, List<twitter4j.Status>>() {

    override fun doInBackground(vararg params: Void): List<twitter4j.Status>? {
        try {
            return mTwitter.homeTimeline
        } catch (e: TwitterException) {
            Log.e("","Caused by Network Issue",e)
        }
        return null
    }

    override fun onPostExecute(result: List<twitter4j.Status>?) {
        if (result != null) {
            for (status in result) {
                mTweetAdapter.add(status)
            }
        }
    }
}