package com.mertos_l.cocookingfinaldesign;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {
    private String src;
    private final ProgressDialog dialog;

    DownloadImageTask(String src, Context context) {
        this.src = src;
        this.dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("Chargement...");
        dialog.show();
    }

    @Override
    protected Bitmap doInBackground(Void... p) {
        Bitmap bmp = null;
        URL url = null;
        try {
            url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bmp = BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmp;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        dialog.dismiss();
    }
}
