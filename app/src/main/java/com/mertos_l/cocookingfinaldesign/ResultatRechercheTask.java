package com.mertos_l.cocookingfinaldesign;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;

class ResultatRechercheTask extends AsyncTask<Void, Void, JSONArray> {
    private static String url = "http://dev.cocooking.eu/api/meal/";
    private DoRequest doRequest = new DoRequest();
    private String location;
    private String date;
    private String words;

    ResultatRechercheTask(List<Double> location, String date, String words) {
        this.location = "&la="+String.valueOf(location.get(0))+"&lo="+String.valueOf(location.get(1));
        this.date = date.isEmpty() ? "&d" : "&d="+date;
        this.words = date.isEmpty() ? "w" : "w="+words;
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        try {
            String response = doRequest.doGetRequest(url + "?"+words+location+"&r&pm&pa&ph&pc&ps&l"+date);
            return response.contains("error") ? null : new JSONArray(response);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
