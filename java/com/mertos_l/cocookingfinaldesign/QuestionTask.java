package com.mertos_l.cocookingfinaldesign;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

class QuestionTask extends AsyncTask<Void, Void, JSONArray> {
    private static String url_qr = "http://dev.cocooking.eu/api/users/questions/";
    private DoRequest doRequest = new DoRequest();
    private String uid;

    QuestionTask(String uid) {
        this.uid = uid;
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        try {
            String response = null;
            response = doRequest.doGetRequest(url_qr + uid);
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("status") && jsonObject.getString("status").equals("ok")) {
                return jsonObject.optJSONArray("questions");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

