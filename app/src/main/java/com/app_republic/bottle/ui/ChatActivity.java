package com.app_republic.bottle.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.app_republic.bottle.R;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.Consersation;
import com.app_republic.bottle.model.Message;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import static com.app_republic.bottle.ui.ChatActivity.VIEW_TYPE_FRIEND_MESSAGE_image;
import static com.app_republic.bottle.ui.ChatActivity.VIEW_TYPE_FRIEND_MESSAGE_text;
import static com.app_republic.bottle.ui.ChatActivity.VIEW_TYPE_FRIEND_MESSAGE_video;
import static com.app_republic.bottle.ui.ChatActivity.VIEW_TYPE_USER_MESSAGE_image;
import static com.app_republic.bottle.ui.ChatActivity.VIEW_TYPE_USER_MESSAGE_text;
import static com.app_republic.bottle.ui.ChatActivity.VIEW_TYPE_USER_MESSAGE_video;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private final int REQUEST_IMAGE_CAPTURE = 0;
    private final int REQUEST_VIDEO_CAPTURE = 1;
    RelativeLayout imageLayout, videoLayout;
    private RecyclerView recyclerChat;
    public static final int VIEW_TYPE_USER_MESSAGE_text = 0;
    public static final int VIEW_TYPE_USER_MESSAGE_image = 1;
    public static final int VIEW_TYPE_USER_MESSAGE_video = 2;
    public static final int VIEW_TYPE_FRIEND_MESSAGE_text = 3;
    public static final int VIEW_TYPE_FRIEND_MESSAGE_image = 4;
    public static final int VIEW_TYPE_FRIEND_MESSAGE_video = 5;
    private ListMessageAdapter adapter;
    private String roomId;
    private String idFriend;
    private Consersation consersation;
    private ImageView btnSend, imageSend, videoSend;
    private EmojiconEditText editWriteMessage;
    private LinearLayoutManager linearLayoutManager;
    public static HashMap<String, String> bitmapAvataFriend;
    EmojIconActions emojIcon;
    LottieAnimationView animationView;
    TextView FriendName;
    ImageView back;
    CircleImageView avatar;


    private void setImageAvatar(String url) {
        if (url.equals(StaticConfig.STR_DEFAULT)) {
            avatar.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.default_avata));
        } else {
            Picasso.get().load(url).into(avatar);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intentData = getIntent();
        idFriend = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_ID);
        roomId = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID);
        String nameFriend = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_FRIEND);
        String imageFriend = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_AVATAR);

        consersation = new Consersation();

        editWriteMessage = findViewById(R.id.editWriteMessage);
        btnSend = findViewById(R.id.btnSend);
        imageSend = findViewById(R.id.add_image);
        videoSend = findViewById(R.id.add_video);


        avatar = findViewById(R.id.avatar);
        FriendName = findViewById(R.id.name);
        back = findViewById(R.id.back);

        ImageView em = findViewById(R.id.emoji);
        RelativeLayout root = findViewById(R.id.root);


        imageLayout = findViewById(R.id.uploadImgae);
        videoLayout = findViewById(R.id.uploadVideo);

        TextView textView1 = findViewById(R.id.textView);
        TextView textView2 = findViewById(R.id.textView2);

        Typeface type = Typeface.createFromAsset(this.getAssets(), "fonts/candy.otf");
        textView1.setTypeface(type);
        textView2.setTypeface(type);


        emojIcon = new EmojIconActions(this, root, editWriteMessage, em);
        emojIcon.setIconsIds(R.drawable.keyboard, R.drawable.smiling);
        emojIcon.setUseSystemEmoji(true);


        emojIcon.ShowEmojIcon();

        Toolbar toolbar = findViewById(R.id.my_toolbar);

        setSupportActionBar(toolbar);

        btnSend.setOnClickListener(this);
        imageSend.setOnClickListener(this);
        videoSend.setOnClickListener(this);

        String url = SharedPreferenceHelper.getInstance(this).getUserInfo().avatar;

        if (idFriend != null && nameFriend != null) {
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerChat = findViewById(R.id.recyclerChat);
            recyclerChat.setLayoutManager(linearLayoutManager);
            adapter = new ListMessageAdapter(this, consersation, bitmapAvataFriend, url);
            FirebaseDatabase.getInstance().getReference().child("message/" + roomId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getValue() != null) {
                        HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                        Message newMessage = new Message();
                        newMessage.idSender = (String) mapMessage.get("idSender");
                        newMessage.idReceiver = (String) mapMessage.get("idReceiver");
                        newMessage.text = (String) mapMessage.get("text");
                        newMessage.timestamp = (long) mapMessage.get("timestamp");
                        newMessage.video = (String) mapMessage.get("video");

                        newMessage.image = (String) mapMessage.get("image");
                        consersation.getListMessageData().add(newMessage);
                        adapter.notifyDataSetChanged();
                        linearLayoutManager.scrollToPosition(consersation.getListMessageData().size() - 1);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            recyclerChat.setAdapter(adapter);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FriendName.setText(nameFriend);
        setImageAvatar(imageFriend);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile_view dialog = new profile_view();

                Bundle bundle = new Bundle();
                bundle.putString("uid", idFriend);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "profile_view");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent result = new Intent();
            result.putExtra("idFriend", idFriend);
            setResult(RESULT_OK, result);
            this.finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        result.putExtra("idFriend", idFriend);
        setResult(RESULT_OK, result);
        this.finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSend) {
            String content = editWriteMessage.getText().toString().trim();
            if (content.length() > 0) {
                editWriteMessage.setText("");
                Message newMessage = new Message();
                newMessage.text = content;
                newMessage.idSender = StaticConfig.UID;
                newMessage.idReceiver = idFriend;
                newMessage.timestamp = System.currentTimeMillis();
                FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);
            }
        } else if (view.getId() == R.id.add_image) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);

            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_CAPTURE);


        } else if (view.getId() == R.id.add_video) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);

                takeVideoIntent.putExtra(android.provider.MediaStore.EXTRA_VIDEO_QUALITY, 0.5);

                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }

            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
            if (videoUri != null) {
                String vdoUri = videoUri.toString();

                String key = FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().getKey();

                upload_video(vdoUri, key);

            }

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri imageUri = intent.getData();
            if (imageUri != null) {
                if (MaxSizeImage(getImagePath(imageUri))) {
                    String imgUri = imageUri.toString();

                    String key = FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().getKey();


                    upload_image(imgUri, key);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setMessage(getString(R.string.max_size_image));
                    builder.setPositiveButton(getString(R.string.ok), null);
                    builder.show();
                }


            }

        }
    }

    public void upload_image(final String uri, final String key) {


        final StorageReference storage = FirebaseStorage.getInstance().getReference().child("images/" + key);

        final UploadTask uploadTask;

        imageLayout.setVisibility(View.VISIBLE);
        animationView = findViewById(R.id.image_animation);
        animationView.playAnimation();

        uploadTask = storage.putFile(Uri.parse(uri));

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                animationView.cancelAnimation();
                imageLayout.setVisibility(View.INVISIBLE);
                Message newMessage = new Message();
                newMessage.image = key;
                newMessage.text = "";
                newMessage.idSender = StaticConfig.UID;
                newMessage.idReceiver = idFriend;
                newMessage.timestamp = System.currentTimeMillis();
                FirebaseDatabase.getInstance().getReference().child("message/" + roomId).child(key).setValue(newMessage);

            }
        });


    }

    public void upload_video(final String uri, final String key) {


        final StorageReference storage = FirebaseStorage.getInstance().getReference().child("videos/" + key);

        final UploadTask uploadTask;

        videoLayout.setVisibility(View.VISIBLE);
        animationView = findViewById(R.id.video_animation);

        animationView.playAnimation();

        uploadTask = storage.putFile(Uri.parse(uri));

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {


                animationView.cancelAnimation();
                videoLayout.setVisibility(View.INVISIBLE);

                Message newMessage = new Message();
                newMessage.video = key;
                newMessage.text = "";
                newMessage.idSender = StaticConfig.UID;
                newMessage.idReceiver = idFriend;
                newMessage.timestamp = System.currentTimeMillis();
                FirebaseDatabase.getInstance().getReference().child("message/" + roomId).child(key).setValue(newMessage);


            }
        });


    }

    public boolean MaxSizeImage(String imagePath) {
        boolean temp = false;
        File file = new File(imagePath);
        long length = file.length();

        if (length < 2000000) // 2.0 mb
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

class ListMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Consersation consersation;
    private HashMap<String, String> bitmapAvata;
    private HashMap<String, DatabaseReference> bitmapAvataDB;
    private String avatar;
    int padding_in_px;

    public ListMessageAdapter(Context context, Consersation consersation, HashMap<String, String> bitmapAvata, String avatar) {
        this.context = context;
        this.consersation = consersation;
        this.bitmapAvata = bitmapAvata;
        this.avatar = avatar;
        bitmapAvataDB = new HashMap<>();

        int padding_in_dp = 10;
        final float scale = context.getResources().getDisplayMetrics().density;
        padding_in_px = (int) (padding_in_dp * scale + 0.5f);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_FRIEND_MESSAGE_text) {
            View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_friend, parent, false);
            return new ItemMessageFriendHolder(view);
        } else if (viewType == ChatActivity.VIEW_TYPE_FRIEND_MESSAGE_video || viewType == ChatActivity.VIEW_TYPE_FRIEND_MESSAGE_image) {
            View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_friend_media, parent, false);
            return new ItemMessageFriendHolder(view);
        } else if (viewType == ChatActivity.VIEW_TYPE_USER_MESSAGE_text) {
            View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_user, parent, false);
            return new ItemMessageUserHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_user_media, parent, false);
        return new ItemMessageUserHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemMessageFriendHolder) {
            String currentAvata = bitmapAvata.get(consersation.getListMessageData().get(position).idSender);

            final String image = consersation.getListMessageData().get(position).image;
            final String video = consersation.getListMessageData().get(position).video;

            if (getItemViewType(position) == VIEW_TYPE_FRIEND_MESSAGE_text) {
                ((ItemMessageFriendHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);
                if (consersation.getListMessageData().get(position).text.contains(context.getString(R.string.deleted_friend))){
                    ((ItemMessageFriendHolder) holder).txtContent.setBackgroundResource(R.drawable.rounded_corner2_red);


                    ((ItemMessageFriendHolder) holder).txtContent.setPadding(padding_in_px,padding_in_px,padding_in_px,padding_in_px);

                }

                else {

                    ((ItemMessageFriendHolder) holder).txtContent.setBackgroundResource(R.drawable.rounded_corner2);
                    ((ItemMessageFriendHolder) holder).txtContent.setPadding(padding_in_px,padding_in_px,padding_in_px,padding_in_px);

                }

            } else if (getItemViewType(position) == VIEW_TYPE_FRIEND_MESSAGE_image) {


                FirebaseStorage.getInstance().getReference("images/" + image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {

                        Picasso.get()
                                .load(uri.toString())
                                .placeholder(AppCompatResources.getDrawable(context, R.drawable.chatimage_large))
                                .into(((ItemMessageFriendHolder) holder).imageView);
                        ((ItemMessageFriendHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                play_image dialog = new play_image();
                                Bundle argumants = new Bundle();
                                argumants.putString("image", uri.toString());
                                dialog.setArguments(argumants);

                                AppCompatActivity activity = (AppCompatActivity) context;
                                if (!activity.isDestroyed() && !activity.isFinishing())
                                    activity.getSupportFragmentManager().beginTransaction().add(dialog, "image").commitAllowingStateLoss();


                            }
                        });
                    }
                });

            } else if (getItemViewType(position) == VIEW_TYPE_FRIEND_MESSAGE_video) {
                ((ItemMessageFriendHolder) holder).imageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.youtube_blue));
                ((ItemMessageFriendHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        FirebaseStorage.getInstance().getReference("videos/" + video).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {


                                play_video dialog = new play_video();
                                Bundle argumants = new Bundle();
                                argumants.putString("video", uri.toString());
                                dialog.setArguments(argumants);

                                AppCompatActivity activity = (AppCompatActivity) context;

                                if (!activity.isDestroyed() && !activity.isFinishing())
                                    activity.getSupportFragmentManager().beginTransaction().add(dialog, "video").commitAllowingStateLoss();


                            }
                        });

                    }
                });
            }

            if (currentAvata != null) {
                if (!currentAvata.equals(StaticConfig.STR_DEFAULT)) {
                    Picasso.get().load(currentAvata).into(((ItemMessageFriendHolder) holder).avatar);

                } else {
                    ((ItemMessageFriendHolder) holder).avatar.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.account_blue));

                }
            } else {
                final String id = consersation.getListMessageData().get(position).idSender;
                if (bitmapAvataDB.get(id) == null) {
                    bitmapAvataDB.put(id, FirebaseDatabase.getInstance().getReference().child("user/" + id + "/avatar"));
                    bitmapAvataDB.get(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                String avataStr = (String) dataSnapshot.getValue();
                                if (!avataStr.isEmpty()) {
                                    ChatActivity.bitmapAvataFriend.put(id, avataStr);
                                } else {
                                    ChatActivity.bitmapAvataFriend.put(id, null);
                                }
                                notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        } else if (holder instanceof ItemMessageUserHolder) {
            final String image = consersation.getListMessageData().get(position).image;
            final String video = consersation.getListMessageData().get(position).video;

            if (getItemViewType(position) == VIEW_TYPE_USER_MESSAGE_text) {
                ((ItemMessageUserHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);
            } else if (getItemViewType(position) == VIEW_TYPE_USER_MESSAGE_image) {


                FirebaseStorage.getInstance().getReference("images/" + image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {

                        Picasso.get()
                                .load(uri.toString())
                                .placeholder(AppCompatResources.getDrawable(context, R.drawable.chatimage_large))
                                .into(((ItemMessageUserHolder) holder).imageView);
                        ((ItemMessageUserHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                play_image dialog = new play_image();
                                Bundle argumants = new Bundle();
                                argumants.putString("image", uri.toString());
                                dialog.setArguments(argumants);

                                AppCompatActivity activity = (AppCompatActivity) context;
                                if (!activity.isDestroyed() && !activity.isFinishing())
                                    activity.getSupportFragmentManager().beginTransaction().add(dialog, "image").commitAllowingStateLoss();


                            }
                        });
                    }
                });

            } else if (getItemViewType(position) == VIEW_TYPE_USER_MESSAGE_video) {
                ((ItemMessageUserHolder) holder).imageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.chatimage_large));
                ((ItemMessageUserHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        FirebaseStorage.getInstance().getReference("videos/" + video).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {


                                play_video dialog = new play_video();
                                Bundle argumants = new Bundle();
                                argumants.putString("video", uri.toString());
                                dialog.setArguments(argumants);

                                AppCompatActivity activity = (AppCompatActivity) context;

                                if (!activity.isDestroyed() && !activity.isFinishing())
                                    activity.getSupportFragmentManager().beginTransaction().add(dialog, "video").commitAllowingStateLoss();


                            }
                        });

                    }
                });
            }
            if (avatar.equals(StaticConfig.STR_DEFAULT)) {
                ((ItemMessageUserHolder) holder).avatar.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.default_avata));
            } else {
                Picasso.get().load(avatar).into(((ItemMessageUserHolder) holder).avatar);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = consersation.getListMessageData().get(position);
        if (message.idSender.equals(StaticConfig.UID)) {
            if (message.video == null && message.image == null)
                return ChatActivity.VIEW_TYPE_USER_MESSAGE_text;
            else if ((message.image != null))
                return ChatActivity.VIEW_TYPE_USER_MESSAGE_image;
            else if ((message.video != null))
                return VIEW_TYPE_USER_MESSAGE_video;

        } else {
            if (message.video == null && message.image == null)
                return VIEW_TYPE_FRIEND_MESSAGE_text;
            else if ((message.image != null))
                return ChatActivity.VIEW_TYPE_FRIEND_MESSAGE_image;
            else if ((message.video != null))
                return ChatActivity.VIEW_TYPE_FRIEND_MESSAGE_video;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return consersation.getListMessageData().size();
    }
}

class ItemMessageUserHolder extends RecyclerView.ViewHolder {
    public EmojiconTextView txtContent;
    public CircleImageView avatar;
    public ImageView imageView;


    public ItemMessageUserHolder(View itemView) {
        super(itemView);
        txtContent = itemView.findViewById(R.id.textContentUser);
        avatar = itemView.findViewById(R.id.imageView2);
        imageView = itemView.findViewById(R.id.image);

    }
}

class ItemMessageFriendHolder extends RecyclerView.ViewHolder {
    public EmojiconTextView txtContent;
    public CircleImageView avatar;
    public ImageView imageView;

    public ItemMessageFriendHolder(View itemView) {
        super(itemView);
        txtContent = itemView.findViewById(R.id.textContentFriend);
        imageView = itemView.findViewById(R.id.image);

        avatar = itemView.findViewById(R.id.avatar);
    }
}
