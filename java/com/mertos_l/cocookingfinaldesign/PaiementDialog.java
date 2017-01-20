package com.mertos_l.cocookingfinaldesign;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

public class PaiementDialog extends DialogFragment {
    private View view;

    public PaiementDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_paiement, new RelativeLayout(getActivity()), false);
        ((Button) view.findViewById(R.id.btn_paymill)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaymillDialog();
            }
        });
        ((Button) view.findViewById(R.id.btn_cagnotte)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCagnotteDialog();
            }
        });

        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setContentView(view);
        return builder;
    }

    private void showPaymillDialog() {
        ((RelativeLayout) view.findViewById(R.id.container_paiement)).setVisibility(View.GONE);
        ((RelativeLayout) view.findViewById(R.id.container_paymill)).setVisibility(View.VISIBLE);
        ((Button) view.findViewById(R.id.btn_oui_paymill)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);
            }
        });
        ((Button) view.findViewById(R.id.btn_non_paymill)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void showCagnotteDialog() {
        ((RelativeLayout) view.findViewById(R.id.container_paiement)).setVisibility(View.GONE);
        ((RelativeLayout) view.findViewById(R.id.container_cagnotte)).setVisibility(View.VISIBLE);
        ((Button) view.findViewById(R.id.btn_oui_cagnotte)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                getTargetFragment().onActivityResult(getTargetRequestCode(), 1, null);
            }
        });
        ((Button) view.findViewById(R.id.btn_non_cagnotte)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}