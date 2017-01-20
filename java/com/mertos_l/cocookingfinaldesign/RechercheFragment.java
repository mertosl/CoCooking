package com.mertos_l.cocookingfinaldesign;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;

public class RechercheFragment extends Fragment {
    private static View root = null;
    private String uid;
    private String api_key;

    private static String TAG = MainActivity.class.getSimpleName();
    private PlacesAutoCompleteAdapter mAdapter;
    HandlerThread mHandlerThread;
    Handler mThreadHandler;

    // format date : jj-mm-yyyy

    public RechercheFragment() {
        if (mThreadHandler == null) {
            // Initialize and start the HandlerThread
            // which is basically a Thread with a Looper
            // attached (hence a MessageQueue)
            mHandlerThread = new HandlerThread(TAG, android.os.Process.THREAD_PRIORITY_BACKGROUND);
            mHandlerThread.start();

            // Initialize the Handler
            mThreadHandler = new Handler(mHandlerThread.getLooper()) {
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        ArrayList<String> results = mAdapter.resultList;

                        if (results != null && results.size() > 0) {
                            mAdapter.notifyDataSetChanged();
                        }
                        else {
                            mAdapter.notifyDataSetInvalidated();
                        }
                    }
                }
            };
        }
    }

    public static Fragment newInstance(String uid, String api_key) {
        RechercheFragment fragment = new RechercheFragment();
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
        root = inflater.inflate(R.layout.fragment_recherche, container, false);
        mAdapter = new PlacesAutoCompleteAdapter((MainActivity)getActivity(), R.layout.fragment_recherche_item);
        try {
            initViews();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }

    private void initViews() throws IOException {
        ((TextView) root.findViewById(R.id.slogan)).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/remachinescript_personal_use.ttf"));
        ((TextView) root.findViewById(R.id.slogan_lieu)).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/remachinescript_personal_use.ttf"));
        ((TextView) root.findViewById(R.id.slogan_date)).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/remachinescript_personal_use.ttf"));
        ((TextView) root.findViewById(R.id.slogan_mot_cle)).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/remachinescript_personal_use.ttf"));
        ((Button) root.findViewById(R.id.btn_valider)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).showResultatTabs(((AutoCompleteTextView) root.findViewById(R.id.address)).getText().toString(),
                        ((EditText) root.findViewById(R.id.date)).getText().toString(),
                        ((EditText) root.findViewById(R.id.words)).getText().toString());
            }
        });

        AutoCompleteTextView autoCompView = (AutoCompleteTextView) root.findViewById(R.id.address);
        autoCompView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.fragment_recherche_item));
        autoCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                String description = (String) parent.getItemAtPosition(position);
                Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
            }
        });
        autoCompView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String value = s.toString();
                // Remove all callbacks and messages
                mThreadHandler.removeCallbacksAndMessages(null);
                // Now add a new one
                mThreadHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Background thread
                        mAdapter.resultList = mAdapter.mPlaceAPI.autocomplete(value);
                        // Footer
                        if (mAdapter.resultList.size() > 0)
                            mAdapter.resultList.add("footer");
                        // Post to Main Thread
                        mThreadHandler.sendEmptyMessage(1);
                    }
                }, 500);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Get rid of our Place API Handlers
        if (mThreadHandler != null) {
            mThreadHandler.removeCallbacksAndMessages(null);
            mHandlerThread.quit();
        }
    }
}
