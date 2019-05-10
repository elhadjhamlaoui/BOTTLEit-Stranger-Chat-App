package com.app_republic.bottle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import com.app_republic.bottle.data.FriendDB;
import com.app_republic.bottle.data.GroupDB;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.service.ServiceUtils;
import com.app_republic.bottle.ui.Communicator;
import com.app_republic.bottle.ui.FriendsFragment;
import com.app_republic.bottle.ui.GroupFragment;
import com.app_republic.bottle.ui.SplashActivity;
import com.app_republic.bottle.ui.UserFragment;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Communicator {
    private static String TAG = "MainActivity";
    private ViewPager viewPager;
    private TabLayout tabLayout = null;
    public static String STR_FRIEND_FRAGMENT = "FRIEND";
    public static String STR_GROUP_FRAGMENT = "GROUP";
    public static String STR_INFO_FRAGMENT = "INFO";
    private ViewPagerAdapter adapter;
    private OnDataReceivedListener mDataListener;

    private HorizontalScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiCompat.init(new BundledEmojiCompatConfig(MainActivity.this));
        MobileAds.initialize(MainActivity.this, getString(R.string.admob_app_id));

        setContentView(R.layout.activity_main);
        scrollView = findViewById(R.id.scroll_view);


        viewPager = findViewById(R.id.viewpager);

        if (SharedPreferenceHelper.getInstance(this).getUID().isEmpty()) {
            FirebaseAuth.getInstance().signOut();
            FriendDB.getInstance(this).dropDB();
            GroupDB.getInstance(this).dropDB();
            ServiceUtils.stopServiceFriendChat(getApplicationContext(), true);
            LoginManager.getInstance().logOut();
            finish();
            startActivity(new Intent(this, SplashActivity.class));
        }
        initTab();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference myConnectionsRef = reference.child("user/"+StaticConfig.UID+"/status/isOnline");

        final DatabaseReference lastOnlineRef  = reference.child("user/"+StaticConfig.UID+"/status/timestamp");

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean bool = dataSnapshot.getValue(Boolean.class);
                if (bool) {

                    myConnectionsRef.onDisconnect().setValue(false);

                    // Add this device to my connections list
                    // this value could contain info about the device or a timestamp too
                    myConnectionsRef.setValue(true);

                    // When I disconnect, update the last time I was seen online
                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        ServiceUtils.stopServiceFriendChat(getApplicationContext(), false);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        ServiceUtils.startServiceFriendChat(getApplicationContext());
        super.onDestroy();
    }

    /**
     * Khoi tao 3 tab
     */
    private void initTab() {
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorIndivateTab));
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }


    private void setupTabIcons() {
        int[] tabIcons = {
                R.drawable.friends,
                R.drawable.island,
                R.drawable.home
        };

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(final ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FriendsFragment(), STR_FRIEND_FRAGMENT);
        adapter.addFrag(new GroupFragment(), STR_GROUP_FRAGMENT);
        adapter.addFrag(new UserFragment(), STR_INFO_FRAGMENT);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int x = (viewPager.getWidth() * position + positionOffsetPixels);
                scrollView.scrollTo(x, 0);
            }

            @Override
            public void onPageSelected(int position) {
                ServiceUtils.stopServiceFriendChat(MainActivity.this.getApplicationContext(), false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_LOGIN && resultCode == RESULT_OK) {
//            if (data.getStringExtra(STR_EXTRA_ACTION).equals(LoginActivity.STR_EXTRA_ACTION_LOGIN)) {
//                authUtils.signIn(data.getStringExtra(STR_EXTRA_USERNAME), data.getStringExtra(STR_EXTRA_PASSWORD));
//            } else if (data.getStringExtra(STR_EXTRA_ACTION).equals(RegisterActivity.STR_EXTRA_ACTION_REGISTER)) {
//                authUtils.createUser(data.getStringExtra(STR_EXTRA_USERNAME), data.getStringExtra(STR_EXTRA_PASSWORD));
//            }else if(data.getStringExtra(STR_EXTRA_ACTION).equals(LoginActivity.STR_EXTRA_ACTION_RESET)){
//                authUtils.resetPassword(data.getStringExtra(STR_EXTRA_USERNAME));
//            }
//        } else if (resultCode == RESULT_CANCELED) {
//            this.finish();
//        }
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {
            Toast.makeText(this, "Rivchat version 1.0", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void keep(String uid, String id) {
        mDataListener.onDataReceived(uid, id);
    }

    /**
     * Adapter hien thi tab
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            // return null to display only the icon
            return null;
        }
    }


    public interface OnDataReceivedListener {
        void onDataReceived(final String idFriend, final String id);
    }

    public void setAboutDataListener(OnDataReceivedListener listener) {
        this.mDataListener = listener;
    }
}