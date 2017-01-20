package com.mertos_l.cocookingfinaldesign;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

class PosterReponseTask extends AsyncTask<Void, Void, JSONObject> {
    private static String url_poster_reponse = "http://dev.cocooking.eu/api/questions/";
    private DoRequest doRequest = new DoRequest();
    private String uid;
    private String api_key;
    private String values;
    private String qid;

    PosterReponseTask(String uid, String api_key, String s, String qid) {
        this.uid = uid;
        this.api_key = api_key;
        this.values = s;
        this.qid = qid;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        String nonce = new NonceGenerator(6).nextString();
        try {
            String response = null;
            String url = new String(qid + "/?uid=" + uid + "&nonce=" + nonce + "&sign=" + new HmacSha1().hmacSha1(new String("POST:/questions/:" + nonce), api_key));
            response = doRequest.doPostRequest(url_poster_reponse + url, values);
            return new JSONObject(response);
        } catch (NoSuchAlgorithmException | IOException | JSONException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}