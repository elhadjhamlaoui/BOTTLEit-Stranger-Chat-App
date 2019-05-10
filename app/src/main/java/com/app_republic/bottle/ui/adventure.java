package com.app_republic.bottle.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.app_republic.bottle.R;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.Adventure;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

/**
 * Created by elhadj on 13/09/2018.
 */

public class adventure extends AppCompatActivity implements Communicator {
    ArrayList<Typeface> fontList = new ArrayList<>();
    final int ADVENTURE_MIN_BODY=30;
    final int ADVENTURE_MAX_BODY=5000;
    final int ADVENTURE_MIN_TITLE=10;
    final int ADVENTURE_MAX_TITLE=50;
    RelativeLayout imageLayout,videoLayout,animationLayout;
    LottieAnimationView animationView;

    EditText body,title;
    String[] fonts = {"normal","billy.ttf","black.ttf","harry.ttf","lemon.ttf","onepiece.ttf","starwars.ttf","band.otf","candy.otf","drift.ttf","freak.ttf","mario.ttf","montague.ttf","orange.ttf","snacker.ttf","texas.otf"};
    int[] papers = {R.drawable.paper1,R.drawable.paper2,R.drawable.paper3,R.drawable.paper4,R.drawable.paper5,R.drawable.paper6,R.drawable.paper7,R.drawable.paper8,R.drawable.paper9,R.drawable.paper10};
    String font="normal",pap="3";
    String imgUri=null,vdoUri=null;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    FloatingActionButton feather,paper,video,image,cancel;
    Button send;
    LinearLayout lin1,lin2;
    int paperResource=R.drawable.paper3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiCompat.init(new BundledEmojiCompatConfig(this));

        setContentView(R.layout.adventure);

        animationView = findViewById(R.id.animation_view);
        animationLayout = findViewById(R.id.animationLayout);
        imageLayout = findViewById(R.id.uploadImgae);
        videoLayout = findViewById(R.id.uploadVideo);

        feather = findViewById(R.id.feather);
        paper = findViewById(R.id.paper);
        lin1 = findViewById(R.id.lin1);
        lin2 = findViewById(R.id.lin2);
        video = findViewById(R.id.video);
        image = findViewById(R.id.image);
        cancel = findViewById(R.id.cancel);
        send = findViewById(R.id.send);

        body = findViewById(R.id.body);
        title = findViewById(R.id.title);


        ProgressBar videoBar = findViewById(R.id.videoProgress);
        ProgressBar imageBar = findViewById(R.id.imageProgress);;


        imageBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        videoBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);

        TextView textView1 = findViewById(R.id.textView);
        TextView textView2 = findViewById(R.id.textView2);
        TextView new_adventure = findViewById(R.id.new_adventure);

        Typeface type = Typeface.createFromAsset(this.getAssets(), "fonts/candy.otf");
        textView1.setTypeface(type);
        textView2.setTypeface(type);
        new_adventure.setTypeface(type);


        body.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction()&MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                return false;
            }
        });

        fontList.add(body.getTypeface());
        fontList.add(Typeface.createFromAsset(getAssets(),"fonts/billy.ttf"));
        fontList.add(Typeface.createFromAsset(getAssets(),"fonts/black.ttf"));
        fontList.add(Typeface.createFromAsset(getAssets(),"fonts/harry.ttf"));
        fontList.add(Typeface.createFromAsset(getAssets(),"fonts/lemon.ttf"));
        fontList.add(Typeface.createFromAsset(getAssets(),"fonts/onepiece.ttf"));
        fontList.add(Typeface.createFromAsset(getAssets(),"fonts/starwars.ttf"));
        fontList.add(Typeface.createFromAsset(getAssets(),"fonts/band.otf"));
        fontList.add(Typeface.createFromAsset(getAssets(),"fonts/candy.otf"));
        fontList.add(Typeface.createFromAsset(getAssets(),"fonts/drift.ttf"));
        fontList.add(Typeface.createFromAsset(getAssets(),"fonts/freak.ttf"));
        fontList.add(Typeface.createFromAsset(getAssets(),"fonts/mario.ttf"));
        fontList.add(Typeface.createFromAsset(getAssets(),"fonts/montague.ttf"));
        fontList.add(Typeface.createFromAsset(getAssets(),"fonts/orange.ttf"));
        fontList.add(Typeface.createFromAsset(getAssets(),"fonts/snacker.ttf"));
        fontList.add(Typeface.createFromAsset(getAssets(),"fonts/texas.otf"));






        for (int i=0;i<lin1.getChildCount();i++){
            final int finalI = i;
            lin1.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                            title.setTypeface(fontList.get(finalI));
                            body.setTypeface(fontList.get(finalI));
                            font = fonts[finalI];
                            feather.setImageResource(R.drawable.ic_feather);

                            lin1.setVisibility(View.GONE);



                }
            });
        }
        for (int i=0;i<lin2.getChildCount();i++){
            final int finalI = i;
            lin2.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paperResource=papers[finalI];
                    paper.setImageResource(paperResource);
                    pap=Integer.toString(finalI+1);

                    paper.setImageResource(paperResource);


                    lin2.setVisibility(View.GONE);

                }
            });
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){

                    Adventure adventure = new Adventure();

                    adventure.sender = SharedPreferenceHelper.getInstance(adventure.this).getUID();
                    adventure.text = body.getText().toString();
                    adventure.title = title.getText().toString();
                    adventure.font=font;
                    adventure.paper=pap;
                    adventure.image=imgUri;
                    adventure.video=vdoUri;

                    newAdventure(adventure);
                }
            }
        });





        feather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lin1.getVisibility()!=View.GONE){
                    feather.setImageResource(R.drawable.ic_feather);

                    lin1.setVisibility(View.GONE);




                }else{
                    FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID).child("feathers").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long feathers = Long.parseLong(dataSnapshot.getValue().toString());
                            if (feathers > 0) {

                                feather.setImageResource(R.drawable.delete_bl);
                                paper.setImageResource(paperResource);

                                lin1.setVisibility(View.VISIBLE);

                                lin2.setVisibility(View.GONE);
                            } else {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(adventure.this);
                                builder.setMessage(getString(R.string.need_feather));
                                builder.setPositiveButton(getString(R.string.buy_feather), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        startActivity(new Intent(adventure.this, ShipActivity.class));
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
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (vdoUri==null){
                    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){

                        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,15);

                        takeVideoIntent.putExtra(android.provider.MediaStore.EXTRA_VIDEO_QUALITY,0.5);

                        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                        }

                    }
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(adventure.this);
                    builder.setMessage(getString(R.string.removeFile));
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            vdoUri = null;
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
                if (imgUri==null){

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_CAPTURE);

                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(adventure.this);
                    builder.setMessage(getString(R.string.removeFile));
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            imgUri = null;
                            image.setImageResource(R.drawable.addimage);
                        }
                    });
                    builder.show();

                }
            }
        });
        paper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler handler1 = new Handler();

                if (lin2.getVisibility()!=View.GONE){
                    paper.setImageResource(paperResource);


                    lin2.setVisibility(View.GONE);


                }else{
                    FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID).child("feathers").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long feathers = Long.parseLong(dataSnapshot.getValue().toString());
                            if (feathers > 0) {

                                paper.setImageResource(R.drawable.delete_bl);
                                feather.setImageResource(R.drawable.ic_feather);

                                lin2.setVisibility(View.VISIBLE);
                                lin1.setVisibility(View.GONE);

                            } else {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(adventure.this);
                                builder.setMessage(getString(R.string.need_feather));
                                builder.setPositiveButton(getString(R.string.buy_feather), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        startActivity(new Intent(adventure.this, ShipActivity.class));
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
            if (videoUri!=null){
                vdoUri = videoUri.toString();

                video.setImageResource(R.drawable.youtube);

            }

        }else  if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri imageUri = intent.getData();
            if (imageUri!=null){
                image.setImageResource(R.drawable.photo);
                imgUri = imageUri.toString();


            }

        }
    }

    public boolean validate(){

        int l1=body.getText().toString().length();
        int l2=title.getText().toString().length();
        if (l1<ADVENTURE_MIN_BODY){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.body_atleast));
            builder.show();
            return false;

        }
        if (l2<ADVENTURE_MIN_TITLE){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.title_atleast));
            builder.show();
            return false;

        }




        return true;
    }

    public  void newAdventure(final Adventure adventure){

        final String uid = SharedPreferenceHelper.getInstance(this).getUID();
        adventure.sender = uid;
        adventure.name = SharedPreferenceHelper.getInstance(this).getUserInfo().name;
        adventure.avatar = SharedPreferenceHelper.getInstance(this).getUserInfo().avatar;
        adventure.country = SharedPreferenceHelper.getInstance(this).getUserInfo().country;
        final String key = FirebaseDatabase.getInstance().getReference().child("adventure").child(uid).push().getKey();
        adventure.id=key;

        final StorageReference storage = FirebaseStorage.getInstance().getReference().child("images/"+key);
        final StorageReference storage2 = FirebaseStorage.getInstance().getReference().child("videos/"+key);

        final UploadTask uploadTask;
        if (adventure.image!=null){
            imageLayout.setVisibility(View.VISIBLE);
            uploadTask = storage.putFile(Uri.parse(adventure.image));

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(adventure.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                    storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            adventure.image=uri.toString();
                            if (adventure.video!=null){
                                videoLayout.setVisibility(View.VISIBLE);
                                UploadTask uploadTask2 = storage2.putFile(Uri.parse(adventure.video));

                                uploadTask2.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(adventure.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot2) {

                                        storage2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                adventure.video=uri.toString();
                                                saveAdventure(key,adventure);
                                            }
                                        });


                                    }
                                });

                            }else{
                                saveAdventure(key,adventure);


                            }
                        }
                    });


                }
            });
        }


        else if (adventure.video!=null){
            videoLayout.setVisibility(View.VISIBLE);

            UploadTask uploadTask2 = storage2.putFile(Uri.parse(adventure.video));
            uploadTask2.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(adventure.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storage2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            adventure.video=uri.toString();
                            saveAdventure(key,adventure);
                        }
                    });

                }
            });
        } else {
            saveAdventure(key,adventure);

        }




    }

    public void saveAdventure(String key,final Adventure adventure) {
        adventure.time = System.currentTimeMillis();
        FirebaseDatabase.getInstance().getReference().child("adventure").child(key).setValue(adventure).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    start_animation(adventure);
                }else{
                    Toast.makeText(adventure.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void start_animation(final Adventure adventure) {
        animationLayout.setVisibility(View.VISIBLE);
        animationView.playAnimation();
        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                finish();
            }
        });
    }

    @Override
    public void keep(String uid, String id) {

    }
}
