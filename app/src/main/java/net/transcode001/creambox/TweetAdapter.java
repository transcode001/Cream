package net.transcode001.creambox;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

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
        private ViewHolder holder;

        public TweetAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.tweet_layout, null);
                TextView text = (TextView) convertView.findViewById(R.id.text);
                TextView name = (TextView) convertView.findViewById(R.id.name);
                TextView screenName = (TextView) convertView.findViewById(R.id.screen_name);
                TextView via=(TextView) convertView.findViewById(R.id.via);
                ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
                holder=new ViewHolder(icon,text,name,screenName,via);
                convertView.setTag(holder);
            }else{
                holder=(ViewHolder)convertView.getTag();
            }

            /*以前保持した画像があれば削除*/
            linearLayout = (LinearLayout)convertView.findViewById(R.id.media);
            if(linearLayout.getChildCount()>0) linearLayout.removeAllViews();

            Status item;
            if(getItem(position).isRetweet()){
                item=getItem(position).getRetweetedStatus();
                holder.getText().setTextColor(Color.rgb(0,100,0));
            }else{
                item = getItem(position);
                holder.getText().setTextColor(Color.BLACK);
            }

            /*ID表示*/
            holder.getName().setText(item.getUser().getName());
            holder.getScreenName().setText("@" + item.getUser().getScreenName());
            holder.getText().setText(item.getText());

            /*アイコン表示*/
            getUserIcon(item);

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
            holder.getVia().setText("via "+viaTexts[0]);

            return convertView;


        }

        private Bitmap getImage(String imageURL){
            Drawable d;
            InputStream is;
            Bitmap b;
            try{
                URL url = new URL(imageURL);
                Object o =url.getContent();
                //is =(InputStream) url.getContent();
                //d = Drawable.createFromStream(is,"");
                b = BitmapFactory.decodeResource((Resources)o,0);
                //is.close();
            }catch(MalformedURLException me){
                System.out.println(me.toString());
                b=null;
            }catch(IOException ioe){
                System.out.println(ioe.toString());
                b=null;
            }
            return b;

        }



        private void getUserIcon(twitter4j.Status status) {
            final twitter4j.Status userStatus = status;
            final ViewHolder viewHolder = holder;
            //final View convertView = view;
            //final String screenName = userScreenName;
            AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        url = new URL(userStatus.getUser().getProfileImageURL());
                        mStream = url.openStream();
                        bmp = BitmapFactory.decodeStream(mStream);
                        //imageView.setImageBitmap(bmp);
                        return true;
                    } catch (MalformedURLException e) {
                        Log.e("MalformedURLException", e.toString());
                        return false;
                    } catch (IOException e) {
                        Log.e("IOException", e.toString());
                        return false;
                    }
                }
                @Override
                protected void onPostExecute(Boolean bool) {
                    if (bool == Boolean.TRUE) {
                        viewHolder.getImage().setImageBitmap(bmp);
                        //IcoheUtils.setIcon(screenName,bmp);
                    }
                }
            };

            task.execute();

        }

}
