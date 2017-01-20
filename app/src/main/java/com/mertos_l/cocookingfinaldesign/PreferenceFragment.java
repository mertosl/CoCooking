package com.mertos_l.cocookingfinaldesign;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PreferenceFragment extends Fragment {
    private static View root = null;
    private String uid;
    private String api_key;

    public PreferenceFragment() {
    }

    public static Fragment newInstance(String uid, String api_key) {
        PreferenceFragment fragment = new PreferenceFragment();
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
        root = inflater.inflate(R.layout.fragment_preference, container, false);
        getDatasFromViews();
        return root;
    }

    void getDatasFromViews() {
    }
}
