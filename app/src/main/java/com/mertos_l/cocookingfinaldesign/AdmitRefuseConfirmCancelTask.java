package com.mertos_l.cocookingfinaldesign;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AdmitRefuseConfirmCancelTask extends AsyncTask<Void, Void, JSONObject> {
    private static String url_cancel = "http://dev.cocooking.eu/api/order/cancel/";
    private static String url_admit = "http://dev.cocooking.eu/api/order/admit/";
    private static String url_decline = "http://dev.cocooking.eu/api/order/decline/";
    private static String url_confirmed = "http://dev.cocooking.eu/api/order/confirmed/";
    private DoRequest doRequest = new DoRequest();
    private String uid;
    private String api_key;
    private String values;
    private int index;

    public AdmitRefuseConfirmCancelTask(String uid, String api_key, String values, int index) {
        this.uid = uid;
        this.api_key = api_key;
        this.values = values;
        this.index = index;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            String nonce = new NonceGenerator(6).nextString();
            String url = (index == 0) ? ("?uid=" + uid + "&nonce=" + nonce + "&sign=" + new HmacSha1().hmacSha1(new String("POST:/order/cancel/:" + nonce), api_key)) :
                    ((index == 1) ? ("?uid=" + uid + "&nonce=" + nonce + "&sign=" + new HmacSha1().hmacSha1(new String("POST:/order/admit/:" + nonce), api_key)) :
                            ((index == 2) ? ("?uid=" + uid + "&nonce=" + nonce + "&sign=" + new HmacSha1().hmacSha1(new String("POST:/order/decline/:" + nonce), api_key)) :
                                    ("?uid=" + uid + "&nonce=" + nonce + "&sign=" + new HmacSha1().hmacSha1(new String("POST:/order/confirmed/:" + nonce), api_key))));
            String response = doRequest.doPostRequest((index == 0) ? (url_cancel + url) : ((index == 1) ? (url_admit + url) : ((index == 2) ? (url_decline + url) : (url_confirmed + url))), values);
            return new JSONObject(response);
        } catch (InvalidKeyException | NoSuchAlgorithmException | JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}