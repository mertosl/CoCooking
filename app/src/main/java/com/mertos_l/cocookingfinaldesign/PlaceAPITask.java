package com.mertos_l.cocookingfinaldesign;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class PlaceAPITask extends AsyncTask<Void, Void, JSONObject> {
    private static final String TAG = PlaceAPI.class.getSimpleName();
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyAnne_4OgWq53yixhX9aVt-S33yQdgCWo4";
    private DoRequest doRequest = new DoRequest();
    private String input;

    PlaceAPITask(String input) {
        this.input = input;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            String response = doRequest.doGetRequest(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON + "?key=" + API_KEY + "&input=" + input);
            return new JSONObject(response);
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
