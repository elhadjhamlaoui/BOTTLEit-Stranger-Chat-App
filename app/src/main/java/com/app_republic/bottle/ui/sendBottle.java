package com.app_republic.bottle.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Display;

import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.app_republic.bottle.R;
import com.app_republic.bottle.data.FriendDB;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.Bottle;
import com.app_republic.bottle.model.Friend;
import com.app_republic.bottle.model.Receiver;
import com.app_republic.bottle.model.body;
import com.app_republic.bottle.service.ServiceUtils;
import com.appyvet.materialrangebar.RangeBar;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.OnCountryPickerListener;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by elhadj on 01/09/2018.
 */

public class sendBottle extends AppCompatActivity {


    RelativeLayout imageLayout, videoLayout, animationLayout;
    ProgressBar videoBar, imageBar;
    TextView textView1, textView2, country_text, gender_text;
    LottieAnimationView animationView;
    RelativeLayout country_selector, gender_selector;
    CardView filterLayout;
    RangeBar rangeBar;
    TextView age1_text, age2_text;
    FloatingActionButton filter;
    ImageView del_country, del_gender;
    Map<String, String> map;
    AnimatorSet animationSet1;
    RelativeLayout tip_layout;
    Button BT_bottles;
    boolean compass_check = false;
    TextView TV_receiver_text;

    boolean bool = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiCompat.init(new BundledEmojiCompatConfig(this));

        setContentView(R.layout.send_bottle);

        //Bottle bottle = new Bottle(getIntent().getStringExtra("text"));
        animationLayout = findViewById(R.id.animationLayout);
        BT_bottles = findViewById(R.id.my_bottles);
        imageLayout = findViewById(R.id.uploadImgae);
        videoLayout = findViewById(R.id.uploadVideo);
        videoBar = findViewById(R.id.videoProgress);
        imageBar = findViewById(R.id.imageProgress);
        textView1 = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        animationView = findViewById(R.id.animation_view);
        rangeBar = findViewById(R.id.rangeBar);
        age1_text = findViewById(R.id.age1_text);
        age2_text = findViewById(R.id.age2_text);
        tip_layout = findViewById(R.id.tip_layout);
        TV_receiver_text = findViewById(R.id.receiver_text);
        del_country = findViewById(R.id.del_country);
        del_gender = findViewById(R.id.del_gender);

        filterLayout = findViewById(R.id.filter_layout);
        filter = findViewById(R.id.filter);
        country_selector = findViewById(R.id.country_selector);
        gender_selector = findViewById(R.id.gender_selector);

        country_text = findViewById(R.id.country);
        gender_text = findViewById(R.id.gender);

        videoBar.setIndeterminate(true);
        imageBar.setIndeterminate(true);

        imageBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        videoBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);

        Typeface type = Typeface.createFromAsset(this.getAssets(), "fonts/candy.otf");
        textView1.setTypeface(type);
        textView2.setTypeface(type);


        map = new HashMap<>();

        if (getIntent().getStringExtra("class").equals("profile")) {
            map.put("uid", getIntent().getStringExtra("uid"));
            filter.hide();
        }

        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                map.put("age", leftPinValue + "-" + rightPinValue);
                age1_text.setText(leftPinValue + " - ");
                age2_text.setText(rightPinValue);
            }
        });
        final OvershootInterpolator interpolator = new OvershootInterpolator();

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!compass_check)
                    FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID)
                            .child("compass").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                long compass = Long.parseLong(dataSnapshot.getValue().toString());
                                if (compass > 0) {
                                    compass_check = true;

                                    if (filterLayout.getVisibility() == View.GONE) {
                                        ViewCompat.animate(filter).
                                                rotation(360f).
                                                withLayer().
                                                setDuration(500).
                                                setInterpolator(interpolator).
                                                start();
                                        filterLayout.setVisibility(View.VISIBLE);

                                    } else {
                                        ViewCompat.animate(filter).
                                                rotation(0f).
                                                withLayer().
                                                setDuration(500).
                                                setInterpolator(interpolator).
                                                start();
                                        filterLayout.setVisibility(View.GONE);

                                    }
                                } else {
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(sendBottle.this);
                                    builder.setMessage(getString(R.string.need_compass));
                                    builder.setPositiveButton(getString(R.string.buy_compass), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(sendBottle.this, ShipActivity.class));
                                        }
                                    });
                                    builder.show();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                else {
                    if (filterLayout.getVisibility() == View.GONE) {
                        ViewCompat.animate(filter).
                                rotation(360f).
                                withLayer().
                                setDuration(500).
                                setInterpolator(interpolator).
                                start();
                        filterLayout.setVisibility(View.VISIBLE);

                    } else {
                        ViewCompat.animate(filter).
                                rotation(0f).
                                withLayer().
                                setDuration(500).
                                setInterpolator(interpolator).
                                start();
                        filterLayout.setVisibility(View.GONE);

                    }
                }


            }
        });
        BT_bottles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(sendBottle.this, mybottles.class));
                setResult(RESULT_OK, new Intent());
                finish();
            }
        });


        CountryPicker.Builder builder =
                new CountryPicker.Builder().with(this).canSearch(true).listener(new OnCountryPickerListener() {
                    @Override
                    public void onSelectCountry(Country country) {
                        map.put("country", country.getCode());

                        country_text.setText(country.getName());
                        del_country.setVisibility(View.VISIBLE);
                    }
                }).theme(CountryPicker.THEME_NEW);
        final CountryPicker picker = builder.build();
        picker.getCountryByISO("CD").setName("Congo");
        picker.getCountryByISO("FM").setName("Micronesia");
        picker.getCountryByISO("VE").setName("Venezuela");
        picker.getCountryByISO("BO").setName("Bolivia");
        picker.getCountryByISO("IR").setName("Iran");
        picker.getCountryByISO("MD").setName("Moldova");
        picker.getCountryByISO("MK").setName("Macedonia");
        picker.getCountryByISO("PS").setName("Palestine");
        picker.getCountryByISO("TZ").setName("Tanzania");
        picker.getCountryByISO("LY").setName("Libya");


        country_selector.setOnClickListener(v -> picker.showDialog(sendBottle.this));

        gender_selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(sendBottle.this);


                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(sendBottle.this, android.R.layout.simple_list_item_1);
                arrayAdapter.add(getString(R.string.male));
                arrayAdapter.add(getString(R.string.female));
                arrayAdapter.add(getString(R.string.nonbinary));


                builderSingle.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        map.put("gender", arrayAdapter.getItem(which));
                        String gender = arrayAdapter.getItem(which);
                        gender_text.setText(gender);
                        del_gender.setVisibility(View.VISIBLE);

                    }
                });
                builderSingle.show();
            }
        });

        del_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.remove("country");
                del_country.setVisibility(View.INVISIBLE);
                country_text.setText(getString(R.string.country));

            }
        });
        del_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.remove("gender");
                del_gender.setVisibility(View.INVISIBLE);
                gender_text.setText(getString(R.string.gender));

            }
        });


        final ImageView view = findViewById(R.id.view);


        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final int Width = display.getWidth();
        int Height = display.getHeight();

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0.0f);
        alpha.setDuration(4000);

        final ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.3f);
        scaleDownX.setDuration(2000);
        final ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.3f);
        scaleDownY.setDuration(2000);

        final ObjectAnimator translatey = ObjectAnimator.ofFloat(view, "translationY", -Height);
        translatey.setDuration(1000);

        final ObjectAnimator translatey2 = ObjectAnimator.ofFloat(view, "translationY", -Height / 2);
        translatey2.setDuration(4000);

        final AnimatorSet animationSet = new AnimatorSet();

        final ObjectAnimator rotation2 = ObjectAnimator.ofFloat(view, "rotation", 0f, 10f);
        rotation2.setDuration(2000);
        final ObjectAnimator rotation3 = ObjectAnimator.ofFloat(view, "rotation", 10f, 0f);
        rotation3.setDuration(2000);

        animationSet.playSequentially(rotation2, rotation3);
        animationSet.start();
        rotation3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animationSet.cancel();
                animationSet.start();
            }
        });

        final ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        rotation.setDuration(1000);
        rotation.setRepeatCount(1);
        rotation.setInterpolator(new LinearInterpolator());

        final AnimatorSet animationSet0 = new AnimatorSet();

        animationSet0.playTogether(translatey, rotation);


        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getY() < 0 && bool) {
                    bool = false;
                    animationSet.cancel();
                    view.setVisibility(View.VISIBLE);
                    view.clearAnimation();
                    animationSet0.start();
                    tip_layout.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });


        translatey.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animationSet0.cancel();
                view.clearAnimation();

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
                params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                view.setLayoutParams(params);


                final ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 0f, 359f);
                rotation.setDuration(4000);
                rotation.setRepeatCount(ValueAnimator.INFINITE);
                rotation.setInterpolator(new LinearInterpolator());

                animationSet1 = new AnimatorSet();
                animationSet1.playTogether(translatey2, alpha, rotation, scaleDownX, scaleDownY);
                animationSet1.start();

                //animationSet.playTogether(translatex,translatey2,rotation);
                // animationSet.start();

            }
        });
        translatey2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animationSet1.cancel();
                view.clearAnimation();

                body message = new body(getIntent().getStringExtra("text"));
                message.image = getIntent().getStringExtra("image");
                message.video = getIntent().getStringExtra("video");
                message.font = getIntent().getStringExtra("font");
                message.paper = getIntent().getStringExtra("paper");

                Bottle bottle = new Bottle(null, null, message, map);

                bottle.gender = getIntent().getStringExtra("gender");
                bottle.country = getIntent().getStringExtra("country");
                bottle.name = getIntent().getStringExtra("name");
                bottle.avatar = getIntent().getStringExtra("avatar");
                bottle.time = getIntent().getLongExtra("time", 0);

                final receivedBottle dialog = new receivedBottle();
                final Bundle argumants = new Bundle();
                argumants.putString("text", bottle.message.text);
                argumants.putString("sender", bottle.sender);
                argumants.putString("name", "name");
                argumants.putString("font", bottle.message.font);
                argumants.putString("paper", bottle.message.paper);

              /* FirebaseStorage.getInstance().getReference("images/-LLy_QlKH6J4F8vrqqjU").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {
                        FirebaseStorage.getInstance().getReference("videos/video.3gpp").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri2) {
                                argumants.putString("image",uri.toString());
                                argumants.putString("video",uri2.toString());
                                argumants.putString("time", Long.toString(new Date().getTime()));

                                dialog.setArguments(argumants);
                                AppCompatActivity activity = sendBottle.this;
                                if(!activity.isDestroyed() && !activity.isFinishing())
                                    activity.getSupportFragmentManager().beginTransaction().add(dialog, "received").commitAllowingStateLoss();

                            }
                        });
                    }
                });
                   */

                newBottle(sendBottle.this, bottle);

            }
        });


    }

    public void newBottle(final Context context, final Bottle bottle) {

        filter.hide();
        final String uid = SharedPreferenceHelper.getInstance(context).getUID();
        bottle.sender = uid;
        final String key = FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("bottle").push().getKey();

        final StorageReference storage = FirebaseStorage.getInstance().getReference().child("images/" + key);
        final StorageReference storage2 = FirebaseStorage.getInstance().getReference().child("videos/" + key);

        final UploadTask uploadTask;
        if (bottle.message.image != null) {
            imageLayout.setVisibility(View.VISIBLE);
            uploadTask = storage.putFile(Uri.parse(bottle.message.image));

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            bottle.message.image = uri.toString();
                            if (bottle.message.video != null) {
                                videoLayout.setVisibility(View.VISIBLE);
                                UploadTask uploadTask2 = storage2.putFile(Uri.parse(bottle.message.video));

                                uploadTask2.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot2) {

                                        storage2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                bottle.message.video = uri.toString();

                                                FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("bottle").child(key).setValue(bottle).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            findTarget(key);


                                                        } else {

                                                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                });
                                            }
                                        });


                                    }
                                });

                            } else {
                                FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("bottle").child(key).setValue(bottle).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            findTarget(key);


                                        } else {

                                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                            }
                        }
                    });

                }
            });
        } else if (bottle.message.video != null) {
            UploadTask uploadTask2 = storage2.putFile(Uri.parse(bottle.message.video));
            uploadTask2.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storage2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            bottle.message.video = uri.toString();
                            FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("bottle").child(key).setValue(bottle).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        findTarget(key);


                                    } else {
                                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                                    }
                                }
                            });
                        }
                    });


                }
            });
        } else {
            FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("bottle").child(key).setValue(bottle).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        findTarget(key);


                    } else {

                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }


    }

    public void start_animation() {
        animationLayout.setVisibility(View.VISIBLE);
        animationView.playAnimation();
        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (getIntent().getStringExtra("class").equals("profile")) {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }

            }
        });
    }

    private void findTarget(String id) {
        final String uid = StaticConfig.UID;
        Map<String, String> data = new HashMap<>();
        data.put("uid", uid);
        data.put("id", id);
        final ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Sending the Bottle..");
        progressDialog.setCancelable(false);
        progressDialog.setTitle(null);
        progressDialog.show();

        FirebaseFunctions
                .getInstance()
                .getHttpsCallable("bottle")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        if (task.isSuccessful()) {
                            start_animation();
                            progressDialog.dismiss();

                            JSONObject object = new JSONObject(task.getResult().getData().toString());
                            Gson gson = new Gson();
                            if (!object.isNull("receiver")) {
                                Receiver receiver = gson.fromJson(object
                                        .getJSONObject("receiver").toString(), Receiver.class);
                                String country = ServiceUtils
                                        .getCountryName(sendBottle.this,
                                                receiver.getCountry());
                                TV_receiver_text.setText(receiver.getName()
                                        + " from " + country + " found your bottle!");

                            } else {
                                TV_receiver_text.setText("Unfortunately no one found your bottle, please try again later");
                            }


                        } else {
                            progressDialog.dismiss();
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(sendBottle.this);

                            alertDialog.setTitle("Error");
                            alertDialog.setMessage("An Error occurred, please try again later");
                            alertDialog.setCancelable(false);
                            alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setResult(RESULT_OK, new Intent());
                                    finish();
                                }
                            });
                            alertDialog.show();
                        }
                        return null;

                    }
                });


    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(sendBottle.this);

        alertDialog.setTitle(null);
        alertDialog.setMessage("You want to exit?");
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(RESULT_OK, new Intent());
                finish();
            }
        });
        alertDialog.show();
    }
}

