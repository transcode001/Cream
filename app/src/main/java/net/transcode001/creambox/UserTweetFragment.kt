package net.transcode001.creambox

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import kotlinx.android.synthetic.main.profile_user_tweet.*
import net.transcode001.creambox.asyncs.LoadUserTimeLineTask
import twitter4j.Twitter

open class UserTweetFragment: Fragment(){
    init {

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view :View = inflater!!.inflate(R.layout.profile_user_tweet,container,false)
        return view

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mTweetAdapter = TweetAdapter(context)

        /**
         * get User ID from UserProfile.java
         */


        val userId:Long = arguments.getLong("userid")
        /*
            profile_tweet_text uses instead of listview
         */
        profile_tweet_text.adapter = mTweetAdapter
        val mTwitter = TwitterUtils.getInstance(context)
        val task = LoadUserTimeLineTask(mTwitter,mTweetAdapter,userId)
        task.execute()
    }
}