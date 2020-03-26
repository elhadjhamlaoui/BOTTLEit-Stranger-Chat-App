package com.app_republic.bottle.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app_republic.bottle.R;
import com.app_republic.bottle.model.Bottle_view;
import com.mukesh.countrypicker.CountryPicker;

import java.util.List;

/**
 * Created by elhadj on 04/09/2018.
 */

public class bottle_adapter extends RecyclerView.Adapter<bottle_adapter.MyViewHolder> {

    private List<Bottle_view> bottleList;
    private AppCompatActivity context;
    private CountryPicker picker;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView country;
        public RelativeLayout root;
        public ImageView bottle_icon;

        public MyViewHolder(View view) {
            super(view);
            name =  view.findViewById(R.id.name);
            country =  view.findViewById(R.id.country);
            root =  view.findViewById(R.id.root);
            bottle_icon =  view.findViewById(R.id.bottle_icon);
        }



    }


    public bottle_adapter(List<Bottle_view> bottleList, Context c) {
        this.bottleList = bottleList;
        this.context = (AppCompatActivity) c;

        CountryPicker.Builder builder = new CountryPicker.Builder().with(context);
        picker = builder.build();


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bottle_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            final Bottle_view bottle = bottleList.get(position);
            holder.name.setText(bottle.getName());

            holder.country.setImageResource(picker.getCountryByISO(bottle.getCountry()).getFlag());

            final AnimatorSet animationSet = new AnimatorSet();

            final ObjectAnimator rotation1 = ObjectAnimator.ofFloat(holder.bottle_icon, "rotation", 0f, 10f);
            rotation1.setDuration(2000);
            final ObjectAnimator rotation2 = ObjectAnimator.ofFloat(holder.bottle_icon, "rotation", 10f, 0f);
            rotation2.setDuration(2000);

            animationSet.playSequentially(rotation1, rotation2);
            animationSet.start();
            rotation2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animationSet.cancel();

                    animationSet.start();
                }
            });


            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    receivedBottle dialog = new receivedBottle();
                    Bundle argumants = new Bundle();

                    argumants.putParcelable("bottle",bottle);
                    dialog.setArguments(argumants);

                    if(!context.isDestroyed() && !context.isFinishing())
                        context.getSupportFragmentManager().beginTransaction().add(dialog, "received").commitAllowingStateLoss();

                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }




    }

    @Override
    public int getItemCount() {
        return bottleList.size();
    }
}