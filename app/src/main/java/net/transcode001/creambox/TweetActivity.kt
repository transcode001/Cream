package net.transcode001.creambox


import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.transcode001.creambox.util.TwitterUtils

import twitter4j.Twitter
import twitter4j.TwitterException


class TweetActivity : AppCompatActivity() {
    private var mInputText: EditText? = null
    private var mTwitter: Twitter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tweet)

        mTwitter = TwitterUtils.getInstance(this)
        mInputText = findViewById<View>(R.id.input_text) as EditText
        findViewById<View>(R.id.action_tweet).setOnClickListener { tweet() }
    }
/*
    private fun tweet() {
        val task = object : AsyncTask<String, Void, Boolean>() {
            override fun doInBackground(vararg params: String): Boolean {
                try {
                    mTwitter!!.updateStatus(params[0])
                    return true
                } catch (e: TwitterException) {
                    e.printStackTrace()
                    return false
                }

            }

            override fun onPostExecute(result: Boolean) {
                if (result) {
                    showToast("ツイートが完了しました")
                    finish()
                } else {
                    showToast("ツイートに失敗しました")
                }
            }
        }
        task.execute(mInputText!!.text.toString())
    }
*/
    private fun tweet(){
        GlobalScope.launch {
            try{
               mTwitter!!.updateStatus(mInputText!!.text.toString())
                showToast("ツイートが完了しました")
                finish()
            }catch(e:TwitterException){
                showToast("ツイートに失敗しました")
            }
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}