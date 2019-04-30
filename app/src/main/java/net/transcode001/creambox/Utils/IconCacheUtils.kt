package net.transcode001.creambox.Utils

import android.graphics.Bitmap
import android.widget.ListView

import java.util.HashMap

internal class IconCacheUtils {
    /* key -> screen name */
    private var cache: HashMap<String, Bitmap>? = null

    init {
        cache = HashMap()
    }


    fun getIcon(key: String): Bitmap? {
        return if (cache!!.containsKey(key)) cache!![key] else null
    }

    fun setIcon(key: String, bmp: Bitmap) {
        cache!![key] = bmp
    }
    
}
