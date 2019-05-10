package com.app_republic.bottle.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.DatePicker;
import android.widget.TextView;

import com.app_republic.bottle.R;
import com.app_republic.bottle.service.DateInterface;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class DateDialog extends DialogFragment {
    DateInterface dateInterface;
    Button next;
    DatePicker datePicker;
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
    public void onAttach(Activity activity) {
        dateInterface = (DateInterface) activity;
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.birthdate_layout, null);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.cardview_light_background);

        next = view.findViewById(R.id.next);
        datePicker = view.findViewById(R.id.datePicker);

        TextView title = view.findViewById(R.id.title);
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/candy.otf");

        title.setTypeface(type);

        setCancelable(false);



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateInterface.onDateSet(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());
                dismiss();
            }
        });

        return view;
    }

    /*  @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (DatePickerDialog.
                OnDateSetListener) getActivity(), year, month, day);
        datePickerDialog.setTitle(getString(R.string.set_birthdate));
        return datePickerDialog;
    }*/

}