package net.transcode001.creambox;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
//import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
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

import net.transcode001.creambox.asyncs.UserProfileViewTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.URLEntity;


public class UserProfile extends AppCompatActivity {

    private Twitter mTwitter;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user_main);

        mTwitter = TwitterUtils.getInstance(getApplicationContext());
        Intent i = getIntent();
        Long userId = i.getLongExtra("Status",0);
        ImageView image_header = findViewById(R.id.profile_header);
        ImageView image_icon = findViewById(R.id.profile_icon);
        setUserProfileView(userId,image_header,image_icon);
        setFragment(userId);

    }



    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    synchronized private void setUserProfileView(long userId,ImageView header,ImageView icon) {
        final long ID = userId;
        UserProfileViewTask task = new UserProfileViewTask(mTwitter,ID,header,icon);
        task.execute();
    }

    synchronized private void setFragment(Long userId){
        ViewPager viewPager = findViewById(R.id.profile_view_pager);
        FragmentManager fm = getSupportFragmentManager();

        UserTweetFragmentAdapter utfa = new UserTweetFragmentAdapter(fm,userId);
        viewPager.setAdapter(utfa);
    }

}
