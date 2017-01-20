package com.mertos_l.cocookingfinaldesign;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class RepasFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static View root = null;
    private String uid;
    private String api_key;
    private JSONArray jsonArray1;
    private JSONArray jsonArray2;
    ListView listRepas;
    ListView listHistorique;

    public RepasFragment() {
    }

    public static Fragment newInstance(String uid, String api_key) {
        RepasFragment fragment = new RepasFragment();
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
        root = inflater.inflate(R.layout.fragment_mesrepas, container, false);
        initViews();
        return root;
    }

    private void initViews() {
        listRepas = (ListView) root.findViewById(R.id.listview1);
        listRepas.setVisibility(View.GONE);
        MesRepasAdapter adapter2 = null;
        ArrayList<MesRepasFragmentItem> data2 = new ArrayList<MesRepasFragmentItem>();
        UserRepasReservationsTask mesrepasTask = new UserRepasReservationsTask(uid, api_key, 0);
        try {
            JSONObject jsonObjet = mesrepasTask.execute().get();
            if (jsonObjet != null && jsonObjet.has("status") && jsonObjet.getString("status").equals("ok")) {
                jsonArray2 = jsonObjet.optJSONArray("meals");
                if (jsonArray2.length() > 0) {
                    listRepas.setVisibility(View.VISIBLE);
                    ((TextView) root.findViewById(R.id.mesrepas)).setVisibility(View.GONE);
                    for (int i = 0; i < jsonArray2.length(); i++) {
                        DetailsRepasTask detailsRepasTask = new DetailsRepasTask(jsonArray2.optJSONObject(i).getString("id"));
                        JSONObject jsonObjectMeal = detailsRepasTask.execute().get();
                        ProfileTask profileTask = new ProfileTask(uid, api_key, jsonArray2.optJSONObject(i).getString("host"));
                        JSONObject jsonObjectProfile = profileTask.execute().get();
                        String firstname = jsonObjectProfile.getString("firstname");
                        String lastname = jsonObjectProfile.getString("lastname");
                        data2.add(new MesRepasFragmentItem(jsonArray2.optJSONObject(i).getString("id"),
                                jsonObjectMeal.getString("title"),
                                jsonObjectMeal.getString("description"),
                                jsonObjectMeal.getString("start_date"),
                                "Pourrait rapporter " + String.valueOf(Double.valueOf(jsonArray2.optJSONObject(i).getString("places")) * Double.valueOf(jsonObjectMeal.getString("price"))) + " €"));
                    }
                }
            }
            adapter2 = new MesRepasAdapter((MainActivity) getActivity(), R.layout.fragment_mesrepas_item, data2);
            listRepas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        ((MainActivity)getActivity()).showDetailsTabs(jsonArray2.optJSONObject(position).getString("id"), uid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            listRepas.setAdapter(adapter2);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }

        listHistorique = (ListView) root.findViewById(R.id.listview2);
        listHistorique.setVisibility(View.GONE);
        HistoriqueRepasAdapter adapter1 = null;
        ArrayList<HistoriqueRepasFragmentItem> data1 = new ArrayList<HistoriqueRepasFragmentItem>();
        UserRepasReservationsTask mealshistoryTask = new UserRepasReservationsTask(uid, api_key, 1);
        try {
            JSONObject jsonObjet = mealshistoryTask.execute().get();
            if (jsonObjet != null && jsonObjet.has("status") && jsonObjet.getString("status").equals("ok")) {
                jsonArray1 = jsonObjet.optJSONArray("mealshistory");
                if (jsonArray1.length() > 0) {
                    listHistorique.setVisibility(View.VISIBLE);
                    ((TextView) root.findViewById(R.id.historique)).setVisibility(View.GONE);
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        DetailsRepasTask detailsRepasTask = new DetailsRepasTask(jsonArray1.optJSONObject(i).getString("id"));
                        JSONObject jsonObjectMeal = detailsRepasTask.execute().get();

                        data1.add(new HistoriqueRepasFragmentItem(jsonArray1.optJSONObject(i).getString("id"),
                                jsonArray1.optJSONObject(i).getString("title"),
                                jsonArray1.optJSONObject(i).getString("description"),
                                jsonArray1.optJSONObject(i).getString("start_date"),
                                "Vous a rapporté " + String.valueOf((Double.valueOf(jsonArray1.optJSONObject(i).getString("places")) - Double.valueOf(jsonArray1.optJSONObject(i).getString("places_free"))) * Double.valueOf(jsonArray1.optJSONObject(i).getString("price"))) + "€"));
                    }
                }
            }
            adapter1 = new HistoriqueRepasAdapter((MainActivity) getActivity(), R.layout.fragment_mesrepas_item, data1);
            listHistorique.setAdapter(adapter1);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}