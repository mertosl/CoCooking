package com.mertos_l.cocookingfinaldesign;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ActionParticipationDialog extends DialogFragment {
    private View view;

    public ActionParticipationDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.dialog_action_participation, new RelativeLayout(getActivity()), false);
        final Bundle bundle = getArguments();
        if (bundle.getInt("type") == 0) {
            ((Button) view.findViewById(R.id.btn_decline)).setVisibility(View.GONE);
            ((Button) view.findViewById(R.id.btn_admit)).setVisibility(View.GONE);
            ((Button) view.findViewById(R.id.btn_cancel)).setVisibility(View.VISIBLE);
            ((Button) view.findViewById(R.id.btn_confirm)).setVisibility(View.VISIBLE);
            ((Button) view.findViewById(R.id.btn_cancel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogConfirmation(bundle.getInt("type"), bundle.getString("idorder"), 0);
                }
            });
            ((Button) view.findViewById(R.id.btn_confirm)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogConfirmation(bundle.getInt("type"), bundle.getString("idorder"), 1);
                }
            });
        } else if (bundle.getInt("type") == 1) {
            ((Button) view.findViewById(R.id.btn_decline)).setVisibility(View.VISIBLE);
            ((Button) view.findViewById(R.id.btn_admit)).setVisibility(View.VISIBLE);
            ((Button) view.findViewById(R.id.btn_cancel)).setVisibility(View.GONE);
            ((Button) view.findViewById(R.id.btn_confirm)).setVisibility(View.GONE);
            view = getActivity().getLayoutInflater().inflate(R.layout.dialog_action_participation, new RelativeLayout(getActivity()), false);
            ((Button) view.findViewById(R.id.btn_admit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogConfirmation(bundle.getInt("type"), bundle.getString("idorder"), 0);
                }
            });
            ((Button) view.findViewById(R.id.btn_decline)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogConfirmation(bundle.getInt("type"), bundle.getString("idorder"), 1);
                }
            });
        }
        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setContentView(view);
        return builder;
    }

    private void showDialogConfirmation(final int type, final String id, final int i) {
        if (type == 0) {
            if (i == 0) {
                ((TextView) view.findViewById(R.id.textView_cancel)).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.textView_decline)).setVisibility(View.GONE);
                ((TextView) view.findViewById(R.id.textView_admit)).setVisibility(View.GONE);
                ((TextView) view.findViewById(R.id.textView_confirm)).setVisibility(View.GONE);
            } else {
                ((TextView) view.findViewById(R.id.textView_cancel)).setVisibility(View.GONE);
                ((TextView) view.findViewById(R.id.textView_decline)).setVisibility(View.GONE);
                ((TextView) view.findViewById(R.id.textView_admit)).setVisibility(View.GONE);
                ((TextView) view.findViewById(R.id.textView_confirm)).setVisibility(View.VISIBLE);
            }
        } else {
            if (i == 0) {
                ((TextView) view.findViewById(R.id.textView_cancel)).setVisibility(View.GONE);
                ((TextView) view.findViewById(R.id.textView_decline)).setVisibility(View.GONE);
                ((TextView) view.findViewById(R.id.textView_admit)).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.textView_confirm)).setVisibility(View.GONE);
            } else {
                ((TextView) view.findViewById(R.id.textView_cancel)).setVisibility(View.GONE);
                ((TextView) view.findViewById(R.id.textView_decline)).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.textView_admit)).setVisibility(View.GONE);
                ((TextView) view.findViewById(R.id.textView_confirm)).setVisibility(View.GONE);
            }
        }
        ((RelativeLayout) view.findViewById(R.id.container_action)).setVisibility(View.GONE);
        ((LinearLayout) view.findViewById(R.id.container_confirmation)).setVisibility(View.VISIBLE);
        ((Button) view.findViewById(R.id.btn_oui)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent();
                Bundle b = new Bundle();
                b.putString("orderid", id);
                b.putString("action", String.valueOf((type == 0 && i == 0 ? 0 : (type == 0 && i == 1 ? 3 : (type == 1 && i == 0 ? 1 : 2)))));
                intent.putExtras(b);
                getTargetFragment().onActivityResult(getTargetRequestCode(), i, intent);
                ((RelativeLayout) view.findViewById(R.id.container_action)).setVisibility(View.VISIBLE);
                ((LinearLayout) view.findViewById(R.id.container_confirmation)).setVisibility(View.GONE);
            }
        });
        ((Button) view.findViewById(R.id.btn_non)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                ((RelativeLayout) view.findViewById(R.id.container_action)).setVisibility(View.VISIBLE);
                ((LinearLayout) view.findViewById(R.id.container_confirmation)).setVisibility(View.GONE);
            }
        });
    }
}