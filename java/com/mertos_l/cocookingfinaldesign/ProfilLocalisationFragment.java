package com.mertos_l.cocookingfinaldesign;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ExecutionException;

public class ProfilLocalisationFragment extends Fragment {
    private static View root = null;
    private String uid;
    private String api_key;

    public ProfilLocalisationFragment() {
    }

    public static Fragment newInstance(String uid, String api_key) {
        ProfilLocalisationFragment fragment = new ProfilLocalisationFragment();
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
        root = inflater.inflate(R.layout.fragment_profil_localisation, container, false);
        initViews();
        return root;
    }

    private void initViews() {
        ProfileTask profileTask = new ProfileTask(uid, api_key, uid);
        try {
            JSONObject jsonObject = profileTask.execute().get();
            if (jsonObject.has("adress") && !jsonObject.getString("adress").equals("null"))
                ((EditText)root.findViewById(R.id.adresse)).setText(jsonObject.getString("adress"));
            if (jsonObject.has("city") && !jsonObject.getString("city").equals("null"))
                ((EditText)root.findViewById(R.id.ville)).setText(jsonObject.getString("city"));
            if (jsonObject.has("zipcode") && !jsonObject.getString("zipcode").equals("null"))
                ((EditText)root.findViewById(R.id.code_postal)).setText(jsonObject.getString("zipcode"));
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
        ((Button)root.findViewById(R.id.btn_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("adress", ((EditText)root.findViewById(R.id.adresse)).getText().toString().equals("null") ? "" : ((EditText)root.findViewById(R.id.adresse)).getText().toString());
                    jsonObject.put("city", ((EditText)root.findViewById(R.id.ville)).getText().toString().equals("null") ? "" : ((EditText)root.findViewById(R.id.ville)).getText().toString());
                    jsonObject.put("zipcode", ((EditText)root.findViewById(R.id.code_postal)).getText().toString().equals("null") ? "" : ((EditText)root.findViewById(R.id.code_postal)).getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                UpdateProfilTask updateProfilTask = new UpdateProfilTask(uid, api_key, jsonObject.toString());
                try {
                    String res = updateProfilTask.execute().get();
                    if (res.equals("\"OK\""))
                        Toast.makeText(getActivity(), "[+] Vos nouvelles informations ont bien été sauvegardées.", Toast.LENGTH_LONG).show();
                    else if (res.contains("adress"))
                        Toast.makeText(getActivity(), "[-] Vérifier le champ \"Adresse\".", Toast.LENGTH_LONG).show();
                    else if (res.contains("city"))
                        Toast.makeText(getActivity(), "[-] Vérifier le champ \"Ville\".", Toast.LENGTH_LONG).show();
                    else if (res.contains("zipcode"))
                        Toast.makeText(getActivity(), "[-] Vérifier le champ \"Code postal\".", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getActivity(), "[-] Une erreur est sur venue.", Toast.LENGTH_LONG).show();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
