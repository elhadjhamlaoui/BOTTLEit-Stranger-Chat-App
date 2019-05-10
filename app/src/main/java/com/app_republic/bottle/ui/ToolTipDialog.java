package com.app_republic.bottle.ui;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.app_republic.bottle.R;

public class ToolTipDialog extends DialogFragment {

    TextView title,body;
    Button button;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tool_tip, null);

        title = view.findViewById(R.id.title);
        body = view.findViewById(R.id.body);
        button = view.findViewById(R.id.ok);


        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/candy.otf");

        title.setTypeface(type);
        body.setTypeface(type);

        title.setText(getArguments().getString("title"));
        body.setText(getArguments().getString("body"));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }
}
