package net.transcode001.creambox

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
import android.util.AndroidRuntimeException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import net.transcode001.creambox.Utils.IconCacheUtils
import net.transcode001.creambox.asyncs.GetImageTask

import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL

import twitter4j.MediaEntity
import twitter4j.Status
import twitter4j.User


class TweetAdapter(context: Context) : ArrayAdapter<twitter4j.Status>(context, android.R.layout.simple_list_item_1) {
    private val mInflater: LayoutInflater
    private var holder: HoldView? = null
    private val utils: IconCacheUtils

    init {
        mInflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        utils = IconCacheUtils()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        if (convertView == null) {
            holder = HoldView()
            convertView = mInflater.inflate(R.layout.tweet_layout, null)

            holder!!.text = convertView!!.findViewById<View>(R.id.text) as TextView
            holder!!.name = convertView.findViewById<View>(R.id.name) as TextView
            holder!!.screenName = convertView.findViewById<View>(R.id.screen_name) as TextView
            holder!!.via = convertView.findViewById<View>(R.id.via) as TextView
            holder!!.icon = convertView.findViewById<View>(R.id.icon) as ImageView
            holder!!.retweetStatus = convertView.findViewById<View>(R.id.retweet_status) as TextView

            convertView.tag = holder

        } else {
            holder = convertView.tag as HoldView
        }

        /*invisible view*/
        holder!!.icon!!.visibility = View.GONE
        holder!!.retweetStatus!!.visibility = View.GONE
        /*


            //以前保持した画像があれば削除
            linearLayout = (LinearLayout) view.findViewById(R.id.media);
            if(linearLayout.getChildCount()>0) linearLayout.removeAllViews();
            */

        val item: Status?
        if (getItem(position)!!.isRetweet) {
            holder!!.retweetStatus!!.visibility = View.VISIBLE
            holder!!.retweetStatus!!.text = getItem(position)!!.user.screenName + " retweeted"
            item = getItem(position)!!.retweetedStatus
        } else {
            item = getItem(position)
        }
        holder!!.text!!.setTextColor(Color.WHITE)

        /*ID表示*/
        holder!!.name!!.text = item!!.user.name
        holder!!.screenName!!.text = "@" + item.user.screenName

        holder!!.text!!.text = item.text
        holder!!.text!!.setTextColor(Color.WHITE)

        holder!!.icon!!.tag = item.user.screenName
        holder!!.icon!!.setOnClickListener {
            try {
                val intent = Intent(context, UserProfile::class.java)
                if (getItem(position)!!.isRetweet) {
                    intent.putExtra("Status", getItem(position)!!.retweetedStatus.user.id)
                    println()
                } else
                    intent.putExtra("Status", getItem(position)!!.user.id)
                context.startActivity(intent)
            } catch (are: AndroidRuntimeException) {
                println(are.cause.toString())
            }
        }


        /*get user icon*/
        getUserIcon(item, holder!!.icon)


        /*Media取得*/
        /*
            MediaEntity[] mediaEntity = item.getExtendedMediaEntities();
            if(mediaEntity.length>0) {
                for (MediaEntity media : mediaEntity) {
                    ImageView mediaView = new ImageView(getContext());
                    System.out.println("resource:"+media.getMediaURL());
                    Uri uri = Uri.parse(media.getMediaURL());
                    mediaView.setImageURI(uri);
                    LinearLayout.LayoutParams params =
                            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMarginEnd(15);
                    mediaView.setLayoutParams(params);
                    linearLayout.addView(mediaView,params);
                }
            }
            */

        /*via表示*/
        val viaText = item.source.split("<*>".toRegex()).toTypedArray()
        val viaTexts = viaText[1].split("<".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        holder!!.via!!.text = "via " + viaTexts[0]

        /*アイコン表示*/
        holder!!.icon!!.visibility = View.VISIBLE

        return convertView
    }

    private fun getUserIcon(status: Status, icon: ImageView?) {
        val getImageTask = GetImageTask(icon!!, status, utils)
        getImageTask.execute()
    }
}
