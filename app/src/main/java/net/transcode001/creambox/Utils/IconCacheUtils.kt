package net.transcode001.creambox.Utils

import android.graphics.Bitmap
import android.widget.ListView

import java.util.HashMap

class IconCacheUtils {
    /* key -> screen name */

    init {
        cache = HashMap()
    }

    companion object {
        private var cache: HashMap<String, Bitmap>? = null

        fun getIcon(key: String): Bitmap? {
            return if (cache!!.containsKey(key)) cache!![key] else null
        }

        fun setIcon(key: String, bmp: Bitmap) {
            cache!![key] = bmp
        }

        fun cacheClear() {
            cache = null
            cache = HashMap()
        }
    }

}
