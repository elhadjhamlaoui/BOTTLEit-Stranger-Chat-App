package com.app_republic.bottle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app_republic.bottle.R;
import com.app_republic.bottle.data.FriendDB;
import com.app_republic.bottle.data.GroupDB;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.User;
import com.app_republic.bottle.service.ServiceUtils;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class UserFragment extends Fragment {


    CircleImageView picture;
    FloatingActionButton ship, gold, bottles;
    TextView bottle_text, reputation_text, owl_text, feather_text, followers_text;
    DatabaseReference reference;
    SharedPreferenceHelper prefHelper;
    User myAccount;
    Button show_profile;
    RelativeLayout bottles_layout,reputation_layout,followers_layout,feathers_layout,owls_layout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        picture = view.findViewById(R.id.picture);

        ship = view.findViewById(R.id.ship);
        gold = view.findViewById(R.id.gold);
        bottles = view.findViewById(R.id.mybottles);
        show_profile = view.findViewById(R.id.show_profile);

        bottle_text = view.findViewById(R.id.bottle_text);
        reputation_text = view.findViewById(R.id.reputation_text);
        owl_text = view.findViewById(R.id.owl_text);
        feather_text = view.findViewById(R.id.feather_text);
        followers_text = view.findViewById(R.id.followers_text);

        bottles_layout = view.findViewById(R.id.bottles_layout);
        reputation_layout = view.findViewById(R.id.reputation_layout);
        owls_layout = view.findViewById(R.id.owl_layout);
        feathers_layout = view.findViewById(R.id.feather_layout);
        followers_layout = view.findViewById(R.id.followers_layout);

        String uid = SharedPreferenceHelper.getInstance(getContext()).getUID();
        reference = FirebaseDatabase.getInstance().getReference().child("user").child(uid);

        prefHelper = SharedPreferenceHelper.getInstance(getContext());
        myAccount = prefHelper.getUserInfo();
        setImageAvatar(myAccount.avatar);

        reference.child("reputation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                    reputation_text.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child("bottles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                    bottle_text.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child("owls").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                    owl_text.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child("feathers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                    feather_text.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                    followers_text.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("avatar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                    setImageAvatar(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), UserProfileActivity.class), StaticConfig.REQUEST_CODE_SETTINGS);
            }
        });
        ship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ShipActivity.class));
            }
        });
        gold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), GoldActivity.class));
            }
        });
        bottles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), mybottles.class));
            }
        });

        show_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile_view dialog = new profile_view();
                Bundle bundle = new Bundle();
                bundle.putString("uid", StaticConfig.UID);
                dialog.setArguments(bundle);
                dialog.show(getActivity().getSupportFragmentManager(), "profile_view");
            }
        });



        bottles_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToolTip(getString(R.string.bottles),getString(R.string.bottle_desc));
            }
        });
        feathers_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToolTip(getString(R.string.feathers),getString(R.string.feather_desc));

            }
        });
        owls_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToolTip(getString(R.string.owls),getString(R.string.owl_desc));

            }
        });
        followers_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToolTip(getString(R.string.followers),getString(R.string.followers_desc));

            }
        });
        reputation_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToolTip(getString(R.string.reputation),getString(R.string.reputation_desc));

            }
        });



        return view;

    }

    public void showToolTip(String title, String body) {
        ToolTipDialog dialog = new ToolTipDialog();
        Bundle argumants = new Bundle();

        argumants.putString("title",title);
        argumants.putString("body",body);

        dialog.setArguments(argumants);

        if(!getActivity().isDestroyed() && !getActivity().isFinishing())
            getActivity().getSupportFragmentManager().beginTransaction().add(dialog, "tool_tip").commitAllowingStateLoss();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StaticConfig.REQUEST_CODE_SETTINGS && resultCode == RESULT_OK) {
            FirebaseAuth.getInstance().signOut();
            FriendDB.getInstance(getActivity()).dropDB();
            GroupDB.getInstance(getActivity()).dropDB();
            ServiceUtils.stopServiceFriendChat(getActivity(), true);
            LoginManager.getInstance().logOut();
            getActivity().finish();
            startActivity(new Intent(getActivity(), SplashActivity.class));
        }
    }

    private void setImageAvatar(String avatar) {
        if (avatar.equals(StaticConfig.STR_DEFAULT)) {
            picture.setImageDrawable(AppCompatResources.getDrawable(getActivity(), R.drawable.default_avata));
        } else {
            Picasso.get().load(avatar).into(picture);
        }
    }
}
