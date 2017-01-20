package com.mertos_l.cocookingfinaldesign;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ResultatRechercheFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static View root = null;
    private String uid;
    private String api_key;
    private String address;
    private String date;
    private String words;
    private GridView gridview;
    private JSONArray recherche_result;

    public ResultatRechercheFragment() {
    }

    public static Fragment newInstance(String uid, String api_key, String address, String date, String words) {
        ResultatRechercheFragment fragment = new ResultatRechercheFragment();
        Bundle args = new Bundle();
        args.putString("uid", uid);
        args.putString("api_key", api_key);
        args.putString("address", address);
        args.putString("date", date);
        args.putString("words", words);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString("uid");
            api_key = getArguments().getString("api_key");
            address = getArguments().getString("address");
            date = getArguments().getString("date");
            words = getArguments().getString("words");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_resultats_recherche, container, false);
        try {
            initViews();
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
        return root;
    }

    private void showDatasToGridViews() throws ExecutionException, InterruptedException, JSONException {
        ResultatRechercheTask resultatRechercheTask = new ResultatRechercheTask(((MainActivity)getActivity()).getLocation(address), date, words);
        recherche_result = resultatRechercheTask.execute().get();
        ResultatRechercheGridAdapter adapter = null;
        if (recherche_result != null) {
            try {
                ArrayList<ResultatRechercheGridItem> data = new ArrayList<ResultatRechercheGridItem>();
                for (int i = 0; i < recherche_result.length(); i++) {
                    DownloadImageTask downloadImageTask = new DownloadImageTask("http://dev.cocooking.eu/media/" + recherche_result.optJSONObject(i).getString("path"), getActivity());
                    Bitmap bitmap = downloadImageTask.execute().get();
                    data.add(new ResultatRechercheGridItem(bitmap,
                            recherche_result.optJSONObject(i).getString("title"),
                            recherche_result.optJSONObject(i).getString("places_free") + " places de libre",
                            recherche_result.optJSONObject(i).getString("price") + "€",
                            recherche_result.optJSONObject(i).getString("start_date")));
                }
                adapter = new ResultatRechercheGridAdapter(((MainActivity) getActivity()).getApplicationContext(), R.layout.fragment_resultats_recherche_item, data);
                gridview.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "[-] Aucun résultat n'a été trouvé.", Toast.LENGTH_LONG).show();
        }
    }

    void initViews() throws ExecutionException, InterruptedException, JSONException {
        gridview = (GridView) root.findViewById(R.id.gridView);
        gridview.setOnItemClickListener(this);
        showDatasToGridViews();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            ((MainActivity) getActivity()).showDetailsTabs(recherche_result.optJSONObject(i).getString("id"), recherche_result.optJSONObject(i).getString("host"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
