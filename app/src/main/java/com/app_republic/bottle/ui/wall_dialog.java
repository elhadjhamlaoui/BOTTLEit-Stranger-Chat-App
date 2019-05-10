package com.app_republic.bottle.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.text.emoji.widget.EmojiEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app_republic.bottle.R;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.User;
import com.app_republic.bottle.service.info_interface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by elhadj on 17/09/2018.
 */

public class wall_dialog extends DialogFragment {
    private User user;
    private Button save;
    private EmojiEditText wall;
    private ProgressBar progressBar;
    private TextView lenght;
    private info_interface info;
    private static final int MAX_LENGHT = 500;

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

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        info = (info_interface) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.color.cardview_dark_background);

        View rootView = inflater.inflate(R.layout.wall_layout, container, false);
        progressBar = rootView.findViewById(R.id.progressBar);
        save = rootView.findViewById(R.id.save);
        wall = rootView.findViewById(R.id.wall);
        lenght = rootView.findViewById(R.id.lenght);


        user = SharedPreferenceHelper.getInstance(getActivity()).getUserInfo();

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(null);

        wall.setText(user.wall);

        wall.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lenght.setText(MAX_LENGHT-charSequence.length()+"");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                user.wall = wall.getText().toString();
                FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID).child("wall").setValue(user.wall).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            alertDialogBuilder.setMessage(getString(R.string.success));
                            alertDialogBuilder.create().show();
                            SharedPreferenceHelper.getInstance(getActivity()).saveUserInfo(user);
                            info.update();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            alertDialogBuilder.setMessage(getString(R.string.error));
                            alertDialogBuilder.create().show();
                        }
                    }
                });
            }
        });
        return rootView;
    }
}
