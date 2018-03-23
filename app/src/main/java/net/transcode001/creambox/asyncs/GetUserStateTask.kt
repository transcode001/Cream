package net.transcode001.creambox.asyncs

import android.os.AsyncTask
import android.widget.TextView
import twitter4j.Twitter
import twitter4j.TwitterException


open class GetUserStateTask(mTwitter: Twitter,userId:Long,textView: TextView):AsyncTask<Void,Void,String>(){
    val mTwitter:Twitter
    val userId:Long
    val textView:TextView
    init {
        this.mTwitter = mTwitter
        this.userId = userId
        this.textView = textView
    }

    override fun doInBackground(vararg p0: Void?): String? {

        try{
            val user = mTwitter.users().showUser(userId)
            return user.description
        }catch(te: TwitterException){

        }
        return null
    }

    override fun onPostExecute(result: String?) {
        textView.text = result
    }
}