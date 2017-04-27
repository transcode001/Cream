package net.transcode001.creambox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.URLEntity;


public class UserProfile extends Activity {

    Boolean alertDialogEnable;
    private TweetAdapter mTweetAdapter;
    private Twitter mTwitter;
    private ListView listView;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_layout);

        SmartImageView mSmartImageView = (SmartImageView) findViewById(R.id.user_profile_header);
        mSmartImageView.setImageUrl(TwitterUtils.getStatus().getUser().getProfileBackgroundImageUrlHttps());
        mTweetAdapter = new TweetAdapter(getApplicationContext());
        listView = (ListView) findViewById(R.id.user_profile_tweet);
        listView.setAdapter(mTweetAdapter);
        mTwitter = TwitterUtils.getInstance(getApplicationContext());
        loadTimeLine();
    }

    private void loadTimeLine() {

        AsyncTask<Void, Void, List<Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    return mTwitter.getUserTimeline(TwitterUtils.getStatus().getId());
                } catch (TwitterException e) {
                    if(e.isCausedByNetworkIssue()){
                        showToast("ネットワークに接続されていません");
                    }
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    //mTweetAdapter.clear();
                    for (twitter4j.Status status : result) {
                        mTweetAdapter.add(status);
                    }
                    //getListView().setSelection(0);
                } else {
                    showToast("タイムラインの取得に失敗しました");
                }
            }
        };
        task.execute();
    }

    private void setTweetPopup(){
        new ListView(getApplicationContext()).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
                final AlertDialog dialog;
                LayoutInflater li = (LayoutInflater) getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View content = li.inflate(R.layout.popup_tweet,null);
                //場所をセット
                view.setVerticalScrollbarPosition(position);

                builder.setView(content);
                TextView tv = (TextView) view.findViewById(R.id.text);
                TextView name = (TextView) view.findViewById(R.id.name);
                alertDialogEnable=false;
                final long tweetId = mTweetAdapter.getItem(position).getId();
                Button retweetButton = (Button) content.findViewById(R.id.button_retweet_tweet);
                Button favoriteButton = (Button) content.findViewById(R.id.button_favorite_tweet);
                retweetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AsyncTask<Void,Void,Boolean> task= new AsyncTask<Void, Void, Boolean>() {
                            @Override
                            protected Boolean doInBackground(Void... voids) {
                                try{
                                    mTwitter.retweetStatus(tweetId);
                                    return true;
                                }catch(TwitterException e){
                                    return false;
                                }
                            }
                            @Override
                            protected void onPostExecute(Boolean bool){
                                if(bool) showToast("リツイートしました");
                                else showToast("リツイートに失敗しました\nリクエストを実行できません");
                                //dialog.dismiss();
                            }
                        };
                        task.execute();
                    }
                });
                if(mTweetAdapter.getItem(position).isFavorited()){
                    //お気に入り登録
                    favoriteButton.setText("unfavorited");
                    favoriteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AsyncTask<Void,Void,Boolean> task= new AsyncTask<Void, Void, Boolean>() {
                                @Override
                                protected Boolean doInBackground(Void... voids) {
                                    try{
                                        mTwitter.destroyFavorite(tweetId);
                                        return true;
                                    }catch(TwitterException e){
                                        return false;
                                    }

                                }
                                @Override
                                protected void onPostExecute(Boolean bool){
                                    if(bool) showToast("お気に入りを解除しました");
                                    else showToast("リクエストを実行できません");
                                    alertDialogEnable=true;
                                }
                            };
                            task.execute();
                        }
                    });
                }else {
                    //お気に入り登録
                    favoriteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                                @Override
                                protected Boolean doInBackground(Void... voids) {
                                    try {
                                        mTwitter.createFavorite(tweetId);
                                        return true;
                                    } catch (TwitterException e) {
                                        return false;
                                    }
                                }
                                @Override
                                protected void onPostExecute(Boolean bool) {
                                    if (bool) showToast("お気に入りに登録しました");
                                    else showToast("お気に入り登録に失敗しました\nリクエストを実行できません");
                                    alertDialogEnable=true;
                                }
                            };
                            task.execute();
                        }
                    });
                }

                builder.setTitle(name.getText());
                builder.setMessage(tv.getText());
                dialog=builder.create();
                dialog.show();

                return true;
            }
        });
    }

    private class TweetAdapter extends ArrayAdapter<Status> {
        private LayoutInflater mInflater;
        private Status item;
        private ImageView imageView;
        private RelativeLayout relativeLayout;
        public TweetAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.tweet_layout, null);
            }

            //Boolean getView=Boolean.FALSE;

            TextView text = (TextView) convertView.findViewById(R.id.text);
            if(getItem(position).isRetweet()){
                item=getItem(position).getRetweetedStatus();
                text.setTextColor(Color.rgb(0,100,0));
            }else{
                item = getItem(position);
                text.setTextColor(Color.BLACK);
                //imageView = (ImageView)convertView.findViewById(R.id.icon);
                //getUserIcon();
            }

            if(!item.getURLEntities().equals(0)){
                URLEntity[] urlEntities = item.getURLEntities();
            }



            TextView name = (TextView) convertView.findViewById(R.id.name);
            name.setText(item.getUser().getName());
            name.setTextColor(Color.BLACK);
            TextView screenName = (TextView) convertView.findViewById(R.id.screen_name);
            screenName.setText("@" + item.getUser().getScreenName());
            screenName.setTextColor(Color.BLACK);
            text.setText(item.getText());

            SmartImageView sImageView = (SmartImageView) convertView.findViewById(R.id.icon);
            sImageView.setImageUrl(item.getUser().getProfileImageURL());
            sImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TwitterUtils.storeStatus(item);
                    Intent intent= new Intent(getApplicationContext(),UserProfile.class);
                    startActivity(intent);
                }
            });
            TextView via=(TextView) convertView.findViewById(R.id.via);
            via.setTextColor(Color.BLACK);
            String[] viaText = item.getSource().split("<*>",-1);
            String[] viaTexts = viaText[1].split("<",0);

            via.setText("via "+viaTexts[0]);

            setTweetPopup();

            return convertView;
            
        }

        private Boolean getUserIcon() {
            AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        URL url = new URL(item.getUser().getProfileImageURL());
                        InputStream mStream = url.openStream();
                        final Bitmap bmp = BitmapFactory.decodeStream(mStream);
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bmp);
                            }
                        });
                        return true;
                    } catch (MalformedURLException e) {
                        Log.e("MalformedURLException", e.toString());
                        return false;
                    } catch (IOException e) {
                        Log.e("IOException", e.toString());
                        return false;
                    }

                }

            };
            task.execute();
            return true;
        }

    }
    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

}
