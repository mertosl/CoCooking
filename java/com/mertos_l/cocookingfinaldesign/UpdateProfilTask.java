package com.mertos_l.cocookingfinaldesign;

import android.os.AsyncTask;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

class UpdateProfilTask extends AsyncTask<Void, Void, String> {
    private static String url_profil = "http://dev.cocooking.eu/api/users/";
    private DoRequest doRequest = new DoRequest();
    private String uid;
    private String api_key;
    private String values;

    public UpdateProfilTask(String _uid, String _api_key, String _values) {
        uid = _uid;
        api_key = _api_key;
        values = _values;
    }

    @Override
    protected String doInBackground(Void... v) {
        try {
            String nonce = new NonceGenerator(6).nextString();
            String url = uid + "?uid=" + uid + "&nonce=" + nonce + "&sign=" + new HmacSha1().hmacSha1(new String("PUT:/users/:" + nonce), api_key);
            return doRequest.doPutRequest(url_profil + url, values);
        } catch (InvalidKeyException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
    }
}
