package com.app_republic.bottle.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.app_republic.bottle.R;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.util.Static;
import com.app_republic.bottle.util.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by elhadj on 16/09/2018.
 */

public class Cloud extends AppCompatActivity {

    FloatingActionButton FB_add;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiCompat.init(new BundledEmojiCompatConfig(this));
        setContentView(R.layout.cloud_layout);

        ViewPager viewPager = findViewById(R.id.viewPager);

        FB_add = findViewById(R.id.add);

        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        FB_add.setOnClickListener(view -> {
            FB_add.setExpanded(false);
            FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID)
                    .child("owls").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    FB_add.setEnabled(true);
                    long owls = Long.parseLong(dataSnapshot.getValue().toString());
                    if (owls > 0) {
                        Intent intent = new Intent(Cloud.this, adventure.class);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Cloud.this);
                        builder.setMessage(getString(R.string.need_owl));
                        builder.setPositiveButton(getString(R.string.buy_owl),
                                (dialogInterface, i) -> startActivity(new Intent(Cloud.this,
                                        ShipActivity.class)));
                        builder.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    FB_add.setEnabled(true);
                }
            });
        });

        //Utils.loadNativeAd(this, adView);
        FrameLayout frameLayout = findViewById(R.id.adView);
        AdView mAdView = new AdView(this);
        mAdView.setAdUnitId(SharedPreferenceHelper.getInstance(this).getBannerAdId());
        mAdView.setAdSize(AdSize.BANNER);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        frameLayout.addView(mAdView);
    }


    public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        private Context mContext;

        public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new filterMine();
                case 1:
                    return new World();
                case 2:
                    return new filterFriends();
                case 3:
                    return new filterFollowing();
                case 4:
                    return new filterCountry();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.my_adventures);
                case 1:
                    return mContext.getString(R.string.world);
                case 2:
                    return mContext.getString(R.string.freinds);
                case 3:
                    return mContext.getString(R.string.following);
                case 4:
                    return mContext.getString(R.string.country);

                default:
                    return null;
            }
        }

    }


}
