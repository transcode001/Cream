package net.transcode001.creambox.asyncs;

import android.os.AsyncTask;
import android.widget.Toast;

import net.transcode001.creambox.TweetAdapter;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class LoadTimelineTask extends AsyncTask<Void,Void,List<twitter4j.Status>> {

    private Twitter mTwitter;
    private TweetAdapter mTweetAdapter;

    public LoadTimelineTask(Twitter mTwitter, TweetAdapter mTweetAdapter){
        this.mTwitter = mTwitter;
        this.mTweetAdapter = mTweetAdapter;
    }

    @Override
    protected List<twitter4j.Status> doInBackground(Void... params) {
        try {
            return mTwitter.getHomeTimeline();
        } catch (TwitterException e) {
            if(e.isCausedByNetworkIssue()){
                System.out.println("failed");
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
        }
    }


}