package com.app_republic.bottle.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.app_republic.bottle.R;
import com.squareup.picasso.Picasso;

/**
 * Created by elhadj on 07/09/2018.
 */


/**
 * Created by neo on 01/01/2017.
 */


public class play_image extends DialogFragment {
    Dialog dialog;

    ImageView imageView;
    FloatingActionButton close,share;
    @Override
    public void onStart() {

        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

         dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getDialog().getWindow().setBackgroundDrawableResource(R.color.cardview_dark_background);

        View view = inflater.inflate(R.layout.play_image, null);

        imageView = view.findViewById(R.id.imageView);
        close = view.findViewById(R.id.close);
        share = view.findViewById(R.id.share);


        Picasso.get()
                .load(getArguments().getString("image"))
                .into(imageView);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(getArguments().getString("image")));
                startActivity(Intent.createChooser(shareIntent, "Share image using"));
            }
        });






        return  view;
    }

}

