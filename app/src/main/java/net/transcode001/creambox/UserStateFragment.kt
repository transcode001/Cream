package net.transcode001.creambox

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.profile_user_info.*
import net.transcode001.creambox.asyncs.GetUserStateTask

open class UserStateFragment: Fragment(){
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.profile_user_info,container,false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mTwitter = TwitterUtils.getInstance(context)
        val userId = arguments.getLong("userid")
        GetUserStateTask(mTwitter,userId,profile_user_statue).execute()
    }
}