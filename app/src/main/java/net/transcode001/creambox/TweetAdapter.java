package net.transcode001.creambox;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.transcode001.creambox.Utils.IconCacheUtils;
import net.transcode001.creambox.asyncs.GetImageTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import twitter4j.MediaEntity;
import twitter4j.Status;


public class TweetAdapter extends ArrayAdapter<twitter4j.Status>{
        private LayoutInflater mInflater;
        private URL url;
        private InputStream mStream;
        private Bitmap bmp;
        private ImageView imageView;
        private ListView lv;
        private LinearLayout linearLayout;
        private HoldView holder;

        public TweetAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new HoldView();
                convertView = mInflater.inflate(R.layout.tweet_layout, null);
                holder.text = (TextView) convertView.findViewById(R.id.text);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.screenName = (TextView) convertView.findViewById(R.id.screen_name);
                holder.via=(TextView) convertView.findViewById(R.id.via);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);
                //view=convertView;
            }else{
                holder=(HoldView) convertView.getTag();
            }

            /*invisible view*/
            holder.icon.setVisibility(View.GONE);

            /*


            //以前保持した画像があれば削除
            linearLayout = (LinearLayout) view.findViewById(R.id.media);
            if(linearLayout.getChildCount()>0) linearLayout.removeAllViews();
            */

            Status item;
            if(getItem(position).isRetweet()){
                item=getItem(position).getRetweetedStatus();
                holder.text.setTextColor(Color.rgb(0,100,0));
            }else{
                item = getItem(position);
                holder.text.setTextColor(Color.BLACK);
            }

            /*ID表示*/
            holder.name.setText(item.getUser().getName());
            holder.screenName.setText("@" + item.getUser().getScreenName());

            holder.text.setText(item.getText());
            holder.icon.setTag(item.getUser().getScreenName());

            /*get user icon*/
            Bitmap bmp = IconCacheUtils.getIcon(item.getUser().getScreenName());
            getUserIcon(item,holder.icon);


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
            String[] viaText = item.getSource().split("<*>",-1);
            String[] viaTexts = viaText[1].split("<",0);
            holder.via.setText("via "+viaTexts[0]);

            /*アイコン表示*/
            holder.icon.setVisibility(View.VISIBLE);

            return convertView;
        }

        private void getUserIcon(twitter4j.Status status,ImageView icon) {
            GetImageTask getImageTask = new GetImageTask(holder.icon,status);
            getImageTask.execute();
        }

}
