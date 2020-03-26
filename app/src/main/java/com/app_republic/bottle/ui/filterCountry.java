package com.app_republic.bottle.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app_republic.bottle.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mukesh.countrypicker.CountryPicker;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by elhadj on 16/09/2018.
 */

public class filterCountry extends Fragment {
    countryAdapter adapter;
    SwipeRefreshLayout refreshLayout;

    ArrayList<String> countries = new ArrayList<>();
    public filterCountry() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.filter_country, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);


         adapter = new countryAdapter(getActivity(),countries);


        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        getCountries();

        refreshLayout = rootView.findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getCountries();
            }
        });

        return rootView;
    }
    public void getCountries(){
        FirebaseDatabase.getInstance().getReference().child("countries").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                countries.clear();
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    String name = snapshot.getKey();
                    countries.add(name);

                }



                refreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    class countryAdapter extends RecyclerView.Adapter<viewHolder> {

        private Context context;
        ArrayList<String> countries;
        CountryPicker.Builder builder;
        CountryPicker picker;

        public countryAdapter(Context context, ArrayList<String> countries) {
            this.context = context;
            this.countries = countries;

            builder = new CountryPicker.Builder().with(context);

             picker = builder.build();

        }

        @Override
        public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.country_item, parent, false);
                return new viewHolder(view);

        }

        @Override
        public void onBindViewHolder(final viewHolder holder, int position) {
            try {
                final String name = countries.get(position);
                holder.country.setText(picker.getCountryByISO(name).getName());
                holder.avatar.setImageResource(picker.getCountryByISO(name).getFlag());

                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        adventures_dialog dialog = new adventures_dialog();
                        Bundle argumants = new Bundle();
                        argumants.putString("country",name);
                        dialog.setArguments(argumants);

                        if(!getActivity().isDestroyed() && !getActivity().isFinishing())
                            getActivity().getSupportFragmentManager().beginTransaction().add(dialog, "adventures").commitAllowingStateLoss();

                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }



        @Override
        public int getItemCount() {
            return countries.size();
        }
    }

    class viewHolder extends RecyclerView.ViewHolder {
        public CircleImageView avatar;
        public TextView country;
        public CardView cardView;



        public viewHolder(View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.avatar);
            country = itemView.findViewById(R.id.name);
            cardView = itemView.findViewById(R.id.card);

        }
    }



}