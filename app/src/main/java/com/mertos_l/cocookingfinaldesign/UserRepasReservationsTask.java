package com.mertos_l.cocookingfinaldesign;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

class UserRepasReservationsTask extends AsyncTask<Void, Void, JSONObject> {
    private static String url_urmearls = "http://dev.cocooking.eu/api/users/mymeals/";
    private static String url_urhistory = "http://dev.cocooking.eu/api/users/mymealshistory/";
    private static String url_urorders = "http://dev.cocooking.eu/api/users/myorders/";
    private static String url_urordershistory = "http://dev.cocooking.eu/api/users/myordershistory/";
    private static String url_requests = "http://dev.cocooking.eu/api/users/mymealsrequests/";
    private DoRequest doRequest = new DoRequest();
    private String uid;
    private String api_key;
    private int index;

    UserRepasReservationsTask(String uid, String api_key, int index) {
        this.uid = uid;
        this.api_key = api_key;
        this.index = index;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            String nonce = new NonceGenerator(6).nextString();
            String url = (index == 0) ? ("?uid=" + uid + "&nonce=" + nonce + "&sign=" + new HmacSha1().hmacSha1(new String("GET:/users/mymeals/:" + nonce), api_key)) :
                    ((index == 1) ? ("?uid=" + uid + "&nonce=" + nonce + "&sign=" + new HmacSha1().hmacSha1(new String("GET:/users/mymealshistory/:" + nonce), api_key)) :
                            ((index == 2) ? ("?uid=" + uid + "&nonce=" + nonce + "&sign=" + new HmacSha1().hmacSha1(new String("GET:/users/myorders/:" + nonce), api_key)) :
                                    ((index == 3) ? ("?uid=" + uid + "&nonce=" + nonce + "&sign=" + new HmacSha1().hmacSha1(new String("GET:/users/myordershistory/:" + nonce), api_key)) :
                                            ("?uid=" + uid + "&nonce=" + nonce + "&sign=" + new HmacSha1().hmacSha1(new String("GET:/users/mymealsrequests/:" + nonce), api_key)))));
            String response = doRequest.doGetRequest((index == 0) ? (url_urmearls + url) : ((index == 1) ? (url_urhistory + url) : ((index == 2) ? (url_urorders + url) : ((index == 3) ? (url_urordershistory + url) : (url_requests + url)))));
            return new JSONObject(response);
        } catch (InvalidKeyException | NoSuchAlgorithmException | JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}