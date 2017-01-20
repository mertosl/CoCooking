package com.mertos_l.cocookingfinaldesign;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

class DetailsRepasTask extends AsyncTask<Void, Void, JSONObject> {
    private static String url = "http://dev.cocooking.eu/api/meal/";
    private DoRequest doRequest = new DoRequest();
    private String id;

    DetailsRepasTask(String id) {
        this.id = id;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            String response = doRequest.doGetRequest(url + id);
            return new JSONObject(response);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
