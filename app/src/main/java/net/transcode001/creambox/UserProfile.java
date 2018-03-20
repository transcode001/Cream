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
//import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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


public class UserProfile extends AppCompatActivity {

    private TweetAdapter mTweetAdapter;
    private Twitter mTwitter;
    private ListView listView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_layout);
=
        int position = getIntent().getIntExtra("position",0);
        Status content = mTweetAdapter.getItem(position);

        mTweetAdapter = new TweetAdapter(getApplicationContext());
        listView = (ListView) findViewById(R.id.user_profile_tweet);
        listView.setAdapter(mTweetAdapter);
        mTwitter = TwitterUtils.getInstance(getApplicationContext());
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_profile);
        final twitter4j.Status con = content;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTweetAdapter.clear();
                        loadTimeLine(con);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                },1000);
            }
        });

        if(content!=null) setUserProfileVIew(content);
        loadTimeLine(content);

    }


    private void loadTimeLine(twitter4j.Status content) {
        final twitter4j.Status con = content;
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
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void setUserProfileVIew(twitter4j.Status status){
        SmartImageView mSmartImageView = (SmartImageView) findViewById(R.id.user_profile_header);
        SmartImageView lSmartImageView = (SmartImageView) findViewById(R.id.user_profile_icon);
        lSmartImageView.setImageUrl(status.getUser().getProfileImageURL());
        mSmartImageView.setImageUrl(status.getUser().getProfileBannerURL());
    }

}
