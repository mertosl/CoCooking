package com.mertos_l.cocookingfinaldesign;

import android.content.ContentValues;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DoRequest {
    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public DoRequest() {
    }

    String doPostImage(String url, File sourceFile, String name) throws IOException {
        final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
        RequestBody body = new MultipartBody
                .Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("picture", name + ".jpeg", RequestBody.create(MEDIA_TYPE_JPEG, sourceFile))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    String doGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    String doPostRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    String doPutRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    String doDelRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .delete(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    RequestBody makeRequestBody(ContentValues contentValues) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : contentValues.valueSet()) {
            formBodyBuilder.add(entry.getKey(), entry.getValue().toString());
        }
        return formBodyBuilder.build();
    }
}
