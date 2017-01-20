package com.mertos_l.cocookingfinaldesign;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LocationRequestTask extends AsyncTask<Void, Void, JSONObject> {
    String link_get_location = "https://www.googleapis.com/geolocation/v1/geolocate?key=";
    String google_api_key = "AIzaSyAnne_4OgWq53yixhX9aVt-S33yQdgCWo4";
    private DoRequest doRequest = new DoRequest();
    private String values;

    LocationRequestTask(String values) {
        this.values = values;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            String response = doRequest.doPostRequest(link_get_location + google_api_key, values);
            return new JSONObject(response);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
