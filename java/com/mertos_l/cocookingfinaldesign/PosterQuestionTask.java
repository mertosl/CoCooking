package com.mertos_l.cocookingfinaldesign;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class PosterQuestionTask extends AsyncTask<Void, Void, JSONObject> {
    private static String url_poster_question = "http://dev.cocooking.eu/api/questions/";
    DoRequest doRequest = new DoRequest();
    String uid;
    String api_key;
    String values;

    public PosterQuestionTask(String uid, String api_key, String values) {
        this.uid = uid;
        this.api_key = api_key;
        this.values = values;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        String nonce = new NonceGenerator(6).nextString();
        try {
            String response = null;
            String url = new String("?uid=" + uid + "&nonce=" + nonce + "&sign=" + new HmacSha1().hmacSha1(new String("POST:/questions/:" + nonce), api_key));
            response = doRequest.doPostRequest(url_poster_question + url, values);
            return new JSONObject(response);
        } catch (NoSuchAlgorithmException | IOException | JSONException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}

