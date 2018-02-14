package net.transcode001.creambox.asyncs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import net.transcode001.creambox.Utils.IconCacheUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


public class GetImageTask extends AsyncTask<Void,Void,Bitmap> {
    private ImageView imageView;
    private String tag;
    private twitter4j.Status status;
    private IconCacheUtils utils;

    public GetImageTask(ImageView imageView,twitter4j.Status status,IconCacheUtils utils){
        this.imageView = imageView;
        this.status = status;
        tag = imageView.getTag().toString();
        this.utils = utils;
    }

    @Override
    protected Bitmap doInBackground(Void... params){
        Bitmap pic = utils.getIcon(status.getUser().getScreenName());
        if(pic!=null) return pic;

        try{
            URL url = new URL(status.getUser().getMiniProfileImageURL());
            InputStream mStream = url.openStream();
            Bitmap bmp = BitmapFactory.decodeStream(mStream);
            mStream.close();
            return bmp;

        }catch(MalformedURLException me){
            me.printStackTrace();
            return null;
        }catch(IOException ioe){
            ioe.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bmp){
        if(bmp!=null){
            if(tag.equals(this.imageView.getTag())){
                imageView.setImageBitmap(bmp);
                utils.setIcon(status.getUser().getScreenName(),bmp);
            }
        }
    }
}
