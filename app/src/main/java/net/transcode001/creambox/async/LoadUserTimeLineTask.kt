package net.transcode001.creambox.async

import android.os.AsyncTask
import net.transcode001.creambox.TweetAdapter
import twitter4j.Twitter
import twitter4j.TwitterException

open class LoadUserTimeLineTask(mTwitter:Twitter?,mTweetAdapter:TweetAdapter?,userId:Long):AsyncTask<Void,Void,List<twitter4j.Status>>(){
    val mTwitter:Twitter?
    val mTweetAdapter: TweetAdapter?
    val userId:Long
    init {
        this.mTwitter = mTwitter
        this.mTweetAdapter = mTweetAdapter
        this.userId = userId
    }

    override fun doInBackground(vararg p0: Void?): List<twitter4j.Status>? {
        try {
            return mTwitter!!.getUserTimeline(userId)
        }catch (te:TwitterException){}
        return null
    }

    override fun onPostExecute(result: List<twitter4j.Status>?) {
        if(result!=null){
            for (res in result)
                mTweetAdapter!!.add(res)
        }
    }
}