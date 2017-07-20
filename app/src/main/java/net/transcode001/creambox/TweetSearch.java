package net.transcode001.creambox;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class TweetSearch extends Activity {

    private TweetAdapter mAdapter;
    private Twitter mTwitter;
    private EditText et;
    private twitter4j.Query mQuery;
    private ListView listView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_main);
        listView = (ListView)findViewById(R.id.listView1);
        mAdapter = new TweetAdapter(this);
        listView.setAdapter(mAdapter);
        mTwitter=TwitterUtils.getInstance(this);
        et = (EditText)findViewById(R.id.word_search);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_search);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!et.getText().toString().equals("")) {

                            mQuery.setQuery(et.getText().toString());
                            System.out.println("go to searchWord\n");
                            searchWord(mQuery);
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                },3000);
            }
        });


        mQuery=new twitter4j.Query();
        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et.getText().toString().equals("")) {

                    mQuery.setQuery(et.getText().toString());
                    System.out.println("go to searchWord\n");
                    searchWord(mQuery);
                }
            }
        });

    }

    private void searchWord(twitter4j.Query query){
        final twitter4j.Query mquery=query;
        mAdapter.clear();
        //System.out.println("Before asyncTask\n");
        final Handler mHandler = new Handler();
        AsyncTask<Void,Void,QueryResult> task=new AsyncTask<Void, Void, QueryResult>() {
            @Override
            protected QueryResult doInBackground(Void... voids) {
                try{
                    //System.out.println("hoge");
                    final QueryResult result = mTwitter.search(mquery);
                    //System.out.println("Insert query result\n");
                    //System.out.println("Insert results\n");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            for(twitter4j.Status status : result.getTweets()){
                                mAdapter.insert(status,0);
                                System.out.println(result.getTweets().toString());
                            }
                        }
                    });

                }catch(TwitterException e){
                    Log.e("Failed to search method",e.toString());
                }

                return null;
            }
        };

        task.execute();



    }





}