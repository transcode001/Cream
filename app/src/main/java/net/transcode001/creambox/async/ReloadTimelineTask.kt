package net.transcode001.creambox.async

import android.os.AsyncTask
import net.transcode001.creambox.TweetAdapter
import twitter4j.Paging
import twitter4j.Twitter
import twitter4j.TwitterException
import java.util.*

open class ReloadTimelineTask(mTwitter: Twitter?,mTweetAdapter: TweetAdapter?):AsyncTask<Void,Void,List<twitter4j.Status>>(){
    val mTwitter:Twitter?
    val mTweetAdapter:TweetAdapter?
    init {
        this.mTwitter = mTwitter
        this.mTweetAdapter = mTweetAdapter
    }

    override fun doInBackground(vararg params:Void):List<twitter4j.Status>?{
        try {
            val s: twitter4j.Status = mTweetAdapter!!.getItem(0)
            val p = Paging()
            p.setSinceId(s.id)
            return mTwitter!!.getHomeTimeline(p)
        }catch (te:TwitterException){

        }
        return null

    }

    override fun onPostExecute(result: List<twitter4j.Status>?) {
        if(result!=null){
            Collections.reverse(result)
            for (res in result){
                mTweetAdapter!!.insert(res,0)
            }
        }
    }

}