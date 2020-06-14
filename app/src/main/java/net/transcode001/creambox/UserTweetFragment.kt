package net.transcode001.creambox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.profile_user_tweet.*
import net.transcode001.creambox.async.LoadUserTimeLineTask
import net.transcode001.creambox.util.TwitterUtils

open class UserTweetFragment: Fragment(){
    init {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.profile_user_tweet,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mTweetAdapter = context?.let { TweetAdapter(it) }

        /**
         * get User ID from UserProfile.java
         */
        val userId:Long = arguments!!.getLong("userid")
        /*
            use profile_tweet_text instead of listview
         */
        profile_tweet_text.adapter = mTweetAdapter
        val mTwitter = context?.let { TwitterUtils.getInstance(it) }
        val task = LoadUserTimeLineTask(mTwitter, mTweetAdapter, userId)
        task.execute()

        /*
        * define swipe refresh
        */
        //profile_tweet_refresh.isRefreshing = false
//        profile_tweet_refresh.setOnRefreshListener {
//            val tasks = LoadUserTimeLineTask(mTwitter,mTweetAdapter,userId)
//            tasks.execute()
//            profile_tweet_refresh.isRefreshing = false
//        }

    }
}