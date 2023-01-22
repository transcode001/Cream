package net.transcode001.creambox

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager

import net.transcode001.creambox.async.UserProfileViewTask
import net.transcode001.creambox.util.TwitterUtils

import twitter4j.Twitter


class UserProfile : AppCompatActivity() {

    private var mTwitter: Twitter? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_user_main)

        mTwitter = TwitterUtils.getInstance(applicationContext)
        val i = intent
        val userId = i.getLongExtra("Status", 0)
        val image_header = findViewById<ImageView>(R.id.profile_header)
        val image_icon = findViewById<ImageView>(R.id.profile_icon)
        setUserProfileView(userId, image_header, image_icon)
        setFragment(userId)
    }

    @Synchronized
    private fun setUserProfileView(userId: Long, header: ImageView, icon: ImageView) {
        val task = UserProfileViewTask(mTwitter!!, userId, header, icon)
        task.execute()
    }

    @Synchronized
    private fun setFragment(userId: Long?) {
        val viewPager = findViewById<ViewPager>(R.id.profile_view_pager)
        val fm = supportFragmentManager

        val utfa = UserTweetFragmentAdapter(fm, userId!!)
        viewPager.adapter = utfa
    }

    private fun showToast(text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }

}
