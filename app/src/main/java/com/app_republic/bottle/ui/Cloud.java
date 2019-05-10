package com.app_republic.bottle.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.app_republic.bottle.R;

/**
 * Created by elhadj on 16/09/2018.
 */

public class Cloud extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiCompat.init(new BundledEmojiCompatConfig(this));
        setContentView(R.layout.cloud_layout);

        ViewPager viewPager = findViewById(R.id.viewPager);

        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
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
