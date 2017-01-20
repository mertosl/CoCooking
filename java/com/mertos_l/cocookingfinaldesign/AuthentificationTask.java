package com.mertos_l.cocookingfinaldesign;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import org.json.JSONException;
import java.io.IOException;

class AuthentificationTask extends AsyncTask<Void, Void, String> {
    private final ProgressDialog dialog;
    DoRequest authRequest = new DoRequest();
    private String url_connect_user = "http://dev.cocooking.eu/api/users/auth/";
    private String url_register_user = "http://dev.cocooking.eu/api/users/";
    String values;
    private int i;

    AuthentificationTask(Context context, String values, int i) {
        this.values = values;
        this.i = i;
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
        String ret = null;
        try {
            if (i == 0) {
                ret = authRequest.doPostRequest(url_connect_user, values);
            } else {
                ret = authRequest.doPostRequest(url_register_user, values);
            }
            return ret;
        } catch (IOException e) {
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
