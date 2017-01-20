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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import de.hdodenhof.circleimageview.CircleImageView;
import static android.app.Activity.RESULT_OK;

public class ProfilPersonnelFragment extends Fragment {
    private static View root = null;
    private String uid;
    private String api_key;
    private Bitmap bitmap = null;
    static final int REQUEST_IMAGE_CAPTURE = 25;

    public ProfilPersonnelFragment() {
    }

    public static Fragment newInstance(String uid, String api_key) {
        ProfilPersonnelFragment fragment = new ProfilPersonnelFragment();
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
        root = inflater.inflate(R.layout.fragment_profil_personnel, container, false);
        initViews();
        return root;
    }

    private void initViews() {
        ProfileTask profileTask = new ProfileTask(uid, api_key, uid);
        try {
            JSONObject jsonObject = profileTask.execute().get();
            if (jsonObject.has("avatar") && !jsonObject.getString("avatar").equals("null")) {
                DownloadImageTask downloadImageTask = new DownloadImageTask(jsonObject.getString("avatar"), getActivity());
                Bitmap bitmap = downloadImageTask.execute().get();
                if (bitmap != null) {
                    ((CircleImageView) root.findViewById(R.id.photo)).setImageBitmap(bitmap);
                } else {
                    Toast.makeText(getActivity(), "[-] Une erreur est survenu lors de la récupération de la photo de profile.", Toast.LENGTH_LONG).show();
                }
            }
            if (jsonObject.has("firstname") && !jsonObject.getString("firstname").equals("null"))
                ((EditText)root.findViewById(R.id.first_name)).setText(jsonObject.getString("firstname"));
            if (jsonObject.has("lastname") && !jsonObject.getString("lastname").equals("null"))
                ((EditText)root.findViewById(R.id.familly_name)).setText(jsonObject.getString("lastname"));
            if (jsonObject.has("bday") && !jsonObject.getString("bday").equals("null"))
                ((EditText)root.findViewById(R.id.birthday)).setText(jsonObject.getString("bday"));
            if (jsonObject.has("gender") && !jsonObject.getString("gender").equals("null"))
                if (jsonObject.getString("gender").equals("1")) {
                    ((Spinner)root.findViewById(R.id.spinner)).setSelection(2);
                } else if (jsonObject.getString("gender").equals("0")) {
                    ((Spinner)root.findViewById(R.id.spinner)).setSelection(1);
                } else {
                    ((Spinner)root.findViewById(R.id.spinner)).setSelection(0);
                }
            if (jsonObject.has("phone") && !jsonObject.getString("phone").equals("null"))
                ((EditText)root.findViewById(R.id.telephone)).setText(jsonObject.getString("phone"));
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
        ((CircleImageView)root.findViewById(R.id.photo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((MainActivity) getActivity()).isFeatureAvailable(PackageManager.FEATURE_CAMERA)) {
                    dispatchTakePictureIntent();
                }
            }
        });
        ((Button)root.findViewById(R.id.btn_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("firstname", ((EditText)root.findViewById(R.id.first_name)).getText().toString().isEmpty() ? "" : ((EditText)root.findViewById(R.id.first_name)).getText().toString());
                    jsonObject.put("lastname", ((EditText)root.findViewById(R.id.familly_name)).getText().toString().isEmpty() ? "" : ((EditText)root.findViewById(R.id.familly_name)).getText().toString());
                    jsonObject.put("bday", ((EditText)root.findViewById(R.id.birthday)).getText().toString().isEmpty() ? "" : ((EditText)root.findViewById(R.id.birthday)).getText().toString());
                    jsonObject.put("gender", ((Spinner)root.findViewById(R.id.spinner)).getSelectedItemPosition() == 0 ? "" : ((Spinner)root.findViewById(R.id.spinner)).getSelectedItemPosition() == 1 ? "0" : "1");
                    jsonObject.put("phone", ((EditText)root.findViewById(R.id.telephone)).getText().toString().isEmpty() ? "" : ((EditText)root.findViewById(R.id.telephone)).getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                UpdateProfilTask updateProfilTask = new UpdateProfilTask(uid, api_key, jsonObject.toString());
                try {
                    String res = updateProfilTask.execute().get();
                    if (res.equals("\"OK\""))
                        Toast.makeText(getActivity(), "[+] Vos nouvelles informations ont bien été sauvegardées.", Toast.LENGTH_LONG).show();
                    else if (res.contains("firstname"))
                        Toast.makeText(getActivity(), "[-] Vérifier le champ \"Prénom\".", Toast.LENGTH_LONG).show();
                    else if (res.contains("lastname"))
                        Toast.makeText(getActivity(), "[-] Vérifier le champ \"Nom de famille\".", Toast.LENGTH_LONG).show();
                    else if (res.contains("bday"))
                        Toast.makeText(getActivity(), "[-] Vérifier le champ \"Date de naissance\".", Toast.LENGTH_LONG).show();
                    else if (res.contains("gender"))
                        Toast.makeText(getActivity(), "[-] Vérifier le champ \"Sexe\".", Toast.LENGTH_LONG).show();
                    else if (res.contains("phone"))
                        Toast.makeText(getActivity(), "[-] Vérifier le champ \"Téléphone\".", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getActivity(), "[-] Une erreur est sur venue.", Toast.LENGTH_LONG).show();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
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
            if (bitmap != null) {
                ((CircleImageView) root.findViewById(R.id.photo)).setImageBitmap(bitmap);
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String currentDateandTime = sdf.format(new Date());
                    UploadImageTask uploadImageTask = new UploadImageTask(getActivity(), uid, api_key, ((MainActivity) getActivity()).bitmapToFile(bitmap, "user_" + currentDateandTime), "user_" + currentDateandTime, 0, "");
                    String ret = uploadImageTask.execute().get();
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
}
