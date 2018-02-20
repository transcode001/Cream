package net.transcode001.creambox;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.app.ActionBar;
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

import java.util.Collections;
import java.util.List;

import twitter4j.*;
import twitter4j.conf.Configuration;

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
        setContentView(R.layout.activity_main);

        if (!TwitterUtils.hasAccessToken(getApplicationContext())) {
            Intent intent = new Intent(getApplicationContext(), Authorization.class);
            startActivity(intent);
            finish();
        } else {

            mTweetAdapter = new TweetAdapter(getApplicationContext());
            listView = (ListView) findViewById(R.id.listView_timeline);
            mHandler = new Handler();
            listView.setVisibility(View.GONE);
            mConfiguration = TwitterUtils.getConfigurationInstance(getApplicationContext());
            mTwitter = TwitterUtils.getInstance(getApplicationContext());
            mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    reloadTimeLine();
                }
            });
            {
                listView.setAdapter(mTweetAdapter);
            }


            loadTimeLine();

            listView.setVisibility(View.VISIBLE);
        }


    }
    /*
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

    }*/

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

/*
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
*/


    private void setTweetPopup(){


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                final int pos = position;

                Intent intent = new Intent(getApplicationContext(),UserProfile.class);
                if(mTweetAdapter.getItem(pos).isRetweet())
                    intent.putExtra("Status",mTweetAdapter.getItem(pos).getRetweetedStatus().getUser().getId());
                else
                    intent.putExtra("Status",mTweetAdapter.getItem(pos).getUser().getId());
                startActivity(intent);
                */
                showToast("hoge");
            }
        });


        /*
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
        */
    }


    private void loadTimeLine() {
        LoadTimelineTask task = new LoadTimelineTask(mTwitter,mTweetAdapter);
        task.execute();
    }

    private void reloadTimeLine() {
        /*
        AsyncTask<Void, Void, List<twitter4j.Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    twitter4j.Status s = mTweetAdapter.getItem(0);
                    Paging p = new Paging();
                    p.setSinceId(s.getId());
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
                    System.out.println("get!");
                    //一番↓の位置
                    int position=mTweetAdapter.getCount();
                    //先頭部分が最後に取得した部分と重複するので削除
                    //result.remove(0);
                    //ツイート順に並び替える
                    Collections.reverse(result);

                    for (twitter4j.Status status : result) {
                        System.out.println(status.getText());
                        mTweetAdapter.insert(status,0);
                    }
                    //getListView().setSelection(0);
                } else {
                    showToast("タイムラインの取得に失敗しました");
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        };
        */
        ReloadTimelineTask task = new ReloadTimelineTask(mTwitter,mTweetAdapter);
        task.execute();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

}