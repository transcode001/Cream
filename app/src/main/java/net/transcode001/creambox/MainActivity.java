package net.transcode001.creambox;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.transcode001.creambox.asyncs.LoadTimelineTask;
import net.transcode001.creambox.asyncs.ReloadTimelineTask;

import twitter4j.*;
import twitter4j.conf.Configuration;

//import android.support.design.widget.FloatingActionButton;

public class MainActivity extends AppCompatActivity {


    public Twitter mTwitter;
    public Configuration mConfiguration;
    public TweetAdapter mTweetAdapter;
    private ListView listView;
    private Handler mHandler;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        if (!TwitterUtils.hasAccessToken(getApplicationContext())) {
            Intent intent = new Intent(getApplicationContext(), Authorization.class);
            startActivity(intent);
            finish();
        } else {

            mTweetAdapter = new TweetAdapter(this);
            listView = (ListView) findViewById(R.id.listView_timeline);
            mHandler = new Handler();
            listView.setVisibility(View.GONE);
            mConfiguration = TwitterUtils.getConfigurationInstance(this);
            mTwitter = TwitterUtils.getInstance(this);
            mSwipeRefreshLayout = findViewById(R.id.refresh);
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    reloadTimeLine();
                }
            });

            listView.setAdapter(mTweetAdapter);
            /*
            FloatingActionButton fab = findViewById(R.id.float_icon);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(),TweetActivity.class);
                    startActivity(intent);
                }
            });
            */

            loadTimeLine();

            listView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private void loadTimeLine() {
        LoadTimelineTask task = new LoadTimelineTask(mTwitter,mTweetAdapter);
        task.execute();
    }

    private void reloadTimeLine() {
        ReloadTimelineTask task = new ReloadTimelineTask(mTwitter,mTweetAdapter);
        task.execute();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

}