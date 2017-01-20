package com.mertos_l.cocookingfinaldesign;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class QuestionRepasFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static View root = null;
    private String uid;
    private String api_key;
    private String mealid;
    private String userid;
    private ListView list;
    private JSONObject detailsRepas;
    private JSONObject profile;

    public QuestionRepasFragment() {
    }

    public static Fragment newInstance(String uid, String api_key, String mealid, String userid) {
        QuestionRepasFragment fragment = new QuestionRepasFragment();
        Bundle args = new Bundle();
        args.putString("uid", uid);
        args.putString("api_key", api_key);
        args.putString("mealid", mealid);
        args.putString("userid", userid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString("uid");
            api_key = getArguments().getString("api_key");
            mealid = getArguments().getString("mealid");
            userid = getArguments().getString("userid");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_question_repas, container, false);
        try {
            initViews();
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
        return root;
    }

    private void initViews() throws ExecutionException, InterruptedException, JSONException {
        list = (ListView) root.findViewById(R.id.listview);
        list.setOnItemClickListener(this);
        DetailsRepasTask detailsRepasTask = new DetailsRepasTask(mealid);
        detailsRepas = detailsRepasTask.execute().get();
        QuestionRepasAdapter adapter = null;
        if (detailsRepas != null) {
            ArrayList<QuestionRepasFragmentItem> data = new ArrayList<QuestionRepasFragmentItem>();
            for (int i = 0; i < detailsRepas.optJSONArray("questions").length(); i++) {
                ProfileTask profileTask = new ProfileTask(uid, api_key, detailsRepas.optJSONArray("questions").optJSONObject(i).getString("userid"));
                profile = profileTask.execute().get();
                if (profile != null) {
                    DownloadImageTask downloadImageTask = new DownloadImageTask(profile.getString("avatar"), getActivity());
                    Bitmap bitmap = downloadImageTask.execute().get();
                    data.add(new QuestionRepasFragmentItem(bitmap,
                            detailsRepas.optJSONArray("questions").optJSONObject(i).getString("subject"),
                            profile.getString("firstname") + " " + profile.getString("lastname"),
                            detailsRepas.optJSONArray("questions").optJSONObject(i).getString("date"),
                            detailsRepas.optJSONArray("questions").optJSONObject(i).getString("content"), 0));
                    for (int j = 0; j < detailsRepas.optJSONArray("questions").optJSONObject(i).optJSONArray("responses").length(); j++) {
                        ProfileTask cuistoTask = new ProfileTask(uid, api_key, detailsRepas.getString("host"));
                        JSONObject cuisto = cuistoTask.execute().get();
                        data.add(new QuestionRepasFragmentItem(null,
                                null,
                                (((cuisto.getString("firstname").equals("null") ? "" : cuisto.getString("firstname")) + " " + (cuisto.getString("lastname").equals("null") ? "" : cuisto.getString("lastname"))).equals(" ") ? "Pas de nom référencé" : ((cuisto.getString("firstname").equals("null") ? "" : cuisto.getString("firstname")) + " " + (cuisto.getString("lastname").equals("null") ? "" : cuisto.getString("lastname")))),
                                detailsRepas.optJSONArray("questions").optJSONObject(i).optJSONArray("responses").optJSONObject(j).getString("date"),
                                detailsRepas.optJSONArray("questions").optJSONObject(i).optJSONArray("responses").optJSONObject(j).getString("content"), 1));
                    }
                }
            }
            adapter = new QuestionRepasAdapter(((MainActivity) getActivity()).getApplicationContext(), R.layout.fragment_question_repas_question_item, data);
            list.setAdapter(adapter);
        } else {
            Toast.makeText(getActivity(), "[-] Aucune question n'a été trouvée.", Toast.LENGTH_LONG).show();
        }
        if (!uid.equals(userid)) {
            ((Button) root.findViewById(R.id.btn_poster)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogQuestion();
                    try {
                        ((MainActivity)getActivity()).showDetailsTabs(mealid, detailsRepas.getString("host"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((Button) root.findViewById(R.id.btn_poster)).setBackgroundColor(((MainActivity)getActivity()).getColor(R.color.colorGris));
            }
        }
    }

    private void showDialogQuestion() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LinearLayout layout = new LinearLayout(getActivity());

        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText subject = new EditText(getActivity());
        subject.setHint("Sujet");
        layout.addView(subject);
        final EditText content = new EditText(getActivity());
        content.setHint("Message");
        layout.addView(content);

        alert.setTitle("Une question ?");
        alert.setView(layout);
        alert.setPositiveButton("Poster", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String sujet = subject.getText().toString();
                String contenu = content.getText().toString();
                JSONObject jsonQuestion = new JSONObject();
                try {
                    jsonQuestion.put("mealid", mealid);
                    jsonQuestion.put("subject", sujet);
                    jsonQuestion.put("content", contenu);
                    PosterQuestionTask posterQuestionTask = new PosterQuestionTask(uid, api_key, jsonQuestion.toString());
                    JSONObject ret = posterQuestionTask.execute().get();
                    if (ret.has("status") && ret.getString("status").equals("error")) {
                        Toast.makeText(getActivity(), "[-] Une erreur est survenue : " + ret.getString("message"), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "[+] La question a bien été postée.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        alert.show();
    }

    private void showDialogReponse(final String qid) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        final EditText content = new EditText(getActivity());
        content.setHint("Message");

        alert.setTitle("Une réponse ?");
        alert.setView(content);
        alert.setPositiveButton("Poster", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String contenu = content.getText().toString();
                JSONObject jsonReponse = new JSONObject();
                try {
                    jsonReponse.put("content", contenu);
                    PosterReponseTask posterReponseTask = new PosterReponseTask(uid, api_key, jsonReponse.toString(), qid);
                    JSONObject ret = posterReponseTask.execute().get();
                    if (ret.has("status") && ret.getString("status").equals("error")) {
                        Toast.makeText(getActivity(), "[-] Une erreur est survenue : " + ret.getString("message"), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "[+] La réponse a bien été postée.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        alert.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (uid.equals(userid)) {
            try {
                showDialogReponse(detailsRepas.optJSONArray("questions").optJSONObject(position).getString("id"));
                try {
                    ((MainActivity)getActivity()).showDetailsTabs(mealid, detailsRepas.getString("host"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
