package net.transcode001.creambox;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.util.concurrent.ExecutionException;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterUtils {

    public static Twitter getInstance(Context context){
        String consumerKey = context.getString(R.string.consumerKey);
        String consumerSecret = context.getString(R.string.consumerSecret);

        TwitterFactory tf = new TwitterFactory();
        Twitter twitter = tf.getInstance();
        twitter.setOAuthConsumer(consumerKey,consumerSecret);

        if (hasAccessToken(context)) {
            twitter.setOAuthAccessToken(loadAccessToken(context));
        }

        return twitter;

    }

    public static Configuration getConfigurationInstance(Context context){
        String consumerKey = context.getString(R.string.consumerKey);
        String consumerSecret = context.getString(R.string.consumerSecret);
        String accessToken = context.getString(R.string.accessToken);
        String accessTokenSec = context.getString(R.string.accessTokenSec);

        return new ConfigurationBuilder().setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret).setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSec).build();
    }

    public static Twitter getAccessTokenInstance(Twitter mTwitter,Context context){
        String accessToken = context.getString(R.string.accessToken);
        String tokenSecret = context.getString(R.string.accessTokenSec);
        AccessToken at = new AccessToken(accessToken,tokenSecret);
        mTwitter.setOAuthAccessToken(at);
        return mTwitter;
    }

    /**
     * AccessTokenを永続的に保存
     * getSharedPreference("設定データの名前",ファイルの操作モード)
     *
     */
    public static void storeAccessToken(Context context, AccessToken accessToken){
        SharedPreferences preferences = context.getSharedPreferences("pref_name",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", accessToken.getToken());
        editor.putString("token_secret", accessToken.getTokenSecret());
        editor.apply();
    }

        public static AccessToken loadAccessToken(Context context) {
            final Context mContext = context;
            SharedPreferences preferences = mContext.getSharedPreferences("pref_name", Context.MODE_PRIVATE);
            String token = preferences.getString("token", null);
            String tokenSecret = preferences.getString("token_secret", null);
            if (token != null && tokenSecret != null) {
                return new AccessToken(token, tokenSecret);
            } else {
                return null;
            }
        }

    public static boolean hasAccessToken(Context context) {
        return loadAccessToken(context) != null;
    }

}
