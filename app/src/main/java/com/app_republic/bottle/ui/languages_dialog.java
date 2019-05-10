package com.app_republic.bottle.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app_republic.bottle.R;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.Interest;
import com.app_republic.bottle.model.User;
import com.app_republic.bottle.service.ServiceUtils;
import com.app_republic.bottle.service.info_interface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by elhadj on 17/09/2018.
 */

public class languages_dialog extends DialogFragment {

    private ArrayList<Interest> languages = new ArrayList<>();
    private RecyclerView recyclerView;
    private Adapter adapter;
    private User user;
    private Button save;
    private ProgressBar progressBar;
    private info_interface info;


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

        View rootView = inflater.inflate(R.layout.interests_layout, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerview);
        save = rootView.findViewById(R.id.save);
        progressBar = rootView.findViewById(R.id.progressBar);
        TextView title = rootView.findViewById(R.id.title);

        title.setText(title.getText().toString().replace("interests","languages"));


        user = SharedPreferenceHelper.getInstance(getActivity()).getUserInfo();
        loadLanguages();
        adapter = new Adapter(getActivity(), languages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(null);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                  FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID).child("languages").setValue(user.languages).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private ArrayList<Interest> list;

        Adapter(Context context, ArrayList<Interest> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(context).inflate(R.layout.interests_item, parent, false);
            return new viewHolder(view);


        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            Interest interest = list.get(position);
            ((viewHolder) holder).interest.setText(interest.name);

            if (user.languages.contains(interest.name))
                ((viewHolder) holder).checkBox.setChecked(true);
            else
                ((viewHolder) holder).checkBox.setChecked(false);


        }


        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private class viewHolder extends RecyclerView.ViewHolder {
        TextView interest;
        CheckBox checkBox;
        RelativeLayout relativeLayout;

        viewHolder(View itemView) {
            super(itemView);
            interest = itemView.findViewById(R.id.interest);
            checkBox = itemView.findViewById(R.id.checkBox);
            relativeLayout = itemView.findViewById(R.id.rel);

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean b;
                    if (checkBox.isChecked()){
                        checkBox.setChecked(false);
                        b = false;
                        user.languages = user.languages.replace("/"+languages.get(getAdapterPosition()).name,"");
                        languages.get(getAdapterPosition()).checked = b;
                        } else {
                        if (ServiceUtils.getCount(user.languages) < 5){
                            checkBox.setChecked(true);
                            b = true;
                            user.languages = user.languages + "/" + languages.get(getAdapterPosition()).name;
                            languages.get(getAdapterPosition()).checked = b;
                        }
                    }

                }
            });
        }
    }

    private void loadLanguages() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getActivity().getAssets().open("languages.txt")));

            String line;
            while ((line = reader.readLine()) != null) {
                languages.add(new Interest(line, false));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
