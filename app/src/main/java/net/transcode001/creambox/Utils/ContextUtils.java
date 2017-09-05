package net.transcode001.creambox.Utils;


import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import net.transcode001.creambox.MainActivity;
import net.transcode001.creambox.R;
import net.transcode001.creambox.TwitterUtils;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class ContextUtils {
 public Context context;
 public ContextUtils(Context context){
     this.context = context;
 }
 public Context getApplicationContext(){
  return context;
 }

}
