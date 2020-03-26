package com.app_republic.bottle.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.app_republic.bottle.R;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.Bottle_view;
import com.app_republic.bottle.model.Receiver;
import com.app_republic.bottle.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mukesh.countrypicker.CountryPicker;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Created by elhadj on 17/09/2018.
 */

public class mybottles extends AppCompatActivity implements Communicator {

    private ArrayList<Bottle_view> bottles = new ArrayList<>();
    private RecyclerView recyclerView;
    private Adapter adapter;
    private User user;
    private CountryPicker picker;
    private TextView number;
    private RelativeLayout  animation;
    private LottieAnimationView animationView;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottles_layout);

        recyclerView = findViewById(R.id.recyclerview);
        number = findViewById(R.id.number);

        animation = findViewById(R.id.animation);
        animationView = findViewById(R.id.video_animation);

        CountryPicker.Builder builder = new CountryPicker.Builder().with(this);
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

        user = SharedPreferenceHelper.getInstance(this).getUserInfo();
        adapter = new Adapter(this, bottles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);

        animationView.playAnimation();
        FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID)
                .child("bottle").orderByChild("time")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 1)
                    number.setText(dataSnapshot.getChildrenCount() + " bottle floating");
                else
                    number.setText(dataSnapshot.getChildrenCount() + " bottles floating");

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Bottle_view bottle_view = new Bottle_view(data);
                    if (bottle_view.getLastReceiver() != null)
                    bottles.add(bottle_view);
                }
                Collections.reverse(bottles);
                adapter.notifyDataSetChanged();
                animationView.cancelAnimation();
                animation.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void keep(String uid, String id) {

    }


    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private ArrayList<Bottle_view> list;

        Adapter(Context context, ArrayList<Bottle_view> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(context).inflate(R.layout.bottles_item, parent, false);
            return new viewHolder(view);


        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            Bottle_view bottle = list.get(position);
            Receiver last = bottle.getLastReceiver();

            try {
                if (last != null) {
                    String country = last.getCountry();

                    ((viewHolder) holder).country.setText(picker.getCountryByISO(country).getName());
                    ((viewHolder) holder).date.setText(getDate(System.currentTimeMillis() - last.getTimeStamp()) + getString(R.string.ago));
                    ((viewHolder) holder).icon.setImageResource(picker.getCountryByISO(country).getFlag());
                } else {
                    ((viewHolder) holder).country.setText(getString(R.string.still_floating));
                    ((viewHolder) holder).date.setText(getString(R.string.sent) +" "+ getDate(System.currentTimeMillis() - bottle.time) + getString(R.string.ago));
                    ((viewHolder) holder).icon.setImageResource(R.drawable.bottle_home);
                }
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
        TextView country, date;
        ImageView icon;
        RelativeLayout relativeLayout;

        viewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            country = itemView.findViewById(R.id.country);
            date = itemView.findViewById(R.id.date);
            relativeLayout = itemView.findViewById(R.id.rel);

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    my_bottle dialog = new my_bottle();

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("bottle",bottles.get(getAdapterPosition()));
                    dialog.setArguments(bundle);
                    dialog.show(getSupportFragmentManager(),"mybottle");
                }
            });
        }
    }
}
