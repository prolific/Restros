package com.fiktivo.restros;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;

public class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {
    private ImageView imageView;
    private Restro restro;

    public LoadImageTask(ImageView imageView, Restro restro) {
        this.imageView = imageView;
        this.restro = restro;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            Bitmap bitmap;
            if (restro.bitmap == null) {
                bitmap = BitmapFactory.decodeStream(new URL(restro.logoURL).openConnection().getInputStream());
                restro.bitmap = bitmap;
            } else
                bitmap = restro.bitmap;
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
