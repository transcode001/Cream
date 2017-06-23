package net.transcode001.creambox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.app.ActionBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import twitter4j.*;
import twitter4j.conf.Configuration;


public class MainActivity extends Activity {


    public Twitter mTwitter;
    public Configuration mConfiguration;
    public TweetAdapter mTweetAdapter;
    private TwitterStream storeTwittterStreamInstance;
    private Handler mHandler;
    private ListView listView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Boolean userStreamEnable=false;
    private Boolean alertDialogEnable=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!TwitterUtils.hasAccessToken(getApplicationContext())) {
            Intent intent = new Intent(getApplicationContext(), Authorization.class);
            startActivity(intent);
            finish();
        } else {

            mTweetAdapter = new TweetAdapter(getApplicationContext());
            listView = (ListView)findViewById(R.id.listView_timeline);
            listView.setAdapter(mTweetAdapter);
            mHandler = new Handler();
            mConfiguration = TwitterUtils.getConfigurationInstance(getApplicationContext());
            mTwitter = TwitterUtils.getInstance(getApplicationContext());

            //長押しの定義はListActivityではサポート外なので
            //ツイート長押しの処理を定義

            loadTimeLine();
            streamTimeLine();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_tweet:
                Intent intent = new Intent(getApplicationContext(), TweetActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_search:
                Intent intent_menu = new Intent(getApplicationContext(),TweetSearch.class);
                startActivity(intent_menu);
                return true;
            case R.id.reload_timeline:
                reloadTimeLine();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        super.onCreateOptionsMenu(menu);
        ActionBar actionBar=getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setIcon(null);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);

        return true;
    }

    private void setTweetPopup(){
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

                builder.setTitle(name.getText());
                builder.setMessage(tv.getText());
                dialog=builder.create();
                dialog.show();

                //リツイート
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
                                dialog.dismiss();
                            }
                        };
                        task.execute();
                    }
                });

                //お気に入り
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
                                    dialog.dismiss();
                                }
                            };
                            task.execute();
                        }
                    });
                }



                return true;
            }
        });
    }

    private void loadTimeLine() {

        AsyncTask<Void, Void, List<twitter4j.Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    return mTwitter.getHomeTimeline();
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

    private void reloadTimeLine() {

        AsyncTask<Void, Void, List<twitter4j.Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    twitter4j.Status s = mTweetAdapter.getItem(mTweetAdapter.getCount()-1);
                    Paging p = new Paging();
                    p.setMaxId(s.getId());
                    return mTwitter.getHomeTimeline(p);
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
                    int position=mTweetAdapter.getCount();
                    result.remove(0);
                    Collections.reverse(result);

                    for (twitter4j.Status status : result) {
                        mTweetAdapter.insert(status,position);
                    }
                    //getListView().setSelection(0);
                } else {
                    showToast("タイムラインの取得に失敗しました");
                }
            }
        };
        task.execute();
    }

    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void streamTimeLine() {
        if(!userStreamEnable) {
            TwitterStream mTwitterStream = new TwitterStreamFactory(mConfiguration).getInstance();
            UserStreamListener listener = new UserStreamListener() {
                @Override
                public void onDeletionNotice(long l, long l1) {

                }

                @Override
                public void onFriendList(long[] longs) {

                }

                @Override
                public void onFavorite(User user, User user1, Status status) {
                    String userName;
                    //NotificationBuilder構築
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                    try {
                        userName = mTwitter.getScreenName();
                        if(userName.equals(user1.getScreenName())) {

                            //アイコンを設定
                            builder.setSmallIcon(R.drawable.images);
                            //テキスト設定
                            builder.setContentTitle("CreamBox");
                            builder.setContentText("Your tweet favorited by @" + user.getScreenName());
                            builder.setShowWhen(true);
                            //Activityに移動(未確認)
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("hoge"));
                            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), RESULT_OK, intent, PendingIntent.FLAG_ONE_SHOT);
                            builder.setContentIntent(contentIntent);
                            builder.setAutoCancel(true);

                            final NotificationCompat.Builder mBuilder = builder;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
                                    manager.notify(3, mBuilder.build());
                                }
                            });
                        }
                    }catch(TwitterException e){

                    }


                }

                @Override
                public void onUnfavorite(User user, User user1, Status status) {

                }

                @Override
                public void onFollow(User user, User user1) {

                }

                @Override
                public void onUnfollow(User user, User user1) {

                }

                @Override
                public void onDirectMessage(DirectMessage directMessage) {
                    //NotificationBuilder構築
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                    //アイコンを設定
                    builder.setSmallIcon(R.drawable.images);
                    //テキスト設定
                    builder.setContentTitle("CreamBox");
                    builder.setContentText("You've got Direct Mail from @" + directMessage.getSender().getScreenName());
                    builder.setShowWhen(true);
                    //Activityに移動(未確認)
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("hoge"));
                    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), RESULT_OK, intent, PendingIntent.FLAG_ONE_SHOT);
                    builder.setContentIntent(contentIntent);
                    builder.setAutoCancel(true);

                    final NotificationCompat.Builder mBuilder = builder;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
                            manager.notify(3, mBuilder.build());
                        }
                    });

                }

                @Override
                public void onUserListMemberAddition(User user, User user1, UserList userList) {

                }

                @Override
                public void onUserListMemberDeletion(User user, User user1, UserList userList) {

                }

                @Override
                public void onUserListSubscription(User user, User user1, UserList userList) {

                }

                @Override
                public void onUserListUnsubscription(User user, User user1, UserList userList) {

                }

                @Override
                public void onUserListCreation(User user, UserList userList) {

                }

                @Override
                public void onUserListUpdate(User user, UserList userList) {

                }

                @Override
                public void onUserListDeletion(User user, UserList userList) {

                }

                @Override
                public void onUserProfileUpdate(User user) {

                }

                @Override
                public void onUserSuspension(long l) {

                }

                @Override
                public void onUserDeletion(long l) {

                }

                @Override
                public void onBlock(User user, User user1) {

                }

                @Override
                public void onUnblock(User user, User user1) {

                }

                @Override
                public void onRetweetedRetweet(User user, User user1, Status status) {

                }

                @Override
                public void onFavoritedRetweet(User user, User user1, Status status) {

                }

                @Override
                public void onQuotedTweet(User user, User user1, Status status) {

                }

                @Override
                public void onStatus(Status status) {
                    final String tweets = "@" + status.getUser().getScreenName() + ":" + status.getText() + "\n";
                    final twitter4j.Status tweet = status;
                    System.out.println(tweets);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mTweetAdapter.insert(tweet, 0);
                            //getListView().setSelection(0);
                        }
                    });


                }

                @Override
                public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

                }

                @Override
                public void onTrackLimitationNotice(int i) {

                }

                @Override
                public void onScrubGeo(long l, long l1) {

                }

                @Override
                public void onStallWarning(StallWarning stallWarning) {

                }

                @Override
                public void onException(Exception e) {

                }
            };
            //インスタンスを設定
            mTwitterStream.addListener(listener);
            //UserStreamを開始
            mTwitterStream.user();
            userStreamEnable = true;
            storeTwittterStreamInstance = mTwitterStream;
        }else{
            storeTwittterStreamInstance.cleanUp();
            storeTwittterStreamInstance.shutdown();
            userStreamEnable = false;
        }

    }

    private class TweetAdapter extends ArrayAdapter<twitter4j.Status> {
        private LayoutInflater mInflater;
        private twitter4j.Status item;
        private ImageView imageView;
        private RelativeLayout relativeLayout;

        public TweetAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            convertView = mInflater.inflate(R.layout.tweet_layout, null);

            int URLEntryCount=0;
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

            MediaEntity[] mediaEntities=item.getExtendedMediaEntities();

                switch(mediaEntities.length){
                    case 1:
                        convertView=mInflater.inflate(R.layout.tweet_layout_pic1,null);
                        SmartImageView mSmartImageView=(SmartImageView) convertView.findViewById(R.id.pic1_image);
                        try {
                            mSmartImageView.setImageUrl(mediaEntities[0].getMediaURL());
                        }catch(NullPointerException e){
                            showToast("画像の取得に失敗しました");
                        }
                        break;


                    default:
                        final twitter4j.Status mItem = item;
                        text = (TextView) convertView.findViewById(R.id.text);
                        TextView name = (TextView) convertView.findViewById(R.id.name);
                        name.setText(item.getUser().getName());
                        TextView screenName = (TextView) convertView.findViewById(R.id.screen_name);
                        screenName.setText("@" + item.getUser().getScreenName());

                        text.setText(item.getText());



                        SmartImageView sImageView = (SmartImageView) convertView.findViewById(R.id.icon);
                        sImageView.setImageUrl(item.getUser().getProfileImageURL());
                        sImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //TwitterUtils.storeStatus(mItem);
                                Intent intent= new Intent(getApplicationContext(),UserProfile.class);
                                //putExtra(name,value) value<-primitive のみ？
                                intent.putExtra("Status",getItem(position).getUser().getId());
                                startActivity(intent);
                            }
                        });
                        TextView via=(TextView) convertView.findViewById(R.id.via);

                        String[] viaText = item.getSource().split("<*>",-1);
                        String[] viaTexts = viaText[1].split("<",0);


                        via.setText("via "+viaTexts[0]);
                        break;
                }

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
                        mHandler.post(new Runnable() {
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

}