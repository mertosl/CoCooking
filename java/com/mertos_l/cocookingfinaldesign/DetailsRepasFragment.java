package com.mertos_l.cocookingfinaldesign;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsRepasFragment extends Fragment {
    private static View root = null;
    private String uid;
    private String api_key;
    private String mealid;
    private String host;
    private String seats;
    private String token;
    private String number_;
    private String month_;
    private String year_;
    private String cvc_;

    public DetailsRepasFragment() {
    }

    public static Fragment newInstance(String uid, String api_key, String mealid, String host) {
        DetailsRepasFragment fragment = new DetailsRepasFragment();
        Bundle args = new Bundle();
        args.putString("uid", uid);
        args.putString("api_key", api_key);
        args.putString("mealid", mealid);
        args.putString("host", host);
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
            host = getArguments().getString("host");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_details_repas, container, false);
        initViews();
        return root;
    }

    private void initViews() {
        DetailsRepasTask detailsRepasTask = new DetailsRepasTask(mealid);
        try {
            JSONObject jsonObject = detailsRepasTask.execute().get();
            if (jsonObject != null) {
                if (jsonObject.has("pictures") && jsonObject.optJSONArray("pictures").length() > 0) {
                    DownloadImageTask downloadImageTask = new DownloadImageTask("http://dev.cocooking.eu/media/" + jsonObject.optJSONArray("pictures").optJSONObject(0).getString("path"), getActivity());
                    Bitmap bitmap = downloadImageTask.execute().get();
                    if (bitmap != null)
                        ((CircleImageView) root.findViewById(R.id.photo)).setImageBitmap(bitmap);
                }
                if (jsonObject.has("title") && !jsonObject.getString("title").isEmpty())
                    ((TextView) root.findViewById(R.id.title)).setText(jsonObject.getString("title"));
                if (jsonObject.has("host") && !jsonObject.getString("host").isEmpty())
                    host = jsonObject.getString("host");
                if (jsonObject.has("description") && !jsonObject.getString("description").isEmpty())
                    ((TextView) root.findViewById(R.id.description)).setText(jsonObject.getString("description"));
                if (jsonObject.has("start_date") && !jsonObject.getString("start_date").isEmpty())
                    ((TextView) root.findViewById(R.id.date)).setText(jsonObject.getString("start_date"));
                if (jsonObject.has("price") && !jsonObject.getString("price").isEmpty())
                    ((TextView) root.findViewById(R.id.prix)).setText(jsonObject.getString("price") + "€ / Personne");
                ((CheckBox) root.findViewById(R.id.fumeur)).setChecked(jsonObject.has("badge_smoke") && jsonObject.getString("badge_smoke").equals("1"));
                ((CheckBox) root.findViewById(R.id.animaux)).setChecked(jsonObject.has("badge_animal") && jsonObject.getString("badge_animal").equals("1"));
                ((CheckBox) root.findViewById(R.id.musique)).setChecked(jsonObject.has("badge_musique") && jsonObject.getString("badge_musique").equals("1"));
                ((CheckBox) root.findViewById(R.id.hallal)).setChecked(jsonObject.has("badge_halal") && jsonObject.getString("badge_halal").equals("1"));
                ((CheckBox) root.findViewById(R.id.casher)).setChecked(jsonObject.has("badge_casher") && jsonObject.getString("badge_casher").equals("1"));
                ((CheckBox) root.findViewById(R.id.autovalidation)).setChecked(jsonObject.has("auto_approval") && jsonObject.getString("auto_approval").equals("1"));
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
        if (!uid.equals(host)) {
            ((Button) root.findViewById(R.id.btn_reserver)).setVisibility(View.VISIBLE);
            ((Button) root.findViewById(R.id.btn_reserver)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });
        } else {
            ((Button) root.findViewById(R.id.btn_reserver)).setBackgroundColor(((MainActivity)getActivity()).getColor(getActivity(), R.color.colorGris));
        }
    }

    private void showDialog() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        PaiementDialog dialog = new PaiementDialog ();
        dialog.setTargetFragment(this, 0);
        dialog.show(ft, "dialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == 0) {
                // paiement par carte
                showDialogPaiement();

            } else if (resultCode == 1) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("mealid", mealid);
                    jsonObject.put("seats", ((Spinner) root.findViewById(R.id.spinner)).getSelectedItem().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                PaymentCagnotteTask paymentCagnotte = new PaymentCagnotteTask(uid, api_key, jsonObject.toString());
                try {
                    JSONObject ret = paymentCagnotte.execute().get();
                    if (ret != null) {
                        if (ret.has("status") && "ok".equals(ret.getString("status"))) {
                            Toast.makeText((MainActivity) getActivity(), "[+] Payment effectué avec succès!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText((MainActivity) getActivity(), "[-] Crédit insuffisant.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText((MainActivity) getActivity(), "[-] Erreur lors du payment.", Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException | ExecutionException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showDialogPaiement() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LinearLayout layout = new LinearLayout(getActivity());

        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText number = new EditText(getActivity());
        number.setHint("Numéro de Carte");
        layout.addView(number);
        final EditText month = new EditText(getActivity());
        month.setHint("Mois");
        layout.addView(month);
        final EditText year = new EditText(getActivity());
        year.setHint("Année");
        layout.addView(year);
        final EditText cvc = new EditText(getActivity());
        cvc.setHint("CVC");
        layout.addView(cvc);

        alert.setTitle("Paiement bancaire");
        alert.setView(layout);
        alert.setPositiveButton("Payer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                number_ = number.getText().toString();
                month_ = month.getText().toString();
                year_ = year.getText().toString();
                cvc_ = cvc.getText().toString();
                Card card = new Card(number_, Integer.valueOf(month_), Integer.valueOf(year_), cvc_);
                if (!card.validateCard()) {
                    Toast.makeText(getActivity(), "[-] Carte Bancaire invalide. Veuillez recommencer l'opération.", Toast.LENGTH_LONG).show();
                } else {
                    Stripe stripe = null;
                    try {
                        stripe = new Stripe("pk_test_6MYN40BlJIc9m3x44I14xY2T");
                        stripe.createToken(
                                card,
                                new TokenCallback() {
                                    public void onSuccess(Token token) {
                                        // Send token to your server
                                        Toast.makeText(getActivity(), token.getId(), Toast.LENGTH_LONG).show();
                                        // POST methode {"mealid":"51", "seats":"1", "token":"token"}
                                        JSONObject jsoncb = new JSONObject();
                                        try {
                                            jsoncb.put("mealid", mealid);
                                            jsoncb.put("seats", ((Spinner) root.findViewById(R.id.spinner)).getSelectedItem().toString());
                                            jsoncb.put("token", token.getId());

                                            PaymentCBTask cbTask = new PaymentCBTask(uid, api_key, jsoncb.toString());
                                            JSONObject ret = cbTask.execute().get();
                                            if (ret.has("status") && ret.getString("status").equals("error")) {
                                                Toast.makeText(getActivity(), "[-] Une erreur est survenue. Veuillez recommencez la procédure de paiement par CB.", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getActivity(), "[+] Le paiement par CB a bien été effectuée.", Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException | InterruptedException | ExecutionException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    public void onError(Exception error) {
                                        // Show localized error message
                                        Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                        );
                    } catch (AuthenticationException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        alert.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Paiement annulé", Toast.LENGTH_LONG).show();
            }
        });
        alert.show();
    }
}
