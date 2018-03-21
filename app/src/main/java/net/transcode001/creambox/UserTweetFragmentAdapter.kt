package net.transcode001.creambox

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter


open class UserTweetFragmentAdapter(fm:FragmentManager,id:Long): FragmentPagerAdapter(fm){
    val PAGE_NUM =2
    val id:Long
    init{
        this.id = id
    }

    override fun getItem(position: Int): Fragment {
        if(position==1){
            val args = Bundle()
            val utf = UserTweetFragment()
            args.putLong("userid",id)
            utf.arguments = args
            return utf
        }else return TFragment()
    }

    override fun getCount(): Int {
        return PAGE_NUM
    }
}