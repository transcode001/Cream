package net.transcode001.creambox

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask

import java.util.concurrent.ExecutionException

import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import twitter4j.conf.Configuration
import twitter4j.conf.ConfigurationBuilder

object TwitterUtils {

    fun getInstance(context: Context): Twitter {
        val consumerKey = context.getString(R.string.consumerKey)
        val consumerSecret = context.getString(R.string.consumerSecret)

        val tf = TwitterFactory()
        val twitter = tf.instance
        twitter.setOAuthConsumer(consumerKey, consumerSecret)

        if (hasAccessToken(context)) {
            twitter.oAuthAccessToken = loadAccessToken(context)
        }

        return twitter

    }

    fun getConfigurationInstance(context: Context): Configuration {
        val consumerKey = context.getString(R.string.consumerKey)
        val consumerSecret = context.getString(R.string.consumerSecret)
        val accessToken = context.getString(R.string.accessToken)
        val accessTokenSec = context.getString(R.string.accessTokenSec)

        return ConfigurationBuilder().setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret).setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSec).build()
    }

    fun getAccessTokenInstance(mTwitter: Twitter, context: Context): Twitter {
        val accessToken = context.getString(R.string.accessToken)
        val tokenSecret = context.getString(R.string.accessTokenSec)
        val at = AccessToken(accessToken, tokenSecret)
        mTwitter.oAuthAccessToken = at
        return mTwitter
    }

    /**
     * AccessTokenを永続的に保存
     * getSharedPreference("設定データの名前",ファイルの操作モード)
     *
     */
    fun storeAccessToken(context: Context, accessToken: AccessToken) {
        val preferences = context.getSharedPreferences("pref_name",
                Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("token", accessToken.token)
        editor.putString("token_secret", accessToken.tokenSecret)
        editor.apply()
    }

    fun loadAccessToken(context: Context): AccessToken? {
        val preferences = context.getSharedPreferences("pref_name", Context.MODE_PRIVATE)
        val token = preferences.getString("token", null)
        val tokenSecret = preferences.getString("token_secret", null)
        return if (token != null && tokenSecret != null) {
            AccessToken(token, tokenSecret)
        } else {
            null
        }
    }

    fun hasAccessToken(context: Context): Boolean {
        return loadAccessToken(context) != null
    }

}
