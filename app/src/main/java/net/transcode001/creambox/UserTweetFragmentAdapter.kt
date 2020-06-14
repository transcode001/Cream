package net.transcode001.creambox

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


open class UserTweetFragmentAdapter(fm: FragmentManager, id:Long): FragmentPagerAdapter(fm){
    private val totalPageNum = 2
    private val userProf = 0
    private val userTwt = 1
    val id:Long
    init{
        this.id = id
    }

    override fun getItem(position: Int): Fragment {
        val fragment:Fragment
        when(position){
            userProf ->{
                val args = Bundle()
                val usFragment = UserStateFragment()
                args.putLong("userid",id)
                usFragment.arguments = args
                fragment = usFragment
            }

            else ->{
                val args =Bundle()
                val utf = UserTweetFragment()
                args.putLong("userid",id)
                utf.arguments = args
                fragment = utf
            }
        }
        return fragment
    }

    override fun getCount(): Int {
        return totalPageNum
    }

    override fun getPageTitle(position: Int): CharSequence {
        var cs:CharSequence = "Profile"

        when(position){
            userProf -> cs = "profile"
            userTwt -> cs = "tweets"
        }
        return cs
    }
}