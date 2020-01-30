package net.transcode001.creambox.asyncs

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView

import net.transcode001.creambox.Utils.IconCacheUtils

import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL


internal class GetImageTask(private val imageView: ImageView, private val status: twitter4j.Status, private val utils: IconCacheUtils) : AsyncTask<Void, Void, Bitmap>() {
    private val tag: String

    init {
        tag = imageView.tag.toString()
    }

    override fun doInBackground(vararg params: Void): Bitmap? {
        val pic = utils.getIcon(status.user.screenName)
        if (pic != null) return pic

        try {
            val url = URL(status.user.profileImageURL)
            val mStream = url.openStream()
            val bmp = BitmapFactory.decodeStream(mStream)
            mStream.close()
            return bmp

        } catch (me: MalformedURLException) {
            me.printStackTrace()
            return null
        } catch (ioe: IOException) {
            ioe.printStackTrace()
            return null
        }

    }

    override fun onPostExecute(bmp: Bitmap?) {
        if (bmp != null) {
            if (tag == this.imageView.tag) {
                imageView.setImageBitmap(bmp)
                utils.setIcon(status.user.screenName, bmp)
            }
        }
    }
}
