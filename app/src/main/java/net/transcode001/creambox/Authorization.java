package net.transcode001.creambox;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;



public class Authorization extends Activity{
    private RequestToken mRequestToken;
    private AccessToken mAccessToken;
    private Twitter mTwitter;
    private EditText et;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_layout);
        mTwitter=TwitterUtils.getInstance(this);
        et = (EditText)findViewById(R.id.input_help);
        findViewById(R.id.btn_auth_pin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et.getText().toString().equals("")) {
                    enterPin(et.getText().toString());
                }
            }
        });

        findViewById(R.id.auth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accessToTwitter();
            }
        });

    }

    private void accessToTwitter() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    mRequestToken = mTwitter.getOAuthRequestToken();
                    return mRequestToken.getAuthorizationURL();
                } catch (TwitterException te) {
                    Log.e("AuthorizationError", te.toString());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String url) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        };
        task.execute();


    }

    private void enterPin(final String accessToken){
        AsyncTask<String,Void,AccessToken> task = new AsyncTask<String,Void, AccessToken>() {
            @Override
            protected AccessToken doInBackground(String... params) {
                try {
                    mAccessToken = mTwitter.getOAuthAccessToken(accessToken);
                    SharedPreferences sp = getSharedPreferences("pref_name", MODE_PRIVATE);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("token", mAccessToken.getToken());
                    edit.putString("token_secret", mAccessToken.getTokenSecret());
                    edit.apply();
                    return mAccessToken;

                } catch (TwitterException e) {
                    if(e.getStatusCode()==401){
                        Log.e("UnableToGetAccessCode",e.toString());
                        showToast("認証エラー\n一時的に利用できません");
                    }else {
                        Log.e("enterPinError", e.toString());
                        showToast("認証エラー\n一時的に利用できません");
                    }
                    return null;

                }
            }

            @Override
            protected void onPostExecute(AccessToken accessToken) {
                if(accessToken!=null){
                    successAccessToken(accessToken);
                }else{
                    Log.e("failToAuth", et.toString());
                }
            }
        };
        task.execute();
    }

    private void successAccessToken(AccessToken nAccessToken){
        TwitterUtils.storeAccessToken(this,nAccessToken);
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


}
