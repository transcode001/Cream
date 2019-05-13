package net.transcode001.creambox

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


import twitter4j.Twitter
import twitter4j.TwitterException
import twitter4j.auth.AccessToken
import twitter4j.auth.RequestToken



class Authorization : Activity() {
    private var mRequestToken: RequestToken? = null
    private var mAccessToken: AccessToken? = null
    private var mTwitter: Twitter? = null
    private var et: EditText? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_layout)
        mTwitter = TwitterUtils.getInstance(this)
        et = findViewById<View>(R.id.input_help) as EditText
        findViewById<View>(R.id.btn_auth_pin).setOnClickListener {
            if (!et!!.text.toString().equals("")) {
                pin(et!!.text.toString())
            }
        }

        findViewById<View>(R.id.auth).setOnClickListener {

            //accessToTwitter()


            val url = access()

            if(!url.equals("")) startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            else showToast("認証に失敗しました")
        }

    }

    private fun accessToTwitter() {
        val task = object : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void): String? {
                try {
                    mRequestToken = mTwitter!!.oAuthRequestToken
                    return mRequestToken!!.authorizationURL
                } catch (te: TwitterException) {
                    Log.e("AuthorizationError", te.toString())
                    showToast("認証に失敗しました")
                    return null
                }

            }

            override fun onPostExecute(url: String?) {
                if (url != null) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
            }
        }
        task.execute()
    }



    @kotlinx.coroutines.ExperimentalCoroutinesApi
    private fun access():String{
        try{
            val getTask = GlobalScope.async {
                mRequestToken = mTwitter!!.oAuthRequestToken
                return@async mRequestToken!!.authenticationURL
            }

            return runBlocking {
                return@runBlocking getTask.await()
            }

        }catch (te:TwitterException){
            Log.e("AuthorizationError",te.toString())
        }
        return ""

    }

    private fun enterPin(accessToken: String) {
        val task = object : AsyncTask<String, Void, AccessToken>() {
            override fun doInBackground(vararg params: String): AccessToken? {
                try {
                    mAccessToken = mTwitter!!.getOAuthAccessToken(accessToken)
                    val sp = getSharedPreferences(mTwitter!!.screenName, Context.MODE_PRIVATE)
                    val edit = sp.edit()
                    edit.putString("token", mAccessToken!!.token)
                    edit.putString("token_secret", mAccessToken!!.tokenSecret)
                    edit.apply()
                    return mAccessToken

                } catch (e: TwitterException) {
                    if (e.statusCode == 401) {
                        Log.e("UnableToGetAccessCode", e.toString())
                        showToast("認証エラー\n一時的に利用できません")
                    } else {
                        Log.e("enterPinError", e.toString())
                        showToast("認証エラー\n一時的に利用できません")
                    }
                    return null

                }

            }

            override fun onPostExecute(accessToken: AccessToken?) {
                accessToken?.let { successAccessToken(it) } ?: Log.e("failToAuth", et!!.toString())
            }
        }
        task.execute()
    }

    private fun pin(accessToken: String) = runBlocking {
        try{
            val token = GlobalScope.async {
                mAccessToken = mTwitter!!.getOAuthAccessToken(accessToken)
                val sp = getSharedPreferences(mTwitter!!.screenName, Context.MODE_PRIVATE)
                val edit = sp.edit()
                edit.putString("token", mAccessToken!!.token)
                edit.putString("token_secret", mAccessToken!!.tokenSecret)
                edit.apply()
                return@async mAccessToken
            }.await()

            successAccessToken(token)

        }catch (e:TwitterException){
            if (e.statusCode == 401) {
                Log.e("UnableToGetAccessCode", e.toString())
                showToast("認証エラー\n一時的に利用できません")
            } else {
                Log.e("enterPinError", e.toString())
                showToast("認証エラー\n一時的に利用できません")
            }
        }
    }

    private fun successAccessToken(nAccessToken: AccessToken?) {
        if(nAccessToken == null){
          showToast("認証に失敗しました")
        }
        TwitterUtils.storeAccessToken(this, nAccessToken!!)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }


}
