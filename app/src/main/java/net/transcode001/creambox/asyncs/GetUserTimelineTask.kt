package net.transcode001.creambox.asyncs

import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import net.transcode001.creambox.TweetAdapter
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.TwitterException

open class GetUserTimelineTask(mTwitter: Twitter, mTweetAdapter: TweetAdapter,intent:Intent): AsyncTask<Void,Void,List<twitter4j.Status>>(){
    val mTwitter: Twitter
    val mTweetAdapter: TweetAdapter
    val intent : Intent
    init{
        this.mTwitter = mTwitter
        this.mTweetAdapter = mTweetAdapter
        this.intent = intent
    }

    override fun doInBackground(vararg params: Void?): List<twitter4j.Status> {
        val userId: Long = intent.getLongExtra("Status", 0)
        try {
            return mTwitter.getUserTimeline(userId)
        }catch (te: TwitterException) {
            if (te.isCausedByNetworkIssue) {
                //showToast("ネットワークに接続されていません")
            }
            Log.d("user timeline", te.toString())
            if (te.statusCode == 429) {
                //showToast("API規制です")
            }
        } catch (ne: NullPointerException) {
            Log.d("user time line",ne.toString())
        }

        return emptyList()
    }

    override fun onPostExecute(result:List<twitter4j.Status>){
        if(!result.isEmpty()){
            for(res in result) mTweetAdapter.add(res)
        }
    }
}