package com.mertos_l.cocookingfinaldesign;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ReponseFragment extends Fragment {
    private static View root = null;
    private String uid;
    private String api_key;
    private JSONObject jsonObject = null;
    ListView listview;

    public ReponseFragment() {
    }

    public static Fragment newInstance(String uid, String api_key) {
        ReponseFragment fragment = new ReponseFragment();
        Bundle args = new Bundle();
        args.putString("uid", uid);
        args.putString("api_key", api_key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString("uid");
            api_key = getArguments().getString("api_key");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_questions, container, false);
        initViews();
        return root;
    }

    void initViews() {
        listview = (ListView) root.findViewById(R.id.listview);
        QuestionFragmentListAdapter adapter = null;
        String username = "";
        String mealname = "";
        try {
            ArrayList<QuestionReponseFragmentListItem> data = new ArrayList<QuestionReponseFragmentListItem>();
            ReponseTask reponseTask = new ReponseTask(uid);
            JSONArray jsonArray = reponseTask.execute().get();
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    ProfileTask profileTask = new ProfileTask(uid, api_key, jsonArray.optJSONObject(i).getString("userid"));
                    jsonObject = profileTask.execute().get();
                    username = jsonObject.getString("firstname") + " " + jsonObject.getString("lastname");

                    Bitmap bitmap = null;
                    if (jsonObject.has("avatar") && !jsonObject.getString("avatar").equals("null")) {
                        DownloadImageTask downloadImageTask = new DownloadImageTask(jsonObject.getString("avatar"), getActivity());
                        bitmap = downloadImageTask.execute().get();
                    }

                    data.add(new QuestionReponseFragmentListItem(bitmap,
                            jsonArray.optJSONObject(i).getString("content"),
                            "",
                            "",
                            username.equals(" ") ? "Pas de nom référencé" : username,
                            jsonArray.optJSONObject(i).getString("date")));
                }
            }
            adapter = new QuestionFragmentListAdapter((MainActivity) getActivity(), data);
            listview.setAdapter(adapter);
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
