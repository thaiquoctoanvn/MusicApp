package com.example.musicapp;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.squareup.picasso.Picasso;

import java.io.IOException;

public class LoadBitmapForNotification extends AsyncTask<String, Void, Bitmap> {

    Bitmap image;
    @Override
    protected Bitmap doInBackground(String... strings) {
        try {
            image = Picasso.get().load(strings[0]).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        CreateNotification.bitmap = bitmap;
    }
}
