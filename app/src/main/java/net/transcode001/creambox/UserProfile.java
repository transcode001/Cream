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
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.Objects;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.URLEntity;


public class UserProfile extends Activity {

    Boolean alertDialogEnable;
    private TweetAdapter mTweetAdapter;
    private Twitter mTwitter;
    private ListView listView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Status content;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_layout);


        mTweetAdapter = new TweetAdapter(getApplicationContext());
        listView = (ListView) findViewById(R.id.user_profile_tweet);
        listView.setAdapter(mTweetAdapter);
        mTwitter = TwitterUtils.getInstance(getApplicationContext());
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_profile);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTweetAdapter.clear();
                        loadTimeLine();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                },1000);
            }
        });

        //TabLayout tabLayout=(TabLayout) findViewById(R.id.user_profile_tab);
        //tabLayout.addTab(tabLayout.newTab().setText("test1"));
        //tabLayout.addTab(tabLayout.newTab().setText("test2"));


        setTweetPopup();
        if(content!=null) setUserProfileVIew(content);
        loadTimeLine();
    }


    private void loadTimeLine() {

        AsyncTask<Void, Void, List<Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            Intent i=getIntent();
            final long userId=i.getLongExtra("Status",0);
            //final twitter4j.Status tweet=m;
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    return mTwitter.getUserTimeline(userId);
                } catch (TwitterException e) {
                    if(e.isCausedByNetworkIssue()){
                        showToast("ネットワークに接続されていません");
                    }
                    Log.d("user timeline",e.toString());
                    if(e.getStatusCode()==429){
                        showToast("API規制です");
                    }
                } catch(NullPointerException e){

                }
                return null;
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    //mTweetAdapter.clear();
                    for (twitter4j.Status status : result) {
                        if(content==null) content = status;
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;

                Intent intent = new Intent(getApplicationContext(),UserProfile.class);
                if(mTweetAdapter.getItem(pos).isRetweet())
                    intent.putExtra("Status",mTweetAdapter.getItem(pos).getRetweetedStatus().getUser().getId());
                else
                    intent.putExtra("Status",mTweetAdapter.getItem(pos).getUser().getId());
                startActivity(intent);
            }
        });

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

    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void setUserProfileVIew(twitter4j.Status status){
        SmartImageView mSmartImageView = (SmartImageView) findViewById(R.id.user_profile_header);
        SmartImageView lSmartImageView = (SmartImageView) findViewById(R.id.user_profile_icon);
        lSmartImageView.setImageUrl(status.getUser().getProfileImageURL());
        mSmartImageView.setImageUrl(status.getUser().getProfileBannerURL());
    }

}
