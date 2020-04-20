package com.app_republic.bottle.ui;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.app_republic.bottle.R;
import com.app_republic.bottle.fragment.CommentsFragment;
import com.app_republic.bottle.model.Country;
import com.app_republic.bottle.service.ServiceUtils;
import com.app_republic.bottle.util.AppSingleton;
import com.app_republic.bottle.util.Static;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CountriesActivity extends AppCompatActivity {

    ArrayList<Country> countries = new ArrayList<>();

    Adapter adapter;
    RecyclerView depsRecyclerView;
    Gson gson;
    AppSingleton appSingleton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_chat);

        initialiseViews();

        appSingleton = AppSingleton.getInstance(this);

        adapter = new Adapter(this, countries);


        depsRecyclerView.setAdapter(adapter);


        depsRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        getCountries();

    }
    
    

    private void initialiseViews() {
        depsRecyclerView = findViewById(R.id.recyclerView);

    }


    private void getCountries() {
        appSingleton.getDb()
                .collection("countries")
                .orderBy("index")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    countries.clear();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        countries.add(documentSnapshot.toObject(Country.class));
                    }
                    adapter.notifyDataSetChanged();

                }).addOnFailureListener(e -> {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });


    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        Context context;
        ArrayList<Country> list;
        Picasso picasso;

        public Adapter(Context context, ArrayList<Country> list) {
            this.context = context;
            this.list = list;
            picasso = AppSingleton.getInstance(CountriesActivity.this).getPicasso();

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(CountriesActivity.this)
                    .inflate(R.layout.item_country2, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

            Country country = list.get(i);

            try {
                viewHolder.icon.setImageResource(ServiceUtils
                        .getCountryFlag(CountriesActivity.this, country.getId()));
            } catch (Resources.NotFoundException e) {
                viewHolder.icon.setImageResource(R.drawable.ic_chat_big_dark);
            }


            viewHolder.name.setText(country.getName());

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView name;

            ImageView icon;
            View V_root;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                icon = itemView.findViewById(R.id.icon);

                V_root = itemView.findViewById(R.id.root);

                V_root.setOnClickListener(view -> {

                    CommentsFragment fragment = CommentsFragment.newInstance();
                    Bundle args = new Bundle();

                    args.putString(Static.TARGET_TYPE,
                            Static.CHAT);
                    args.putString(Static.TARGET_ID,
                            list.get(getAdapterPosition()).getId());

                    args.putString(Static.ROOT_TYPE,
                            Static.CHAT);
                    args.putString(Static.ROOT_ID,
                            list.get(getAdapterPosition()).getId());


                    args.putParcelable(Static.COUNTRY,
                            list.get(getAdapterPosition()));

                    fragment.setArguments(args);
                    getSupportFragmentManager().beginTransaction()
                            .addToBackStack(Static.FRAGMENT_COMMENTS)
                            .add(R.id.container, fragment)
                            .commit();

                });
            }
        }
    }
}
