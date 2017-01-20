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

import de.hdodenhof.circleimageview.CircleImageView;

public class QuestionFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static View root = null;
    private String uid;
    private String api_key;
    private JSONObject jsonObject = null;
    ListView listview;

    public QuestionFragment() {
    }

    public static Fragment newInstance(String uid, String api_key) {
        QuestionFragment fragment = new QuestionFragment();
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
        listview.setOnItemClickListener(this);
        QuestionFragmentListAdapter adapter = null;
        String username = "";
        String mealname = "";
        try {
            ArrayList<QuestionReponseFragmentListItem> data = new ArrayList<QuestionReponseFragmentListItem>();
            QuestionTask questionReponseTask = new QuestionTask(uid);
            JSONArray jsonArray = questionReponseTask.execute().get();
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    DetailsRepasTask detailsRepasTask = new DetailsRepasTask(jsonArray.optJSONObject(i).getString("mealid"));
                    jsonObject = detailsRepasTask.execute().get();
                    mealname = jsonObject.getString("title");

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
                            jsonArray.optJSONObject(i).getString("subject"),
                            mealname,
                            username,
                            jsonArray.optJSONObject(i).getString("date")));
                }
            }
            adapter = new QuestionFragmentListAdapter((MainActivity) getActivity(), data);
            listview.setAdapter(adapter);
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
