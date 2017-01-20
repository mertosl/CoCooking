package com.mertos_l.cocookingfinaldesign;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class PaymentCBTask extends AsyncTask<Void, Void, JSONObject> {
    private static String url_token = "http://dev.cocooking.eu/api/order/token/";
    DoRequest doRequest = new DoRequest();
    private String uid;
    private String api_key;
    private String values;

    PaymentCBTask(String uid, String api_key, String values) {
        this.uid = uid;
        this.api_key = api_key;
        this.values = values;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            String nonce = new NonceGenerator(6).nextString();
            String url = "?uid=" + uid + "&nonce=" + nonce + "&sign=" + new HmacSha1().hmacSha1(new String("POST:/order/token/:" + nonce), api_key);
            String response = doRequest.doPostRequest(url_token + url, values);
            return new JSONObject(response);
        } catch (JSONException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}