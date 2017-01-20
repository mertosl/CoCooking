package com.mertos_l.cocookingfinaldesign;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ProfilComplementaireFragment extends Fragment {
    private static View root = null;
    private String uid;
    private String api_key;

    public ProfilComplementaireFragment() {
    }

    public static Fragment newInstance(String uid, String api_key) {
        ProfilComplementaireFragment fragment = new ProfilComplementaireFragment();
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
        root = inflater.inflate(R.layout.fragment_profil_complementaire, container, false);
        initViews();
        return root;
    }

    private void initViews() {
        ProfileTask profileTask = new ProfileTask(uid, api_key, uid);
        try {
            JSONObject jsonObject = profileTask.execute().get();
            if (jsonObject.has("bio") && !jsonObject.getString("bio").equals("null"))
                ((EditText)root.findViewById(R.id.description)).setText(jsonObject.getString("bio"));
            if (jsonObject.has("newsletter") && (!jsonObject.getString("newsletter").equals("null") || !jsonObject.getString("newsletter").isEmpty()))
                ((CheckBox)root.findViewById(R.id.checkBox)).setChecked(jsonObject.getString("newsletter").equals("1"));
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
        ((Button)root.findViewById(R.id.btn_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("bio", ((EditText)root.findViewById(R.id.description)).getText().toString().equals("null") ? "" : ((EditText)root.findViewById(R.id.description)).getText().toString());
                    jsonObject.put("newsletter", ((CheckBox) root.findViewById(R.id.checkBox)).isChecked() ? "1" : "0");
                    } catch (JSONException e) {
                    e.printStackTrace();
                }
                UpdateProfilTask updateProfilTask = new UpdateProfilTask(uid, api_key, jsonObject.toString());
                try {
                    String res = updateProfilTask.execute().get();
                    if (res.equals("\"OK\""))
                        Toast.makeText(getActivity(), "[+] Vos nouvelles informations ont bien été sauvegardées.", Toast.LENGTH_LONG).show();
                    else if (res.contains("bio"))
                        Toast.makeText(getActivity(), "[-] Vérifier le champ \"Description\".", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getActivity(), "[-] Une erreur est sur venue.", Toast.LENGTH_LONG).show();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
