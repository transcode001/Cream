package net.transcode001.creambox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
//import kotlinx.android.synthetix,
import net.transcode001.creambox.util.TwitterUtils

open class UserStateFragment: Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.profile_user_info,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mTwitter = context?.let { TwitterUtils.getInstance(it) }
        val userId = arguments?.getLong("userid")
        //profile_user_statue = GetUserStateTask(mTwitter,userId).get()
    }
}