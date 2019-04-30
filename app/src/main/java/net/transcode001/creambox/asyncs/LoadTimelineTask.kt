package net.transcode001.creambox.asyncs

import android.os.AsyncTask
import android.widget.Toast

import net.transcode001.creambox.MainActivity
import net.transcode001.creambox.TweetAdapter

import twitter4j.Status
import twitter4j.Twitter
import twitter4j.TwitterException


class LoadTimelineTask(private val mTwitter: Twitter?, private val mTweetAdapter: TweetAdapter?) : AsyncTask<Void, Void, List<twitter4j.Status>>() {

    override fun doInBackground(vararg params: Void): List<twitter4j.Status>? {
        try {
            return mTwitter!!.homeTimeline
        } catch (e: TwitterException) {
            if (e.isCausedByNetworkIssue) {
                println()
            }
            e.printStackTrace()
        }

        return null
    }

    override fun onPostExecute(result: List<twitter4j.Status>?) {
        if (result != null) {
            //mTweetAdapter.clear();
            for (status in result) {
                mTweetAdapter!!.add(status)
            }
            //getListView().setSelection(0);
        }
    }


}