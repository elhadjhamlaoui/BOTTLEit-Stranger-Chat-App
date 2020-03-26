package com.app_republic.bottle.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app_republic.bottle.R;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.Item;
import com.app_republic.bottle.model.User;
import com.app_republic.bottle.service.UpdateInterface;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShipActivity extends AppCompatActivity implements UpdateInterface {

    private ArrayList<Item> items = new ArrayList<>();
    private RecyclerView recyclerView;
    private Adapter adapter;
    private User user;
    private int gold;
    private TextView balance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ship_layout);

        recyclerView = findViewById(R.id.recyclerview);
        balance = findViewById(R.id.balance);

        adapter = new Adapter(this,items);

        user = SharedPreferenceHelper.getInstance(this).getUserInfo();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);

        items.add(new Item("bottles","1 Bottle",getString(R.string.bottle_desc),R.drawable.pack,5, 1));
        items.add(new Item("bottles","5 Bottles",getString(R.string.bottle_desc),R.drawable.pack,25, 5));
        items.add(new Item("owls","1 Owl",getString(R.string.owl_desc),R.drawable.owl2,5, 1));
        items.add(new Item("owls","5 Owls",getString(R.string.owl_desc),R.drawable.owl2,25, 5));
        items.add(new Item("feathers","1 Feather",getString(R.string.feather_desc),R.drawable.wings,5, 1));
        items.add(new Item("feathers","5 Feather",getString(R.string.feather_desc),R.drawable.wings,25, 5));
        items.add(new Item("compass","1 Compass",getString(R.string.compass_desc),R.drawable.ic_compass,15, 1));
        items.add(new Item("compass","3 Compass",getString(R.string.compass_desc),R.drawable.ic_compass,45, 3));

        adapter.notifyDataSetChanged();



    }

    @Override
    public void update() {
        getBalance();
    }


    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private ArrayList<Item> list;

        Adapter(Context context, ArrayList<Item> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(context).inflate(R.layout.ship_item, parent, false);
            return new viewHolder(view);


        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            Item item = list.get(position);
            ((viewHolder) holder).name.setText(item.getName());
            ((viewHolder) holder).price.setText(item.getPrice()+" coins");
            ((viewHolder) holder).icon.setImageResource(item.getIcon());

        }


        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private class viewHolder extends RecyclerView.ViewHolder {
        TextView name,price;
        ImageView icon;
        RelativeLayout relativeLayout;

        viewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            icon = itemView.findViewById(R.id.icon);
            relativeLayout = itemView.findViewById(R.id.rel);

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item_dialog dialog = new item_dialog();
                    Bundle args = new Bundle();
                    args.putString("key",items.get(getAdapterPosition()).getKey());
                    args.putString("name",items.get(getAdapterPosition()).getName());
                    args.putString("desc",items.get(getAdapterPosition()).getDesc());
                    args.putInt("price",items.get(getAdapterPosition()).getPrice());
                    args.putInt("icon",items.get(getAdapterPosition()).getIcon());
                    args.putInt("count",items.get(getAdapterPosition()).getCount());

                    dialog.setArguments(args);
                    dialog.show(getSupportFragmentManager(), "item");
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBalance();

    }

    private void getBalance() {
        FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID).child("gold").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    gold = Integer.parseInt(dataSnapshot.getValue().toString());
                    balance.setText(gold + " coins");
                } else {
                    gold = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
