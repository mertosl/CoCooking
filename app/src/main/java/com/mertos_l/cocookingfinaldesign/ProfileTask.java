package com.mertos_l.cocookingfinaldesign;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

class ProfileTask extends AsyncTask<Void, Void, JSONObject> {
    private static String url_profil = "http://dev.cocooking.eu/api/users/";
    private DoRequest doRequest = new DoRequest();
    private String uid;
    private String userID;
    private String api_key;

    ProfileTask(String uid, String api_key, String userID) {
        this.uid = uid;
        this.userID = userID;
        this.api_key = api_key;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        String nonce = new NonceGenerator(6).nextString();
        try {
            String url = userID + "?uid=" + uid + "&nonce=" + nonce + "&sign=" + new HmacSha1().hmacSha1(new String("GET:/users/:" + nonce), api_key);
            String response = doRequest.doGetRequest(url_profil + url);
            return new JSONObject(response);
        } catch (NoSuchAlgorithmException | IOException | JSONException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}
