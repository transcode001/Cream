package net.transcode001.creambox.Utils;

import android.graphics.Bitmap;
import android.widget.ListView;

import java.util.HashMap;

public class IconCacheUtils {
    private static HashMap<String,Bitmap> cache = new HashMap<>();
    /* key -> screen name */

    public static Bitmap getIcon(String key){
        return (cache.containsKey(key)) ? cache.get(key) : null;
    }

    public static void setIcon(String key, Bitmap bmp){
        cache.put(key,bmp);
    }

    public static void cacheClear(){
        cache = null;
        cache = new HashMap<>();
    }

}
