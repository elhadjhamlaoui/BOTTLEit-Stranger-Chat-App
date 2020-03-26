package com.app_republic.bottle.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app_republic.bottle.R;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.model.Bottle_view;
import com.app_republic.bottle.model.Receiver;
import com.app_republic.bottle.model.User;
import com.mukesh.countrypicker.CountryPicker;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class my_bottle extends DialogFragment {

    RecyclerView recyclerView;
    Adapter adapter;
    CountryPicker picker;
    User user;
    Button show;
    ArrayList<Receiver> receivers = new ArrayList<>();
    Bottle_view bottle;
    TextView TV_floating;

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

        View rootView = inflater.inflate(R.layout.my_bottle_dialog, container, false);


        recyclerView = rootView.findViewById(R.id.recyclerview);
        show = rootView.findViewById(R.id.show);
        TV_floating = rootView.findViewById(R.id.floating_text);

        CountryPicker.Builder builder = new CountryPicker.Builder().with(getActivity());
        picker = builder.build();

        picker.getCountryByISO("CD").setName("Congo");
        picker.getCountryByISO("FM").setName("Micronesia");
        picker.getCountryByISO("VE").setName("Venezuela");
        picker.getCountryByISO("BO").setName("Bolivia");
        picker.getCountryByISO("IR").setName("Iran");
        picker.getCountryByISO("MD").setName("Moldova");
        picker.getCountryByISO("MK").setName("Macedonia");
        picker.getCountryByISO("PS").setName("Palestine");
        picker.getCountryByISO("TZ").setName("Tanzania");
        picker.getCountryByISO("LY").setName("Libya");

        bottle = getArguments().getParcelable("bottle");
        receivers = bottle.receiver;
        if (receivers.size() == 0)
            TV_floating.setVisibility(View.VISIBLE);
        user = SharedPreferenceHelper.getInstance(getActivity()).getUserInfo();
        adapter = new Adapter(getActivity(), receivers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                receivedBottle dialog = new receivedBottle();
                Bundle argumants = new Bundle();

                argumants.putString("class","my_bottle");
                argumants.putParcelable("bottle",bottle);
                dialog.setArguments(argumants);

                if(!getActivity().isDestroyed() && !getActivity().isFinishing())
                    getActivity().getSupportFragmentManager().beginTransaction().add(dialog, "my_bottle").commitAllowingStateLoss();

            }
        });

        return rootView;
    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
       private ArrayList<Receiver> list;

        Adapter(Context context, ArrayList<Receiver> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(context).inflate(R.layout.mybottle_item, parent, false);

            return new viewHolder(view);


        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            try {
                Receiver receiver = list.get(position);
                String country = receiver.getCountry();

                ((viewHolder) holder).country.setText(picker.getCountryByISO(country).getName());
                ((viewHolder) holder).date.setText(getDate(System.currentTimeMillis() - receiver.getTimeStamp()) + " ago");
                ((viewHolder) holder).icon.setImageResource(picker.getCountryByISO(country).getFlag());
                ((viewHolder) holder).name.setText(receiver.getName());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


        }


        @Override
        public int getItemCount() {
            return list.size();
        }

        public String getDate(long millis) {
            long days = TimeUnit.MILLISECONDS.toDays(millis);
            millis -= TimeUnit.DAYS.toMillis(days);
            long hours = TimeUnit.MILLISECONDS.toHours(millis);
            millis -= TimeUnit.HOURS.toMillis(hours);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);

            StringBuilder sb = new StringBuilder(64);

            if (days != 0) {
                sb.append(days);
                sb.append(" Days ");
            } else if (hours != 0) {
                sb.append(hours);
                sb.append(" Hours ");
            } else {
                sb.append(minutes);
                sb.append(" Minutes ");
            }
            return (sb.toString());
        }
    }

    private class viewHolder extends RecyclerView.ViewHolder {
        TextView country, date,name;
        ImageView icon;
        RelativeLayout relativeLayout;

        viewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            country = itemView.findViewById(R.id.country);
            date = itemView.findViewById(R.id.date);
            name = itemView.findViewById(R.id.name);
            relativeLayout = itemView.findViewById(R.id.rel);

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    profile_view dialog = new profile_view();

                    Bundle bundle = new Bundle();
                    bundle.putString("uid",receivers.get(getAdapterPosition()).getUid());
                    dialog.setArguments(bundle);
                    dialog.show(getActivity().getSupportFragmentManager(),"profile_view");

                }
            });
        }
    }
}
