package com.mertos_l.cocookingfinaldesign;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ProfilSecuriteFragment extends Fragment {
    private static View root = null;
    private String uid;
    private String api_key;
    private String email;

    public ProfilSecuriteFragment() {
    }

    public static Fragment newInstance(String uid, String api_key, String email) {
        ProfilSecuriteFragment fragment = new ProfilSecuriteFragment();
        Bundle args = new Bundle();
        args.putString("uid", uid);
        args.putString("api_key", api_key);
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString("uid");
            api_key = getArguments().getString("api_key");
            email = getArguments().getString("email");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profil_securite, container, false);
        initViews();
        return root;
    }

    private void initViews() {
        ((TextView)root.findViewById(R.id.email)).setText(email);
        ((Button)root.findViewById(R.id.btn_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((EditText) root.findViewById(R.id.ancien)).getText().toString().isEmpty() &&
                        !((EditText) root.findViewById(R.id.nouveau)).getText().toString().isEmpty() &&
                        !((EditText) root.findViewById(R.id.confirmer)).getText().toString().isEmpty() &&
                        ((EditText) root.findViewById(R.id.nouveau)).getText().toString().equals(((EditText) root.findViewById(R.id.confirmer)).getText().toString())) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("oldpassword", ((EditText) root.findViewById(R.id.ancien)).getText().toString());
                        jsonObject.put("newpassword", ((EditText) root.findViewById(R.id.nouveau)).getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    UpdateProfilTask updateProfilTask = new UpdateProfilTask(uid, api_key, jsonObject.toString());
                    try {
                        String res = updateProfilTask.execute().get();
                        if (res.equals("\"OK\""))
                            Toast.makeText(getActivity(), "[+] Le nouveau mot de passe a bien été sauvegardé.", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getActivity(), "[-] Une erreur est sur venue. Le nouveau mot de passe doit être au minimum de 8 charactères et doit contenir au moin 1 majuscule et 1 chiffre.", Toast.LENGTH_LONG).show();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
