package com.app_republic.bottle.ui;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.app_republic.bottle.R;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.util.AppSingleton;
import com.app_republic.bottle.util.Static;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class RandomChat extends AppCompatActivity {

    LottieAnimationView lottieAnimationView;
    TextView TV_looking;
    Button BT_start;
    ImageView IV_icon;
    AppSingleton appSingleton;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_chat);

        lottieAnimationView = findViewById(R.id.video_animation);
        TV_looking = findViewById(R.id.looking);
        BT_start = findViewById(R.id.start);
        IV_icon = findViewById(R.id.icon);


        appSingleton = AppSingleton.getInstance(this);

        databaseReference = appSingleton.getDatabaseReference();


        BT_start.setOnClickListener(v -> {
            BT_start.setEnabled(false);

            databaseReference
                    .child("random")
                    .orderByChild("available")
                    .equalTo(true)
                    .limitToFirst(1)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    data.child("available").getRef().setValue(false);

                                }
                            } else {
                                databaseReference
                                        .child("random")
                                        .child(StaticConfig.UID)
                                        .child("available")
                                        .setValue(true)
                                        .addOnSuccessListener(aVoid -> {
                                            new Handler().postDelayed(() -> {
                                                databaseReference
                                                        .child("random")
                                                        .child(StaticConfig.UID)
                                                        .child("available")
                                                        .setValue(false);
                                            }, 30000);
                                        });

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



            /*databaseReference
                    .child("random")
                    .child(StaticConfig.UID)
                    .child("available")
                    .setValue(true)
                    .addOnSuccessListener(aVoid -> {
                        lottieAnimationView.playAnimation();
                        TV_looking.setText("Looking for someone...");
                    });*/
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        databaseReference
                .child("random")
                .child(StaticConfig.UID)
                .child("available")
                .setValue(false)
                .addOnSuccessListener(aVoid -> {
                    lottieAnimationView.pauseAnimation();
                    TV_looking.setText("Chat with random people");
                });
    }
}
