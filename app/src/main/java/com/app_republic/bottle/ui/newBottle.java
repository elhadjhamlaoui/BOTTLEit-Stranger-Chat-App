package com.app_republic.bottle.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.support.text.emoji.widget.EmojiEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app_republic.bottle.R;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

/**
 * Created by elhadj on 01/09/2018.
 */

public class newBottle extends AppCompatActivity {
    FloatingActionButton style, send, video, image;
    FloatingActionButton f0, f1, f2, f3, f4, f5, f6, f7, f8, f10, f11, f12, f13, f14, f15, f16;
    FloatingActionButton float1, float2, float3, float4, float5, float6, float7, float8, float9, float10;

    Typeface type0, type1, type2, type3, type4, type5, type6, type7, type8, type10, type11, type12, type13, type14, type15, type16;

    RelativeLayout paper;
    EmojiEditText editText;
    LinearLayout lin1, lin2;

    String font = "normal", pap = "1";
    Intent bottle_intent;
    String imgUri = null, vdoUri = null;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;

    TextView title;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiCompat.init(new BundledEmojiCompatConfig(this));

        setContentView(R.layout.bottle);

        bottle_intent = new Intent(newBottle.this, sendBottle.class);

        paper = findViewById(R.id.paper);


        style = findViewById(R.id.style);
        send = findViewById(R.id.send);
        editText = findViewById(R.id.editText);

        lin1 = findViewById(R.id.lin1);
        lin2 = findViewById(R.id.lin2);

        video = findViewById(R.id.video);
        image = findViewById(R.id.image);

        title = findViewById(R.id.title);

        f0 = findViewById(R.id.normal);

        f1 = findViewById(R.id.billy);
        f2 = findViewById(R.id.black);
        f3 = findViewById(R.id.harry);
        f4 = findViewById(R.id.lemon);
        f5 = findViewById(R.id.onepiece);
        f6 = findViewById(R.id.starwars);
        f7 = findViewById(R.id.band);
        f8 = findViewById(R.id.candy);
        f10 = findViewById(R.id.drift);
        f11 = findViewById(R.id.freak);
        f12 = findViewById(R.id.mario);
        f13 = findViewById(R.id.montague);
        f14 = findViewById(R.id.orange);
        f15 = findViewById(R.id.snacker);
        f16 = findViewById(R.id.texas);

        float1 = findViewById(R.id.float1);
        float2 = findViewById(R.id.float2);
        float3 = findViewById(R.id.float3);
        float4 = findViewById(R.id.float4);
        float5 = findViewById(R.id.float5);
        float6 = findViewById(R.id.float6);
        float7 = findViewById(R.id.float7);
        float8 = findViewById(R.id.float8);
        float9 = findViewById(R.id.float9);
        float10 = findViewById(R.id.float10);


        type0 = editText.getTypeface();
        type1 = Typeface.createFromAsset(getAssets(), "fonts/billy.ttf");
        type2 = Typeface.createFromAsset(getAssets(), "fonts/black.ttf");
        type3 = Typeface.createFromAsset(getAssets(), "fonts/harry.ttf");
        type4 = Typeface.createFromAsset(getAssets(), "fonts/lemon.ttf");
        type5 = Typeface.createFromAsset(getAssets(), "fonts/onepiece.ttf");
        type6 = Typeface.createFromAsset(getAssets(), "fonts/starwars.ttf");
        type7 = Typeface.createFromAsset(getAssets(), "fonts/band.otf");
        type8 = Typeface.createFromAsset(getAssets(), "fonts/candy.otf");
        type10 = Typeface.createFromAsset(getAssets(), "fonts/drift.ttf");
        type11 = Typeface.createFromAsset(getAssets(), "fonts/freak.ttf");
        type12 = Typeface.createFromAsset(getAssets(), "fonts/mario.ttf");
        type13 = Typeface.createFromAsset(getAssets(), "fonts/montague.ttf");
        type14 = Typeface.createFromAsset(getAssets(), "fonts/orange.ttf");
        type15 = Typeface.createFromAsset(getAssets(), "fonts/snacker.ttf");
        type16 = Typeface.createFromAsset(getAssets(), "fonts/texas.otf");


        title.setTypeface(type8);

        style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Handler handler1 = new Handler();

                if (lin1.getChildAt(0).getVisibility() != View.GONE) {
                    style.setImageResource(R.drawable.wings);


                    for (int i = 0; i < lin1.getChildCount(); i++) {
                        final int finalI = i;
                        handler1.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                lin1.getChildAt(finalI).setVisibility(View.GONE);
                            }
                        }, 10 * i);


                    }
                    for (int i = lin2.getChildCount() - 1; i >= 0; i--) {
                        final int finalI = i;
                        handler1.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                lin2.getChildAt(finalI).setVisibility(View.GONE);
                            }
                        }, 10 * i);


                    }
                } else {
                    FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID).child("feathers").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long feathers = Long.parseLong(dataSnapshot.getValue().toString());
                            if (feathers > 0) {

                                style.setImageResource(android.R.drawable.ic_popup_sync);


                                for (int i = 0; i < lin1.getChildCount(); i++) {
                                    final int finalI = i;
                                    handler1.postDelayed(new Runnable() {

                                        @Override
                                        public void run() {
                                            lin1.getChildAt(finalI).setVisibility(View.VISIBLE);
                                        }
                                    }, 10 * i);


                                }
                                for (int i = lin2.getChildCount() - 1; i >= 0; i--) {
                                    final int finalI = i;
                                    handler1.postDelayed(new Runnable() {

                                        @Override
                                        public void run() {
                                            lin2.getChildAt(finalI).setVisibility(View.VISIBLE);

                                        }
                                    }, 10 * i);

                                }
                            } else {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(newBottle.this);
                                builder.setMessage(getString(R.string.need_feather));
                                builder.setPositiveButton(getString(R.string.buy_feather), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        startActivity(new Intent(newBottle.this, ShipActivity.class));
                                    }
                                });
                                builder.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().length() > 20) {


                    User info = SharedPreferenceHelper.getInstance(newBottle.this).getUserInfo();

                    bottle_intent.putExtra("text", editText.getText().toString());
                    bottle_intent.putExtra("font", font);
                    bottle_intent.putExtra("paper", pap);
                    bottle_intent.putExtra("avatar", info.avatar);
                    bottle_intent.putExtra("name", info.name);
                    bottle_intent.putExtra("country", info.country);
                    bottle_intent.putExtra("gender", info.gender);
                    bottle_intent.putExtra("time", System.currentTimeMillis());
                    bottle_intent.putExtra("class", getIntent().getStringExtra("class"));
                    bottle_intent.putExtra("uid", getIntent().getStringExtra("uid"));

                    startActivityForResult(bottle_intent, StaticConfig.REQUEST_CODE_BOTTLE);

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(newBottle.this);
                    builder.setMessage(getResources().getString(R.string.tooshort));
                    builder.setPositiveButton("ok", null);
                    builder.show();
                }
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (vdoUri == null) {
                    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

                        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);

                        takeVideoIntent.putExtra(android.provider.MediaStore.EXTRA_VIDEO_QUALITY, 0.5);

                        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                        }

                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(newBottle.this);
                    builder.setMessage(getString(R.string.removeFile));
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            vdoUri = null;
                            bottle_intent.removeExtra("video");
                            video.setImageResource(R.drawable.addvideo);
                        }
                    });
                    builder.show();
                }

            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgUri == null) {

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_CAPTURE);

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(newBottle.this);
                    builder.setMessage(getString(R.string.removeFile));
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            imgUri = null;
                            bottle_intent.removeExtra("image");
                            image.setImageResource(R.drawable.addimage);
                        }
                    });
                    builder.show();

                }
            }
        });


        // fonts

        f0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editText.setTypeface(type0);
                font = "normal";
            }
        });
        f1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                font = "billy.ttf";

                editText.setTypeface(type1);
            }
        });
        f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setTypeface(type2);
                font = "black.ttf";

            }
        });
        f3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setTypeface(type3);
                font = "harry.ttf";

            }
        });
        f4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setTypeface(type4);
                font = "lemon.ttf";

            }
        });
        f5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setTypeface(type5);
                font = "onepiece.ttf";

            }
        });
        f6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setTypeface(type6);
                font = "starwars.ttf";

            }
        });

        f7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                font = "band.otf";

                editText.setTypeface(type7);
            }
        });
        f8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setTypeface(type8);
                font = "candy.otf";

            }
        });

        f10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setTypeface(type10);
                font = "drift.ttf";

            }
        });
        f11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setTypeface(type11);
                font = "freak.ttf";

            }
        });
        f12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setTypeface(type12);
                font = "mario.ttf";

            }
        });
        f13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setTypeface(type13);
                font = "montague.ttf";

            }
        });
        f14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setTypeface(type14);
                font = "orange.ttf";

            }
        });
        f15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setTypeface(type15);
                font = "snacker.ttf";

            }
        });
        f16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setTypeface(type16);
                font = "texas.otf";

            }
        });


        float1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                paper.setBackgroundResource(R.drawable.paper1);
                pap = "1";
            }
        });
        float2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paper.setBackgroundResource(R.drawable.paper2);
                pap = "2";

            }
        });
        float3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paper.setBackgroundResource(R.drawable.paper3);
                pap = "3";

            }
        });
        float4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paper.setBackgroundResource(R.drawable.paper4);
                pap = "4";

            }
        });
        float5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paper.setBackgroundResource(R.drawable.paper5);
                pap = "5";

            }
        });
        float6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paper.setBackgroundResource(R.drawable.paper6);
                pap = "6";

            }
        });

        float7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pap = "7";

                paper.setBackgroundResource(R.drawable.paper7);
            }
        });
        float8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paper.setBackgroundResource(R.drawable.paper8);
                pap = "8";

            }
        });

        float9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paper.setBackgroundResource(R.drawable.paper9);
                pap = "9";

            }
        });
        float10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paper.setBackgroundResource(R.drawable.paper10);
                pap = "10";

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
            if (videoUri != null) {
                vdoUri = videoUri.toString();
                bottle_intent.putExtra("video", vdoUri);

                video.setImageResource(R.drawable.youtube);

            }

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri imageUri = intent.getData();
            if (imageUri != null) {
                if (MaxSizeImage(getImagePath(imageUri))) {
                    image.setImageResource(R.drawable.photo);
                    imgUri = imageUri.toString();

                    bottle_intent.putExtra("image", imgUri);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(newBottle.this);
                    builder.setMessage(getString(R.string.max_size_image));
                    builder.setPositiveButton(getString(R.string.ok), null);
                    builder.show();
                }


            }

        } else if (requestCode == StaticConfig.REQUEST_CODE_BOTTLE && resultCode == RESULT_OK) {
            finish();
        }
    }

    public boolean MaxSizeImage(String imagePath) {
        boolean temp = false;
        File file = new File(imagePath);
        long length = file.length();

        if (length < 2000000) // 1.5 mb
            temp = true;

        return temp;
    }

    public String getImagePath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
}
