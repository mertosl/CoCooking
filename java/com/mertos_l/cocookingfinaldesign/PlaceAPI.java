package com.mertos_l.cocookingfinaldesign;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

class PlaceAPI {
    private PlaceAPITask placeAPITask;

    ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;
        placeAPITask = new PlaceAPITask(input);
        JSONObject jsonObj = null;
        try {
            jsonObj = placeAPITask.execute().get();
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
        return resultList;
    }


}