package com.mertos_l.cocookingfinaldesign;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

class ReservationTask extends AsyncTask<Void, Void, JSONObject> {
    private static String url_myorders = "http://dev.cocooking.eu/api/users/myorders/";
    private DoRequest doRequest = new DoRequest();
    private String uid;
    private String api_key;

    ReservationTask(String uid, String api_key) {
        this.uid = uid;
        this.api_key = api_key;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        String nonce = new NonceGenerator(6).nextString();
        try {
            String url = "?uid=" + uid + "&nonce=" + nonce + "&sign=" + new HmacSha1().hmacSha1(new String("GET:/users/myorders/:" + nonce), api_key);
            String response = doRequest.doGetRequest(url_myorders + url);
            return new JSONObject(response);
        } catch (IOException | JSONException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}