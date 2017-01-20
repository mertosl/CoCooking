package com.mertos_l.cocookingfinaldesign;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import de.hdodenhof.circleimageview.CircleImageView;
import static android.app.Activity.RESULT_OK;

public class CreationFragment extends Fragment {
    private static View root = null;
    private String uid;
    private String api_key;
    private String mealid = null;
    JSONObject jsonObject = null;
    private Bitmap bitmap = null;
    static final int REQUEST_IMAGE_CAPTURE = 25;

    public CreationFragment() {
    }

    public static Fragment newInstance(String uid, String api_key) {
        CreationFragment fragment = new CreationFragment();
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
        root = inflater.inflate(R.layout.fragment_creation, container, false);
        initViews();
        return root;
    }

    private void initViews() {
        ((CircleImageView)root.findViewById(R.id.photo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mealid != null) {
                    if (((MainActivity) getActivity()).isFeatureAvailable(PackageManager.FEATURE_CAMERA)) {
                        dispatchTakePictureIntent();
                    }
                }
            }
        });
        ((Button)root.findViewById(R.id.ajouter)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDatasFromViews();
                if (jsonObject != null) {
                    AjouterRepasTask ajouterRepasTask = new AjouterRepasTask(uid, api_key, jsonObject.toString());
                    JSONObject jsonobject1 = null;
                    try {
                        jsonobject1 = ajouterRepasTask.execute().get();
                        if (jsonobject1.has("error")) {
                            Toast.makeText(getActivity(), "[-] Une erreur est survenue : " + jsonobject1.getString("error"), Toast.LENGTH_LONG).show();
                        } else if (jsonobject1.has("status") && jsonobject1.getString("status").equals("ok")) {
                            Toast.makeText(getActivity(), "[+] L'ajout du repas à été effectuée avec succès. Vous pouvez envoyer une photo.", Toast.LENGTH_LONG).show();
                            mealid = jsonobject1.getString("id");
                        } else if (jsonobject1.has("title")) {
                            Toast.makeText(getActivity(), "[-] Une erreur est survenue : vérifiez le champs 'Titre du repas'", Toast.LENGTH_LONG).show();
                        } else if (jsonobject1.has("description")) {
                            Toast.makeText(getActivity(), "[-] Une erreur est survenue : vérifiez le champs 'Description du repas'", Toast.LENGTH_LONG).show();
                        } else if (jsonobject1.has("places")) {
                            Toast.makeText(getActivity(), "[-] Une erreur est survenue : vérifiez le champs 'Places'", Toast.LENGTH_LONG).show();
                        } else if (jsonobject1.has("price")) {
                            Toast.makeText(getActivity(), "[-] Une erreur est survenue : vérifiez le champs 'Prix'", Toast.LENGTH_LONG).show();
                        } else if (jsonobject1.has("start_date")) {
                            Toast.makeText(getActivity(), "[-] Une erreur est survenue : vérifiez les champs 'Date' et 'Heure'", Toast.LENGTH_LONG).show();
                        } else if (jsonobject1.has("duration")) {
                            Toast.makeText(getActivity(), "[-] Une erreur est survenue : vérifiez le champs 'Durée'", Toast.LENGTH_LONG).show();
                        } else if (jsonobject1.has("longitude") || jsonobject1.has("latitude")) {
                            Toast.makeText(getActivity(), "[-] Une erreur est survenue : vérifiez le champs 'Lieu'", Toast.LENGTH_LONG).show();
                        }
                    } catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "[-] Erreur lors de la récupération des valeurs du formulaire.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getDatasFromViews() {
        try {
            jsonObject = new JSONObject();
            jsonObject.put("title", ((EditText) root.findViewById(R.id.title)).getText().toString());
            jsonObject.put("description", ((EditText) root.findViewById(R.id.description)).getText().toString());
            jsonObject.put("places", ((EditText) root.findViewById(R.id.places)).getText().toString());
            jsonObject.put("price", ((EditText) root.findViewById(R.id.price)).getText().toString());
            jsonObject.put("start_date", ((EditText) root.findViewById(R.id.date)).getText().toString() + " " + ((EditText) root.findViewById(R.id.heure)).getText().toString());
            jsonObject.put("duration", ((EditText) root.findViewById(R.id.duree)).getText().toString());
            List<Double> location = ((MainActivity)getActivity()).getLocation(((EditText) root.findViewById(R.id.lieu)).getText().toString());
            if (location != null) {
                jsonObject.put("latitude", String.valueOf(location.get(0)));
                jsonObject.put("longitude", String.valueOf(location.get(1)));
            } else {
                Toast.makeText(getActivity(), "[-] Impossible de récupérer la localisation. Vérifiez l'adresse saisie.", Toast.LENGTH_LONG).show();
            }
            jsonObject.put("badge_smoke", ((CheckBox) root.findViewById(R.id.fumeur)).isChecked() ? "1" : "0");
            jsonObject.put("badge_animal", ((CheckBox) root.findViewById(R.id.animaux)).isChecked() ? "1" : "0");
            jsonObject.put("badge_musique", ((CheckBox) root.findViewById(R.id.musique)).isChecked() ? "1" : "0");
            jsonObject.put("badge_halal", ((CheckBox) root.findViewById(R.id.hallal)).isChecked() ? "1" : "0");
            jsonObject.put("badge_casher", ((CheckBox) root.findViewById(R.id.casher)).isChecked() ? "1" : "0");
            jsonObject.put("auto_approval", ((CheckBox) root.findViewById(R.id.autovalidation)).isChecked() ? "1" : "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(((MainActivity)getActivity()).getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentDateandTime = sdf.format(new Date());
                ((CircleImageView) root.findViewById(R.id.photo)).setImageBitmap(bitmap);
                UploadImageTask uploadImageTask = new UploadImageTask(getActivity(), uid, api_key, ((MainActivity) getActivity()).bitmapToFile(bitmap, "meal_" + currentDateandTime), "meal_" + currentDateandTime, 1, mealid);
                String ret = uploadImageTask.execute().get();
                Log.i("RET", ret);
                if (ret.contains("error")) {
                    Toast.makeText(getActivity(), "[-] Une erreur est survenue lors de l'envoi de la photo. Veuillez recommencer.", Toast.LENGTH_LONG).show();
                } else if (ret.contains("ok")) {
                    Toast.makeText(getActivity(), "[+] Mise à jour de la photo effectuée.", Toast.LENGTH_LONG).show();
                }
            } catch (ExecutionException | InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
