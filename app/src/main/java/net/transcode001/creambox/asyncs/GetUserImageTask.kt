package net.transcode001.creambox.asyncs

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.os.AsyncTask
import android.widget.ImageView
import twitter4j.Twitter
import twitter4j.TwitterException
import java.net.URL

open class GetUserImageTask(mTwitter: Twitter,userId:Long,icon: ImageView): AsyncTask<Void,Void, Bitmap>(){
    val mTwitter:Twitter
    val userId:Long
    val icon:ImageView
    init {
        this.mTwitter = mTwitter
        this.userId = userId
        this.icon = icon
    }

    override fun doInBackground(vararg p0: Void?): Bitmap? {
        try {
            val status = mTwitter.showUser(userId)
            val iconUrl = URL(status.profileImageURL)
            val iStream = iconUrl.openStream()
            val image =BitmapFactory.decodeStream(iStream)
            iStream.close()
            return image
        }catch(te:TwitterException){

        }
        return null
    }

    override fun onPostExecute(result: Bitmap?) {
        icon.setImageBitmap(result)
    }

}