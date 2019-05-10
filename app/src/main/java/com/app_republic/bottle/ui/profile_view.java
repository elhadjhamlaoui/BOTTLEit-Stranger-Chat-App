package com.app_republic.bottle.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.app_republic.bottle.R;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.User;
import com.app_republic.bottle.service.ServiceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by elhadj on 17/09/2018.
 */

public class profile_view extends DialogFragment {

    private ArrayList<String> interests = new ArrayList<>();
    private ArrayList<String> languages = new ArrayList<>();

    private CircleImageView country, avatar;
    private ImageView gender;
    private Button favorite_but;

    private TextView name, age, reputation, wall, following, followers;

    private RecyclerView interest_recycler, language_recycler;
    private Adapter interest_adapter, language_adapter;
    private User user;
    private RelativeLayout animation;
    private LottieAnimationView animationView;
    private FloatingActionButton bottle;

    private boolean favorite;

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
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.color.cardview_dark_background);

        View rootView = inflater.inflate(R.layout.profile_view, container, false);

        animation = rootView.findViewById(R.id.animation);
        animationView = rootView.findViewById(R.id.video_animation);

        language_recycler = rootView.findViewById(R.id.languages);
        interest_recycler = rootView.findViewById(R.id.interests);

        country = rootView.findViewById(R.id.country);
        avatar = rootView.findViewById(R.id.avatar);
        gender = rootView.findViewById(R.id.gender);
        favorite_but = rootView.findViewById(R.id.follow);
        bottle = rootView.findViewById(R.id.bottle);

        name = rootView.findViewById(R.id.name);
        age = rootView.findViewById(R.id.age);
        reputation = rootView.findViewById(R.id.reputation);
        wall = rootView.findViewById(R.id.wall);
        followers = rootView.findViewById(R.id.followers);
        following = rootView.findViewById(R.id.following);

        user = SharedPreferenceHelper.getInstance(getActivity()).getUserInfo();
        interest_adapter = new Adapter(getActivity(), interests);
        language_adapter = new Adapter(getActivity(), languages);

        language_recycler.setLayoutManager(new RtlGridLayoutManager(getActivity(), 3));
        language_recycler.setAdapter(language_adapter);

        interest_recycler.setLayoutManager(new RtlGridLayoutManager(getActivity(), 3));
        interest_recycler.setAdapter(interest_adapter);

        animationView.playAnimation();


        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/candy.otf");

        name.setTypeface(type);
        age.setTypeface(type);

        FirebaseDatabase.getInstance().getReference().child("user").child(getArguments().getString("uid")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {

                if (getActivity() != null) {
                    User user = dataSnapshot.getValue(User.class);
                    name.setText(user.name);
                    age.setText(ServiceUtils.getAge(user.birthdate)+" "+getString(R.string.years));
                    reputation.setText("Reputation: "+user.reputation);
                    followers.setText("Followers: "+user.followers);
                    following.setText("Following: "+user.followings);



                    country.setImageResource(ServiceUtils.getCountryFlag(getActivity(), user.country));

                    String string = user.gender;
                    if (string.equals(getString(R.string.male))) {
                        gender.setVisibility(View.VISIBLE);
                        gender.setImageResource(R.drawable.male);
                    }
                    else if (string.equals(getString(R.string.female))) {
                        gender.setVisibility(View.VISIBLE);
                        gender.setImageResource(R.drawable.female);
                    }
                    else if (string.equals(getString(R.string.nonbinary))) {
                        gender.setVisibility(View.VISIBLE);
                        gender.setImageResource(R.drawable.non_binary);
                    }


                    setImageAvatar(user.avatar);

                    String[] array;
                    array = user.interests.split("/");
                    for (String s : array)
                        interests.add(s);

                    array = user.languages.split("/");
                    for (String s : array)
                        languages.add(s);

                    interests.remove(0);
                    languages.remove(0);


                    interest_adapter.notifyDataSetChanged();
                    language_adapter.notifyDataSetChanged();

                    animationView.cancelAnimation();
                    animation.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        favorite_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favorite_but.setEnabled(false);

                if (favorite) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.unfollow_message));
                    builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            makeTransaction();
                        }
                    });
                    builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            favorite_but.setEnabled(true);
                            dialogInterface.dismiss();
                        }
                    });

                    builder.show();
                } else {
                    makeTransaction();
                }


            }
        });

        FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID)
                .child("following").child(getArguments().getString("uid"))
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    favorite_but.setTextColor(getResources().getColor(R.color.grey_500));
                    favorite_but.setBackgroundResource(R.drawable.bac_follow_grey);
                    favorite_but.setText("following");
                    favorite = true;
                }
                else {
                    favorite_but.setTextColor(getActivity().getResources().getColor(android.R.color.holo_blue_light));
                    favorite_but.setBackgroundResource(R.drawable.bac_follow);
                    favorite_but.setText("follow");
                    favorite = false;
                }
                favorite_but.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bottle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottle.setEnabled(false);
                FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID).child("bottles").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        bottle.setEnabled(true);
                        long bottles = Long.parseLong(dataSnapshot.getValue().toString());
                        if (bottles > 0) {
                            Intent intent = new Intent(getActivity(), newBottle.class);
                            intent.putExtra("class","profile");
                            intent.putExtra("uid",getArguments().getString("uid"));
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
                        bottle.setEnabled(true);
                    }
                });
            }
        });

        bottle.hide();
        if (!getArguments().getString("uid").equals(StaticConfig.UID))
        FirebaseDatabase.getInstance().getReference("user").child(StaticConfig.UID)
                .child("receivers").child(getArguments().getString("uid"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            bottle.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        return rootView;
    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private ArrayList<String> list;


        Adapter(Context context, ArrayList<String> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(context).inflate(R.layout.interest_item_profile, parent, false);
            return new viewHolder(view);


        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            ((viewHolder) holder).name.setText(list.get(position));

        }


        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private class viewHolder extends RecyclerView.ViewHolder {
        TextView name;

        viewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
        }
    }

    private void setImageAvatar(String url) {
        if (url.equals(StaticConfig.STR_DEFAULT)) {
            avatar.setImageDrawable(AppCompatResources.getDrawable(getActivity(), R.drawable.default_avata));
        } else {
            Picasso.get().load(url).into(avatar);
        }
    }
    private class RtlGridLayoutManager extends GridLayoutManager {

        public RtlGridLayoutManager(Context context, int spanCount) {
            super(context, spanCount);
        }

        @Override
        protected boolean isLayoutRTL() {
            return super.isLayoutRTL();
        }
    }

    void makeTransaction() {
        FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID)
                .child("followings").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Long value = mutableData.getValue(Long.class);

                try {
                    if (value > 0) {
                        if (favorite)
                            mutableData.setValue(value - 1);
                        else
                            mutableData.setValue(value + 1);
                    } else {
                        if (!favorite)
                            mutableData.setValue(value + 1);
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }




                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                if (favorite) {
                    FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID)
                            .child("following").child(getArguments().getString("uid")).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            favorite_but.setEnabled(true);
                        }
                    });
                } else  {
                    FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID)
                            .child("following").child(getArguments().getString("uid")).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            favorite_but.setEnabled(true);
                        }
                    });
                }

            }
        });
    }
}
