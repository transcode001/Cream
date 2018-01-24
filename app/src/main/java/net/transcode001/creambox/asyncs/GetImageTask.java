package net.transcode001.creambox.asyncs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


public class GetImageTask extends AsyncTask<Void,Void,Bitmap> {
    private ImageView imageView;
    private String tag;
    private twitter4j.Status status;

    public GetImageTask(ImageView imageView,twitter4j.Status status){
        this.imageView = imageView;
        this.status = status;
        tag = imageView.getTag().toString();
    }

    @Override
    protected Bitmap doInBackground(Void... params){
        try{
            URL url = new URL(status.getUser().getProfileImageURL());
            InputStream mStream = url.openStream();
            Bitmap bmp = BitmapFactory.decodeStream(mStream);
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
            }
        }
    }
}
