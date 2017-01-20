package com.mertos_l.cocookingfinaldesign;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ExecutionException;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilFragment extends Fragment {
    private static View root = null;
    private String uid;
    private String api_key;
    private String userid;
    private int index;

    public ProfilFragment() {
    }

    public static Fragment newInstance(String uid, String api_key, String userid, int index) {
        ProfilFragment fragment = new ProfilFragment();
        Bundle args = new Bundle();
        args.putString("uid", uid);
        args.putString("api_key", api_key);
        args.putString("userid", userid);
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString("uid");
            api_key = getArguments().getString("api_key");
            userid = getArguments().getString("userid");
            index = getArguments().getInt("index");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profil, container, false);
        initViews();
        return root;
    }

    private void initViews() {
        ProfileTask profileTask = new ProfileTask(uid, api_key, index == 0 ? uid : userid);
        JSONObject jsonObject = null;
        try {
            jsonObject = profileTask.execute().get();
            if (jsonObject.has("avatar") && !jsonObject.getString("avatar").equals("null")) {
                DownloadImageTask downloadImageTask = new DownloadImageTask(jsonObject.getString("avatar"), getActivity());
                Bitmap bitmap = downloadImageTask.execute().get();
                if (bitmap != null)
                    ((CircleImageView)root.findViewById(R.id.photo)).setImageBitmap(bitmap);
            }
            String firstname = "";
            String lastname = "";
            String user = "";
            if (jsonObject.has("firstname") && !jsonObject.getString("firstname").equals("null"))
                firstname = jsonObject.getString("firstname");
            if (jsonObject.has("lastname") && !jsonObject.getString("lastname").equals("null"))
                lastname = jsonObject.getString("lastname");
            user = (firstname.equals("") && lastname.equals("") ? "Nom et Prénom non renseignés" : (firstname.equals("") && !lastname.equals("") ? lastname : (!firstname.equals("") && lastname.equals("") ? firstname : firstname + " " + lastname)));
            ((TextView)root.findViewById(R.id.name)).setText(user);
            UserRepasReservationsTask userRepasReservationsTask = new UserRepasReservationsTask(uid, api_key, 1);
            JSONObject jsonURRT1 = userRepasReservationsTask.execute().get();
            userRepasReservationsTask = new UserRepasReservationsTask(uid, api_key, 3);
            JSONObject jsonURRT2 = userRepasReservationsTask.execute().get();
            if (jsonURRT1.has("status") && jsonURRT1.getString("status").equals("ok") && jsonURRT2.has("status") && jsonURRT2.getString("status").equals("ok")) {
                ((TextView)root.findViewById(R.id.repas_partages)).setText(String.valueOf(jsonURRT1.optJSONArray("mealshistory").length() + jsonURRT2.optJSONArray("orders").length()));
            }
            if (jsonObject.has("bio") && !jsonObject.getString("bio").equals("null")) {
                ((TextView)root.findViewById(R.id.bio)).setText(jsonObject.getString("bio"));
            }
            if (index == 1) {
                ((LinearLayout)root.findViewById(R.id.container_cagnotte)).setVisibility(View.GONE);
            } else {
                ((LinearLayout)root.findViewById(R.id.container_cagnotte)).setVisibility(View.VISIBLE);
                if (jsonObject.has("wallet") && !jsonObject.getString("wallet").equals("null") && !jsonObject.getString("wallet").equals("")) {
                    ((TextView)root.findViewById(R.id.cagnotte)).setText(String.valueOf(Double.valueOf(jsonObject.getString("wallet")) / Double.valueOf("100")) + " €");
                }
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
        if (index == 1) {
            ((Button)root.findViewById(R.id.btn_edit_profil)).setVisibility(View.GONE);
        } else {
            ((Button)root.findViewById(R.id.btn_edit_profil)).setVisibility(View.VISIBLE);
            ((Button)root.findViewById(R.id.btn_edit_profil)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) getActivity()).showEditerCompteTabs();
                }
            });
        }
    }
}
