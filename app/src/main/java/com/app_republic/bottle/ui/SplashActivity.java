package com.app_republic.bottle.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.app_republic.bottle.MainActivity;
import com.app_republic.bottle.R;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SplashActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        final LottieAnimationView animationView = findViewById(R.id.video_animation);

        animationView.playAnimation();


        TextView title = findViewById(R.id.title);

        //Typeface type = Typeface.createFromAsset(this.getAssets(), "fonts/candy.otf");
        //title.setTypeface(type);

        mAuth = FirebaseAuth.getInstance();
        final Handler handler = new Handler();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null && !SharedPreferenceHelper.getInstance(SplashActivity.this).getUID().isEmpty()) {
                    StaticConfig.UID = user.getUid();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FirebaseDatabase.getInstance().getReference().child("settings").child("admob_reward_unit_id").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    animationView.cancelAnimation();
                                    SharedPreferenceHelper.getInstance(SplashActivity.this).saveAdmobId(dataSnapshot.getValue().toString());
                                    finish();
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }, 1000);

                } else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAuth.signOut();
                            LoginManager.getInstance().logOut();
                            finish();
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        }
                    }, 2000);

                }
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}