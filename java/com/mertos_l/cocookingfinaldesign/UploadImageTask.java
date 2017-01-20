package com.mertos_l.cocookingfinaldesign;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class UploadImageTask extends AsyncTask<Void, Void, String> {
    private final ProgressDialog dialog;
    private static String url_photo_user = "http://dev.cocooking.eu/api/pictures/user/";
    private static String url_photo_meal = "http://dev.cocooking.eu/api/pictures/meal/";
    DoRequest doRequest = new DoRequest();
    private Context mContext;
    private String uid;
    private String api_key;
    private String name;
    private File file;
    private int index;
    private String mealid;

    UploadImageTask(Context context, String uid, String api_key, File file, String name, int index, String mealid) {
        this.mContext = context;
        this.uid = uid;
        this.api_key = api_key;
        this.file = file;
        this.name = name;
        this.index = index;
        if (index == 1)
            this.mealid = mealid;
        this.dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("Chargement...");
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String nonce = new NonceGenerator(6).nextString();
            String url = "?uid=" + uid + "&nonce=" + nonce + "&sign=" + new HmacSha1().hmacSha1(new String((index == 0 ? "POST:/pictures/user/:" : "/pictures/meal/") + nonce), api_key);
            Log.i("url", (index == 0 ? url_photo_user : (url_photo_meal + mealid + "/")) + url);
            return doRequest.doPostImage((index == 0 ? url_photo_user : (url_photo_meal + mealid + "/")) + url, file, name);
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        dialog.dismiss();
    }
}
