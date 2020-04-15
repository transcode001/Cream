package net.transcode001.creambox

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.AndroidRuntimeException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import net.transcode001.creambox.util.IconCacheUtils
import net.transcode001.creambox.async.GetImageTask

import twitter4j.Status


class TweetAdapter(context: Context) : ArrayAdapter<twitter4j.Status>(context, android.R.layout.simple_list_item_1) {
    var mInflater: LayoutInflater
    private val utils: IconCacheUtils

    init {
        mInflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        utils = IconCacheUtils()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
//        var convertView = convertView
//        if (convertView == null) {
//            convertView = mInflater.inflate(R.layout.tweet_layout, null)
//
//            val text = convertView.findViewById<View>(R.id.text) as TextView
//            val name = convertView.findViewById<View>(R.id.name) as TextView
//            val screenName = convertView.findViewById<View>(R.id.screen_name) as TextView
//            val via = convertView.findViewById<View>(R.id.via) as TextView
//            val icon = convertView.findViewById<View>(R.id.icon) as ImageView
//            val retweetStatus = convertView.findViewById<View>(R.id.retweet_status) as TextView
//
//            holder = HoldView(text,name,screenName,via,icon,retweetStatus)
//            convertView.tag = holder
//
//        } else {
//            holder = convertView.tag as HoldView
//        }

        var convertViewTemp = convertView
        val holder = getViewContent(convertView)
        convertViewTemp!!.tag = holder

        /* make view invisible */
        holder.icon.visibility = View.GONE
        holder.retweetStatus.visibility = View.GONE
        /*


            //以前保持した画像があれば削除
            linearLayout = (LinearLayout) view.findViewById(R.id.media);
            if(linearLayout.getChildCount()>0) linearLayout.removeAllViews();
            */

        val item: Status?
        if (getItem(position)!!.isRetweet) {
            holder.retweetStatus.visibility = View.VISIBLE
            holder.retweetStatus.text = getItem(position)!!.user.screenName + " retweeted"
            item = getItem(position)!!.retweetedStatus
        } else {
            item = getItem(position)
        }
        holder.text.setTextColor(Color.WHITE)

        /*ID表示*/
        holder.name.text = item!!.user.name
        holder.screenName.text = "@" + item.user.screenName

        holder.text.text = item.text
        holder.text.setTextColor(Color.WHITE)

        holder.icon.tag = item.user.screenName
        holder.icon.setOnClickListener {
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
        getUserIcon(item, holder.icon)


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

        /*show via*/
        val viaText = item.source.split("<*>".toRegex()).toTypedArray()
        val viaTexts = viaText[1].split("<".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        holder.via.text = "via " + viaTexts[0]

        /*show icon*/
        holder.icon.visibility = View.VISIBLE

        return convertViewTemp
    }

    private fun getUserIcon(status: Status, icon: ImageView?) {
        val getImageTask = GetImageTask(icon!!, status, utils)
        getImageTask.execute()
    }

    private fun getViewContent(convertView:View?): HoldView {
        var convertViewTemp = convertView
        if (convertViewTemp == null) {
            convertViewTemp = mInflater.inflate(R.layout.tweet_layout, null)

            val text = convertViewTemp.findViewById<View>(R.id.text) as TextView
            val name = convertViewTemp.findViewById<View>(R.id.name) as TextView
            val screenName = convertViewTemp.findViewById<View>(R.id.screen_name) as TextView
            val via = convertViewTemp.findViewById<View>(R.id.via) as TextView
            val icon = convertViewTemp.findViewById<View>(R.id.icon) as ImageView
            val retweetStatus = convertViewTemp.findViewById<View>(R.id.retweet_status) as TextView

            return HoldView(text,name,screenName,via,icon,retweetStatus)

        } else {
            return convertViewTemp.tag as HoldView
        }
    }
}
