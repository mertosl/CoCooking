package com.mertos_l.cocookingfinaldesign;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ParticipationsFragment extends Fragment {
    private static View root = null;
    private String uid;
    private String api_key;
    private JSONArray jsonArray1;
    private JSONArray jsonArray2;
    ListView listParticipations;
    ListView listParticipants;

    public ParticipationsFragment() {
    }

    public static Fragment newInstance(String uid, String api_key) {
        ParticipationsFragment fragment = new ParticipationsFragment();
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
        root = inflater.inflate(R.layout.fragment_participations, container, false);
        initViews();
        return root;
    }

    private void initViews() {
        listParticipations = (ListView) root.findViewById(R.id.listview1);
        ParticipationsAdapter adapter1 = null;
        ArrayList<ParticipationsFragmentItem> data1 = new ArrayList<ParticipationsFragmentItem>();
        UserRepasReservationsTask userRepasReservationsTask1 = new UserRepasReservationsTask(uid, api_key, 2);
        try {
            JSONObject jsonObjet = userRepasReservationsTask1.execute().get();
            if (jsonObjet != null && jsonObjet.has("status") && jsonObjet.getString("status").equals("ok")) {
                jsonArray1 = jsonObjet.optJSONArray("orders");
                if (jsonArray1.length() > 0) {
                    listParticipations.setVisibility(View.VISIBLE);
                    listParticipations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (jsonArray1 != null) {
                                try {
                                    showDialogParticipation(jsonArray1.optJSONObject(i).getString("idOrder"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    ((TextView) root.findViewById(R.id.participations)).setVisibility(View.GONE);
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        DetailsRepasTask detailsRepasTask = new DetailsRepasTask(jsonArray1.optJSONObject(i).getString("mid"));
                        JSONObject jsonObjectMeal = detailsRepasTask.execute().get();

                        data1.add(new ParticipationsFragmentItem(jsonArray1.optJSONObject(i).getString("idOrder"),
                                jsonObjectMeal.getString("title"),
                                jsonObjectMeal.getString("description").length() > 100 ? jsonObjectMeal.getString("description").substring(0, 97) + "..." : jsonObjectMeal.getString("description"),
                                jsonObjectMeal.getString("start_date"),
                                "Réservé pour " + jsonArray1.optJSONObject(i).getString("places") + " personnes",
                                String.valueOf(Double.valueOf(jsonArray1.optJSONObject(i).getString("places")) * Double.valueOf(jsonObjectMeal.getString("price"))) + "€"));
                    }
                }
            }
            adapter1 = new ParticipationsAdapter((MainActivity) getActivity(), R.layout.fragment_participations_item, data1);
            listParticipations.setAdapter(adapter1);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }

        listParticipants = (ListView) root.findViewById(R.id.listview2);
        ParticipantsAdapter adapter2 = null;
        ArrayList<ParticipantsFragmentItem> data2 = new ArrayList<ParticipantsFragmentItem>();
        UserRepasReservationsTask userRepasReservationsTask2 = new UserRepasReservationsTask(uid, api_key, 4);
        try {
            JSONObject jsonObjet = userRepasReservationsTask2.execute().get();
            if (jsonObjet != null && jsonObjet.has("status") && jsonObjet.getString("status").equals("ok")) {
                jsonArray2 = jsonObjet.optJSONArray("requests");
                if (jsonArray2.length() > 0) {
                    listParticipants.setVisibility(View.VISIBLE);
                    listParticipants.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (jsonArray2 != null) {
                                try {
                                    showDialogParticipant(jsonArray2.optJSONObject(i).getString("id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    ((TextView) root.findViewById(R.id.participants)).setVisibility(View.GONE);
                    for (int i = 0; i < jsonArray2.length(); i++) {
                        DetailsRepasTask detailsRepasTask = new DetailsRepasTask(jsonArray2.optJSONObject(i).getString("mid"));
                        JSONObject jsonObjectMeal = detailsRepasTask.execute().get();
                        ProfileTask profileTask = new ProfileTask(uid, api_key, jsonArray2.optJSONObject(i).getString("userid"));
                        JSONObject jsonObjectProfile = profileTask.execute().get();
                        Bitmap bitmap = null;
                        if (jsonObjectProfile.has("avatar") && !jsonObjectProfile.getString("avatar").isEmpty()) {
                            DownloadImageTask downloadImageTask = new DownloadImageTask("http://dev.cocooking.eu/media/" + jsonObjectProfile.getString("avatar"), getActivity());
                            bitmap = downloadImageTask.execute().get();
                        }
                        data2.add(new ParticipantsFragmentItem(jsonArray2.optJSONObject(i).getString("id"),
                                bitmap,
                                jsonArray2.optJSONObject(i).getString("user"),
                                String.valueOf(Double.valueOf(jsonObjectMeal.getString("price")) * Double.valueOf(jsonArray2.optJSONObject(i).getString("places"))) + "€"));
                    }
                }
            }
            adapter2 = new ParticipantsAdapter((MainActivity) getActivity(), R.layout.fragment_participants_item, data2);
            listParticipants.setAdapter(adapter2);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDialogParticipation(final String idOrder) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Voulez vous confirmer votre participation au repas ?");
        alert.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                JSONObject jsonaction = new JSONObject();
                try {
                    jsonaction.put("orderid", idOrder);
                    AdmitRefuseConfirmCancelTask confirmerTask = new AdmitRefuseConfirmCancelTask(uid, api_key, jsonaction.toString(), 3);
                    JSONObject ret = confirmerTask.execute().get();
                    if (ret.has("status") && ret.getString("status").equals("error")) {
                        Toast.makeText(getActivity(), "[-] Une erreur est survenue : " + ret.getString("message"), Toast.LENGTH_LONG).show();
                    } else if (ret.toString().contains("ok")) {
                        Toast.makeText(getActivity(), "[+] Votre confirmation de présence a bien été enregistrée.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        alert.setPositiveButton("Refuser", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                JSONObject jsonaction = new JSONObject();
                try {
                    jsonaction.put("orderid", idOrder);
                    AdmitRefuseConfirmCancelTask confirmerTask = new AdmitRefuseConfirmCancelTask(uid, api_key, jsonaction.toString(), 0);
                    JSONObject ret = confirmerTask.execute().get();
                    if (ret.has("status") && ret.getString("status").equals("error")) {
                        Toast.makeText(getActivity(), "[-] Une erreur est survenue : " + ret.getString("message"), Toast.LENGTH_LONG).show();
                    } else if (ret.toString().contains("ok")) {
                        Toast.makeText(getActivity(), "[+] Votre refus de participation a bien été enregistrée.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        alert.show();
    }

    private void showDialogParticipant(final String idOrder) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Voulez vous accepter ce participant au repas ?");
        alert.setPositiveButton("Accepter", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                JSONObject jsonaction = new JSONObject();
                try {
                    jsonaction.put("orderid", idOrder);
                    AdmitRefuseConfirmCancelTask confirmerTask = new AdmitRefuseConfirmCancelTask(uid, api_key, jsonaction.toString(), 3);
                    JSONObject ret = confirmerTask.execute().get();
                    if (ret.has("status") && ret.getString("status").equals("error")) {
                        Toast.makeText(getActivity(), "[-] Une erreur est survenue : " + ret.getString("message"), Toast.LENGTH_LONG).show();
                    } else if (ret.toString().contains("ok")) {
                        Toast.makeText(getActivity(), "[+] Le participant a bien été admis.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        alert.setPositiveButton("Refuser", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                JSONObject jsonaction = new JSONObject();
                try {
                    jsonaction.put("orderid", idOrder);
                    AdmitRefuseConfirmCancelTask confirmerTask = new AdmitRefuseConfirmCancelTask(uid, api_key, jsonaction.toString(), 0);
                    JSONObject ret = confirmerTask.execute().get();
                    if (ret.has("status") && ret.getString("status").equals("error")) {
                        Toast.makeText(getActivity(), "[-] Une erreur est survenue : " + ret.getString("message"), Toast.LENGTH_LONG).show();
                    } else if (ret.toString().contains("ok")) {
                        Toast.makeText(getActivity(), "[+] Le participant a bien été refusé.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        alert.show();
    }
}