package com.mertos_l.cocookingfinaldesign;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends Activity {
    private String path = "http://dev.cocooking.eu/_assets/videos/rendu_2_leger.mp4";
    private AuthentificationTask authentificationTask;
    private TextInputEditText emailView;
    private TextInputEditText passwordView;
    private Boolean bVideoIsBeingTouched = false;
    View focusedView = null;
    boolean cancel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    private void initViews() {
        final VideoView mVideoView = (VideoView) findViewById(R.id.videoView);
        mVideoView.setVideoPath(path);
        mVideoView.setMediaController(null);
        mVideoView.requestFocus();
        mVideoView.start();
        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!bVideoIsBeingTouched) {
                    bVideoIsBeingTouched = true;
                    if (mVideoView.isPlaying()) {
                        mVideoView.stopPlayback();
                        onClickVideo();
                    }
                }
                return true;
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                onClickVideo();
            }
        });
        ((TextView) findViewById(R.id.titre)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/remachinescript_personal_use.ttf"));
        emailView = (TextInputEditText) findViewById(R.id.email);
        passwordView = (TextInputEditText) findViewById(R.id.password);
        Button connection = (Button) findViewById(R.id.btn_login);
        connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryLogin(0);
            }
        });
        TextView enregistrement = (TextView) findViewById(R.id.register);
        enregistrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryLogin(1);
            }
        });
    }

    private void tryLogin(int i) {
        String email;
        String password;

        emailView.setError(null);
        passwordView.setError(null);
        email = emailView.getText().toString();
        password = passwordView.getText().toString();
        if (email.isEmpty()) {
            emailView.setError("Ce champs est requis!");
            focusedView = emailView;
            focusedView.requestFocus();
            initViews();
        } else if (!isEmailValid(email)) {
            emailView.setError("Format de l'email invalide!");
            focusedView = emailView;
            focusedView.requestFocus();
            initViews();
        } else if (password.isEmpty()) {
            passwordView.setError("Ce champs est requis!");
            focusedView = passwordView;
            focusedView.requestFocus();
            initViews();
        } else if (!isPasswordValid(password)) {
            passwordView.setError("Le mot de passe doit contenir au moins:\n" +
                    "-une majuscule\n" +
                    "-un chiffre\n" +
                    "-8 caractères!");
            focusedView = passwordView;
            focusedView.requestFocus();
            initViews();
        }
        JSONObject ret;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            if (i == 0) {
                authentificationTask = new AuthentificationTask(this, jsonObject.toString(), 0);
                ret = new JSONObject(authentificationTask.execute().get());
                if (ret != null && ret.has("error") && ret.getString("error").equals("Bad credentials")) {
                    emailView.setError("L'adresse mail et/ou le mot de passe semble(nt) incorrecte(s).");
                    focusedView = emailView;
                    focusedView.requestFocus();
                    initViews();
                } else if (ret != null && ret.has("uid")) {
                    Toast.makeText(this, "Connexion éffectuée avec succès!", Toast.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("email", email);
                    bundle.putString("uid", ret.getString("uid"));
                    bundle.putString("api_key", ret.getString("api_key"));
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtras(bundle);
                    finish();
                    startActivity(intent);
                }
            } else {
                authentificationTask = new AuthentificationTask(this, jsonObject.toString(), 1);
                ret = new JSONObject(authentificationTask.execute().get());
                if (ret != null && ret.has("email")) {
                    emailView.setError("Format de l'email invalide!");
                    focusedView = emailView;
                    focusedView.requestFocus();
                    initViews();
                } else if (ret != null && ret.has("password")) {
                    passwordView.setError("Le mot de passe doit contenir au moins:\n" +
                            "-une majuscule\n" +
                            "-un chiffre\n" +
                            "-8 caractères!");
                    focusedView = passwordView;
                    focusedView.requestFocus();
                    initViews();
                } else if (ret != null && ret.toString().contains("OK")) {
                    Toast.makeText(this, "Inscription effectuée avec succès. Vous pouvez vous connecter dès à présent.", Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void onClickVideo() {
        ((VideoView) findViewById(R.id.videoView)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.container_default)).setVisibility(View.VISIBLE);
    }

    private boolean isEmailValid(String email) { return email.contains("@") && email.contains("."); }
    private boolean isPasswordValid(String password) { return password.length() >= 8; }
}
