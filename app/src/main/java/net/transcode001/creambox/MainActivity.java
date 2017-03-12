package net.transcode001.creambox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import twitter4j.*;
import twitter4j.conf.Configuration;


public class MainActivity extends ListActivity {


    public Twitter mTwitter;
    private TwitterStream mTwitterStream;
    public Configuration mConfiguration;
    public TweetAdapter mTweetAdapter;
    private Handler mHandler;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!TwitterUtils.hasAccessToken(getApplicationContext())) {
            Intent intent = new Intent(this, Authorization.class);
            startActivity(intent);
            finish();
        } else {

            mTweetAdapter = new TweetAdapter(getApplicationContext());
            setListAdapter(mTweetAdapter);
            mHandler = new Handler();
            mConfiguration = TwitterUtils.getConfigurationInstance(this);
            mTwitterStream = new TwitterStreamFactory(mConfiguration).getInstance();
            mTwitter = TwitterUtils.getInstance(this);




            //長押しの定義はListActivityではサポート外なので
            //ツイート長押しの処理を定義
            listView = getListView();

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    LayoutInflater li = (LayoutInflater) getApplicationContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    View content = li.inflate(R.layout.popup_tweet,null);
                    //場所をセット
                    view.setVerticalScrollbarPosition(position);

                    TextView tv = (TextView) view.findViewById(R.id.text);
                    TextView name = (TextView) view.findViewById(R.id.name);

                    builder.setView(content);

                    builder.setTitle(name.getText());
                    builder.setMessage(tv.getText());
                    builder.create().show();

                    return true;
                }
            });

            reloadTimeLine();
            streamTimeLine();

        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_tweet:
                Intent intent = new Intent(this, TweetActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_search:
                Intent intent_menu = new Intent(this,TweetSearch.class);
                startActivity(intent_menu);
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

    private void reloadTimeLine() {
        AsyncTask<Void, Void, List<twitter4j.Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    return mTwitter.getHomeTimeline();
                } catch (TwitterException e) {
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

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


    private void streamTimeLine() {

        UserStreamListener listener = new UserStreamListener() {
            @Override
            public void onDeletionNotice(long l, long l1) {

            }

            @Override
            public void onFriendList(long[] longs) {

            }

            @Override
            public void onFavorite(User user, User user1, Status status) {

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
                builder.setContentText("You've got Direct Mail from @"+directMessage.getSender().getScreenName());
                builder.setShowWhen(true);
                //Activityに移動(未確認)
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("hoge"));
                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),RESULT_OK,intent, PendingIntent.FLAG_ONE_SHOT);
                builder.setContentIntent(contentIntent);
                builder.setAutoCancel(true);

                final NotificationCompat.Builder mBuilder=builder;
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
    }


    private class TweetAdapter extends ArrayAdapter<twitter4j.Status> {
        private LayoutInflater mInflater;
        private URL url;
        private InputStream mStream;
        private Bitmap bmp;
        private ImageView imageView;
        private Status item;

        public TweetAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.tweet_layout, null);
            }


            //imageView = (ImageView) convertView.findViewById(R.id.icon);
            //Boolean getView=Boolean.FALSE;

            //getUserIcon();


            item = getItem(position);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            name.setText(item.getUser().getName());
            TextView screenName = (TextView) convertView.findViewById(R.id.screen_name);
            screenName.setText("@" + item.getUser().getScreenName());
            TextView text = (TextView) convertView.findViewById(R.id.text);
            text.setText(item.getText());
            SmartImageView sImageView = (SmartImageView) convertView.findViewById(R.id.icon);
            sImageView.setImageUrl(item.getUser().getProfileImageURL());
            TextView via=(TextView) convertView.findViewById(R.id.via);

            String[] viaText = item.getSource().split("<*>",-1);
            String[] viaTexts = viaText[1].split("<",0);

            via.setText("via "+viaTexts[0]);

            return convertView;


        }

        private Boolean getUserIcon() {
            AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        url = new URL(item.getUser().getProfileImageURL());
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
                        imageView.setImageBitmap(bmp);
                    }
                }
            };
            task.execute();
            return true;
        }

    }

}