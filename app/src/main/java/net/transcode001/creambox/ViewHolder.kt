package net.transcode001.creambox

import android.widget.ImageView
import android.widget.TextView

class ViewHolder(imageView:ImageView,textTweet:TextView,userName:TextView,
                 userScreenName:TextView,userVia:TextView){
    val image: ImageView = imageView
    val text: TextView = textTweet
    val name:TextView = userName
    val screenName: TextView = userScreenName
    val via:TextView = userVia

}