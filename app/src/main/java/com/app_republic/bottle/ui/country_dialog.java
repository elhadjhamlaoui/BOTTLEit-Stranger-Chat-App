package com.app_republic.bottle.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app_republic.bottle.R;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.User;
import com.app_republic.bottle.service.ServiceUtils;
import com.app_republic.bottle.service.info_interface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by elhadj on 17/09/2018.
 */

public class country_dialog extends DialogFragment {

    private User user;
    private Button location;
    private TextView country;
    private info_interface info;
    private ProgressBar progressBar;


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

        View rootView = inflater.inflate(R.layout.country_layout, container, false);
        location = rootView.findViewById(R.id.location);
        country = rootView.findViewById(R.id.country);
        progressBar = rootView.findViewById(R.id.progressBar);


        user = SharedPreferenceHelper.getInstance(getActivity()).getUserInfo();

        country.setText(ServiceUtils.getCountryName(getActivity(),user.country));
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                findCountry();
            }
        });
        return rootView;
    }

    public void findCountry() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(null);

        String countryCodeValue;

        if (getActivity().getSystemService(Context.TELEPHONY_SERVICE) != null) {
            TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            countryCodeValue = tm.getNetworkCountryIso();
        } else {
            countryCodeValue = getResources().getConfiguration().locale.getCountry();
        }
        user.country = countryCodeValue.isEmpty() ? "gb" : countryCodeValue;
        FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID).child("country").setValue(user.country).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    alertDialogBuilder.setMessage(getString(R.string.success));
                    alertDialogBuilder.create().show();
                    SharedPreferenceHelper.getInstance(getActivity()).saveUserInfo(user);
                    info.update();
                    country.setText(ServiceUtils.getCountryName(getActivity(),user.country));
                } else {
                    progressBar.setVisibility(View.GONE);
                    alertDialogBuilder.setMessage(getString(R.string.error));
                    alertDialogBuilder.create().show();
                }
            }
        });


        /*RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://ip-api.com/json";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            String code = object.getString("countryCode");
                            user.country = code;
                            FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID).child("country").setValue(user.country).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        alertDialogBuilder.setMessage(getString(R.string.success));
                                        alertDialogBuilder.create().show();
                                        SharedPreferenceHelper.getInstance(getActivity()).saveUserInfo(user);
                                        info.update();
                                        country.setText(ServiceUtils.getCountryName(getActivity(),user.country));
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        alertDialogBuilder.setMessage(getString(R.string.error));
                                        alertDialogBuilder.create().show();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            alertDialogBuilder.setMessage(getString(R.string.error));
                            alertDialogBuilder.create().show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                alertDialogBuilder.setMessage(getString(R.string.error));
                alertDialogBuilder.create().show();
            }
        });

        queue.add(stringRequest);

        */
    }
}
