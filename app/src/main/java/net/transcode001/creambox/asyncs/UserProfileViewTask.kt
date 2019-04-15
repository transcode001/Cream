package net.transcode001.creambox.asyncs

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import com.loopj.android.image.SmartImageView
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.TwitterException
import twitter4j.User
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL

open class UserProfileViewTask(mTwitter: Twitter, userId:Long, header:ImageView,icon:ImageView)
                                                            :AsyncTask<Void,Void,List<Bitmap>>(){
    val mTwitter:Twitter
    val userId:Long
    val header:ImageView
    val icon:ImageView
    init{
        this.mTwitter = mTwitter
        this.userId = userId
        this.header = header
        this.icon = icon
    }

    override fun doInBackground(vararg p0: Void?): List<Bitmap>? {
        try{
            System.out.println(userId)
            val status: User = mTwitter.showUser(userId)
            val iconUrl = URL(status.profileImageURL)
            val headerUrl = URL(status.profileBannerURL)
            val iStream: InputStream = iconUrl.openStream()
            val iBmp = BitmapFactory.decodeStream(iStream)
            iStream.close()
            val hStream = headerUrl.openStream()
            val hBmp = BitmapFactory.decodeStream(hStream)
            hStream.close()
            var list = listOf<Bitmap>(iBmp,hBmp)
            return list

        }catch(te: TwitterException) {
            System.out.println(te.toString())
        }catch(mue:MalformedURLException){
            /*
            * プロフィールなし？
            * */
        }
        return listOf<Bitmap>()
    }

    override fun onPostExecute(result: List<Bitmap>?){
        if(result!!.size==2) {
            icon.setImageBitmap(result!!.get(0))
            header.setImageBitmap(result!!.get(1))
        }
    }

}