package com.app_republic.bottle.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app_republic.bottle.R;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.User;
import com.app_republic.bottle.service.UpdateInterface;
import com.app_republic.bottle.service.info_interface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by elhadj on 17/09/2018.
 */

public class item_dialog extends DialogFragment {

    private User user;
    private Button buy;
    private ImageView icon;
    private TextView name, desc, amount;
    private int price;
    private ProgressBar progressBar;
    private info_interface info;
    private String key;
    private AlertDialog.Builder alertDialogBuilder;
    UpdateInterface updateInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        updateInterface = (UpdateInterface) context;
    }

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.color.cardview_dark_background);

        View rootView = inflater.inflate(R.layout.item_dialog, container, false);
        buy = rootView.findViewById(R.id.buy);
        progressBar = rootView.findViewById(R.id.progressBar);
        icon = rootView.findViewById(R.id.icon);
        desc = rootView.findViewById(R.id.desc);
        name = rootView.findViewById(R.id.name);
        amount = rootView.findViewById(R.id.price);


        price = getArguments().getInt("price");
        amount.setText(price + " coins");
        name.setText(getArguments().getString("name"));
        desc.setText(getArguments().getString("desc"));
        icon.setImageResource(getArguments().getInt("icon"));

        key = getArguments().getString("key");


        user = SharedPreferenceHelper.getInstance(getActivity()).getUserInfo();

        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID).child("gold").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int gold = Integer.parseInt(dataSnapshot.getValue().toString());
                        if (gold >= price)
                            FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID).child("gold").setValue(gold - price).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                int number = Integer.parseInt(dataSnapshot.getValue().toString());
                                                FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID).child(key).setValue(number + 1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            showSuccess();
                                                        } else {
                                                            showError();
                                                        }
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                showError();

                                            }
                                        });
                                    } else {
                                        showError();

                                    }
                                }
                            });
                        else
                            showInsufficient();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showError();

                    }
                });

            }
        });
        return rootView;
    }

    private void showInsufficient() {
        progressBar.setVisibility(View.GONE);
        alertDialogBuilder.setMessage(getString(R.string.insufficient));
        alertDialogBuilder.setTitle(null);
        alertDialogBuilder.setPositiveButton(getString(R.string.buy_gold), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(getActivity(), GoldActivity.class));
            }
        });
        alertDialogBuilder.create().show();
    }

    public void showError() {
        progressBar.setVisibility(View.GONE);
        alertDialogBuilder.setMessage(getString(R.string.error));
        alertDialogBuilder.setTitle(null);
        alertDialogBuilder.create().show();
    }

    public void showSuccess() {
        progressBar.setVisibility(View.GONE);
        alertDialogBuilder.setMessage("You have purchased "+amount.getText().toString()+" successfully");
        alertDialogBuilder.setTitle(null);
        alertDialogBuilder.setPositiveButton(R.string.ok,null);
        alertDialogBuilder.create().show();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        updateInterface.update();
    }
}
