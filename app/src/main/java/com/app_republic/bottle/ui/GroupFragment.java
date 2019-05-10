package com.app_republic.bottle.ui;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app_republic.bottle.R;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.Bottle_view;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class GroupFragment extends Fragment {

    FloatingActionButton send_bottle,cloud,adventure;

    DatabaseReference firebaseDatabase;
    List<Bottle_view> list;
    String uid;
    TextView empty_text;
    RelativeLayout empty_layout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_group, container, false);

        uid = SharedPreferenceHelper.getInstance(getActivity()).getUID();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        cloud = layout.findViewById(R.id.cloud);
        adventure = layout.findViewById(R.id.send_owl);

        empty_text = layout.findViewById(R.id.empty_text);
        empty_layout = layout.findViewById(R.id.empty_layout);

        send_bottle = layout.findViewById(R.id.send_bottle);
        send_bottle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_bottle.setEnabled(false);
                FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("bottles").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        send_bottle.setEnabled(true);
                        long bottles = Long.parseLong(dataSnapshot.getValue().toString());
                        if (bottles > 0) {
                            Intent intent = new Intent(getActivity(), newBottle.class);
                            intent.putExtra("class","home");
                            intent.putExtra("uid","");

                            startActivity(intent);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(getString(R.string.need_bottle));
                            builder.setPositiveButton(getString(R.string.buy_bottle), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(getActivity(), ShipActivity.class));
                                }
                            });
                            builder.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        send_bottle.setEnabled(true);
                    }
                });

            }
        });

        cloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Cloud.class);
                startActivity(intent);
            }
        });
        adventure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("owls").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        send_bottle.setEnabled(true);
                        long owls = Long.parseLong(dataSnapshot.getValue().toString());
                        if (owls > 0) {
                            Intent intent = new Intent(getActivity(),adventure.class);
                            startActivity(intent);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(getString(R.string.need_owl));
                            builder.setPositiveButton(getString(R.string.buy_owl), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(getActivity(), ShipActivity.class));
                                }
                            });
                            builder.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        send_bottle.setEnabled(true);
                    }
                });


            }
        });

        RecyclerView recyclerView =  layout.findViewById(R.id.recycler_view);
         list = new ArrayList<>();
        final bottle_adapter adapter = new bottle_adapter(list,getActivity());
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/candy.otf");


        empty_text.setTypeface(type);

        /*firebaseDatabase.child("user").child(uid).child("received").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data :dataSnapshot.getChildren()){

                    list.add(new Bottle_view(data));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        firebaseDatabase.child("user").child(uid).child("received").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Object val = dataSnapshot.child("receivers").child(StaticConfig.UID).child("status").getValue();
                if (val == null || val.toString().equals("received")) {
                    list.add(new Bottle_view(dataSnapshot));
                    adapter.notifyDataSetChanged();
                }
                if (list.isEmpty())
                    empty_layout.setVisibility(View.VISIBLE);
                else
                    empty_layout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                Object val = dataSnapshot.child("receivers").child(StaticConfig.UID).child("status").getValue();
                if (val != null && !val.toString().equals("received")) {
                    list.remove(find(dataSnapshot.getKey()));
                    adapter.notifyDataSetChanged();
                }
                if (list.isEmpty())
                    empty_layout.setVisibility(View.VISIBLE);
                else
                    empty_layout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (find(dataSnapshot.getKey()) != -1) {
                    list.remove(find(dataSnapshot.getKey()));
                    adapter.notifyDataSetChanged();
                    if (list.isEmpty())
                        empty_layout.setVisibility(View.VISIBLE);
                    else
                        empty_layout.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });








      return layout;
    }

    private int find(String id) {
        int i = 0;
        for (Bottle_view bottle_view:list) {
            if (bottle_view.id.equals(id))
                return i;
            i++;
        }
        return -1;
    }
}


