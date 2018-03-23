package net.transcode001.creambox

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter


open class UserTweetFragmentAdapter(fm:FragmentManager,id:Long): FragmentPagerAdapter(fm){
    private val PAGE_NUM =2
    val id:Long
    init{
        this.id = id
    }

    override fun getItem(position: Int): Fragment {
        val fragment:Fragment
        when(position){
            0 ->{
                val args =Bundle()
                val usf = UserStateFragment()
                args.putLong("userid",id)
                usf.arguments = args
                fragment = usf
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
        return PAGE_NUM
    }

    override fun getPageTitle(position: Int): CharSequence {
        var cs:CharSequence = "Profile"

        when(position){
            0 -> cs = "Profile"
            1 -> cs = "tweet"
        }
        return cs
    }
}