package net.transcode001.creambox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.transcode001.creambox.Utils.IconCacheUtils;
import net.transcode001.creambox.asyncs.GetImageTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.User;


public class TweetAdapter extends ArrayAdapter<twitter4j.Status>{
        private LayoutInflater mInflater;
        private HoldView holder;
        private IconCacheUtils utils;

        public TweetAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            utils = new IconCacheUtils();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                holder = new HoldView();
                convertView = mInflater.inflate(R.layout.tweet_layout, null);

                holder.text = (TextView) convertView.findViewById(R.id.text);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.screenName = (TextView) convertView.findViewById(R.id.screen_name);
                holder.via=(TextView) convertView.findViewById(R.id.via);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.retweetStatus = (TextView)convertView.findViewById(R.id.retweet_status);

                convertView.setTag(holder);

            }else{
                holder=(HoldView)convertView.getTag();
            }

            /*invisible view*/
            holder.icon.setVisibility(View.GONE);
            holder.retweetStatus.setVisibility(View.GONE);
            /*


            //以前保持した画像があれば削除
            linearLayout = (LinearLayout) view.findViewById(R.id.media);
            if(linearLayout.getChildCount()>0) linearLayout.removeAllViews();
            */

            Status item;
            if(getItem(position).isRetweet()){
                holder.retweetStatus.setVisibility(View.VISIBLE);
                holder.retweetStatus.setText(getItem(position).getUser().getScreenName()+" retweeted");
                item=getItem(position).getRetweetedStatus();
            }else{
                item = getItem(position);
            }
            holder.text.setTextColor(Color.WHITE);

            /*ID表示*/
            holder.name.setText(item.getUser().getName());
            holder.screenName.setText("@" + item.getUser().getScreenName());

            holder.text.setText(item.getText());
            holder.text.setTextColor(Color.WHITE);

            holder.icon.setTag(item.getUser().getScreenName());
            holder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(getContext(), UserProfile.class);
                        if (getItem(position).isRetweet()) {
                            intent.putExtra("Status", getItem(position).getRetweetedStatus().getUser().getId());
                            System.out.println();
                        }else
                            intent.putExtra("Status",getItem(position).getUser().getId());
                        getContext().startActivity(intent);
                    }catch(AndroidRuntimeException are){
                        System.out.println(are.getCause().toString());
                    }
                }
            });



            /*get user icon*/
            getUserIcon(item, holder.icon);



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
            GetImageTask getImageTask = new GetImageTask(icon,status,utils);
            getImageTask.execute();
        }
}
