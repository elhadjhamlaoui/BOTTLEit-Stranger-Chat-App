package com.app_republic.bottle.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.text.emoji.widget.EmojiTextView;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app_republic.bottle.service.DateInterface;
import com.app_republic.bottle.service.info_interface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.app_republic.bottle.R;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.Configuration;
import com.app_republic.bottle.model.User;
import com.app_republic.bottle.service.ServiceUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfileActivity extends AppCompatActivity implements info_interface, DateInterface {
    TextView tvUserName;
    CircleImageView avatar;

    private List<Configuration> listConfig = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserInfoAdapter infoAdapter;
    private Button logout;
    private RelativeLayout wall_layout;
    private EmojiTextView wall;

    private static final String USERNAME_LABEL = "Username";
    private static final String EMAIL_LABEL = "Email";
    private static final String SIGNOUT_LABEL = "Sign out";
    private static final String RESETPASS_LABEL = "Change Password";
    private static final String INTERESTS = "Interests";
    private static final String WALL = "Wall";
    private static final String LANGUAGES = "Languages";
    private static final String COUNTRY = "Country";
    private static final String BIRTHDATE = "birthdate";
    private static final String GENDER = "Gender";
    private static final String SEPERATOR_LABEL = "Personal info";
    private static final int PICK_IMAGE = 1994;
    private LovelyProgressDialog waitingDialog;

    SharedPreferenceHelper prefHelper;

    private NestedScrollView scrollView;
    private DatabaseReference userDB;
    private FirebaseAuth mAuth;
    private User myAccount;
    private Context context;
    private TextView privacy;

    public UserProfileActivity() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        userDB = FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID);
        userDB.addListenerForSingleValueEvent(userListener);
        mAuth = FirebaseAuth.getInstance();
        context = this;
        prefHelper = SharedPreferenceHelper.getInstance(context);

        avatar = findViewById(R.id.img_avatar);
        avatar.setOnClickListener(onAvatarClick);
        tvUserName = findViewById(R.id.tv_username);
        scrollView = findViewById(R.id.scrollView);
        logout = findViewById(R.id.logout);
        wall_layout = findViewById(R.id.wall_layout);
        wall = findViewById(R.id.wall);
        privacy = findViewById(R.id.privacy);

        myAccount = prefHelper.getUserInfo();
        setupArrayListInfo(myAccount);
        setImageAvatar(myAccount.avatar);
        tvUserName.setText(myAccount.name);

        recyclerView = findViewById(R.id.info_recycler_view);
        infoAdapter = new UserInfoAdapter(listConfig);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(infoAdapter);
        recyclerView.setFocusable(false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        waitingDialog = new LovelyProgressDialog(context);



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle(null)
                        .setMessage("Are you sure want to logout ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent data = new Intent();
                                setResult(RESULT_OK, data);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

            }
        });

        wall_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wall_dialog dialog = new wall_dialog();
                dialog.show(getSupportFragmentManager(), "wall");
            }
        });

        privacy.setText(Html.fromHtml("<p><u>Privacy policy</u></p>"));


        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_url)));
                startActivity(browserIntent);
            }
        });



    }

    private ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //Lấy thông tin của user về và cập nhật lên giao diện
            listConfig.clear();
            User user = dataSnapshot.getValue(User.class);
            prefHelper.saveUserInfo(user);

            myAccount = prefHelper.getUserInfo();

            setupArrayListInfo(myAccount);
            if (infoAdapter != null) {
                infoAdapter.notifyDataSetChanged();
            }
            if (tvUserName != null) {
                tvUserName.setText(myAccount.name);
            }

            setImageAvatar(myAccount.avatar);

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //Có lỗi xảy ra, không lấy đc dữ liệu
        }
    };


    /**
     * Khi click vào avatar thì bắn intent mở trình xem ảnh mặc định để chọn ảnh
     */
    private View.OnClickListener onAvatarClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            new AlertDialog.Builder(context)
                    .setTitle("Avatar")
                    .setMessage("Are you sure want to change avatar profile?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_PICK);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(context, "Có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_LONG).show();
                return;
            }

            waitingDialog.setCancelable(false)
                    .setTitle("Avatar updating....")
                    .show();
            upload_image(data.getData().toString());
        }
    }

    private void upload_image(String url) {
        final StorageReference storage = FirebaseStorage.getInstance().getReference().child("avatar/" + StaticConfig.UID);

        UploadTask uploadTask = storage.putFile(Uri.parse(url));

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
                    public void onSuccess(final Uri uri) {
                        userDB.child("avatar").setValue(uri.toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            myAccount.avatar = uri.toString();
                                            waitingDialog.dismiss();
                                            prefHelper.saveUserInfo(myAccount);
                                            setImageAvatar(myAccount.avatar);

                                            new LovelyInfoDialog(context)
                                                    .setTitle("Success")
                                                    .setMessage("Update avatar successfully!")
                                                    .show();


                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        waitingDialog.dismiss();
                                        new LovelyInfoDialog(context)
                                                .setTitle("False")
                                                .setMessage("False to update avatar")
                                                .show();
                                    }
                                });

                    }
                });


            }
        });
    }

    /**
     * Xóa list cũ và cập nhật lại list data mới
     *
     * @param myAccount
     */
    public void setupArrayListInfo(User myAccount) {
        if (myAccount.wall.isEmpty())
            wall.setText(getString(R.string.empty_wall));
        else
        wall.setText(myAccount.wall);

        listConfig.clear();

        Configuration userNameConfig = new Configuration(USERNAME_LABEL, myAccount.name);
        listConfig.add(userNameConfig);

        Configuration emailConfig = new Configuration(EMAIL_LABEL, myAccount.email);
        listConfig.add(emailConfig);

        Configuration resetPass = new Configuration(RESETPASS_LABEL, "");
        listConfig.add(resetPass);

        Configuration seperator = new Configuration(SEPERATOR_LABEL, "");
        listConfig.add(seperator);

        Configuration gender = new Configuration(GENDER, myAccount.gender);
        listConfig.add(gender);

        Configuration birthdate = new Configuration(BIRTHDATE, myAccount.birthdate);
        listConfig.add(birthdate);

        Configuration country = new Configuration(COUNTRY, ServiceUtils.getCountryName(UserProfileActivity.this, myAccount.country));
        listConfig.add(country);

        Configuration interests = new Configuration(INTERESTS, "");
        listConfig.add(interests);

        Configuration languages = new Configuration(LANGUAGES, "");
        listConfig.add(languages);

    }

    private void setImageAvatar(String url) {
            if (url.equals(StaticConfig.STR_DEFAULT)) {
                avatar.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.default_avata));
            } else {
                Picasso.get().load(url).into(avatar);
            }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void update() {
        myAccount = prefHelper.getUserInfo();
        setupArrayListInfo(myAccount);
        infoAdapter.notifyDataSetChanged();
        wall.setText(myAccount.wall);
    }

    @Override
    public void onDateSet(int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String birthdate = dayOfMonth+"/"+month+"/"+year;
        userDB.child("birthdate").setValue(birthdate);
        myAccount.birthdate = birthdate;
        prefHelper.saveUserInfo(myAccount);
        myAccount = prefHelper.getUserInfo();
        setupArrayListInfo(myAccount);
        infoAdapter.notifyDataSetChanged();

    }

    public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.ViewHolder> {
        private List<Configuration> profileConfig;
        private Context context = UserProfileActivity.this;

        public UserInfoAdapter(List<Configuration> profileConfig) {
            this.profileConfig = profileConfig;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_info_item_layout, parent, false);
            return new ViewHolder(itemView);
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Configuration config = profileConfig.get(position);
            holder.label.setText(config.getLabel());
            holder.value.setText(config.getValue());

            if (position == 3) {
                holder.arrow.setVisibility(View.INVISIBLE);
                holder.relativeLayout.setClickable(false);
                holder.relativeLayout.setBackgroundColor(getResources().getColor(R.color.grey_200));
            } else if (position == 7) {
                holder.value.setText(ServiceUtils.getCount(myAccount.interests) + "");
            } else if (position == 8) {
                holder.value.setText(ServiceUtils.getCount(myAccount.languages) + "");
            }

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (config.getLabel().equals(USERNAME_LABEL)) {
                        View vewInflater = LayoutInflater.from(context)
                                .inflate(R.layout.dialog_edit_username, (ViewGroup) getWindow().getDecorView().getRootView(), false);
                        final EditText input = vewInflater.findViewById(R.id.edit_username);
                        input.setText(myAccount.name);
                        /*Hiển thị dialog với dEitText cho phép người dùng nhập username mới*/
                        new AlertDialog.Builder(context)
                                .setTitle("Edit username")
                                .setView(vewInflater)
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String newName = input.getText().toString();
                                        if (!myAccount.name.equals(newName)) {
                                            changeUserName(newName);
                                        }
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    }

                    else if (config.getLabel().equals(RESETPASS_LABEL)) {
                        new AlertDialog.Builder(context)
                                .setTitle("Password")
                                .setMessage("Are you sure want to reset password?")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        resetPassword(myAccount.email);
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    }

                    else if (config.getLabel().equals(INTERESTS)) {
                        interests_dialog dialog = new interests_dialog();
                        dialog.show(getSupportFragmentManager(), "interests");
                    }

                    else if (config.getLabel().equals(LANGUAGES)) {
                        languages_dialog dialog = new languages_dialog();
                        dialog.show(getSupportFragmentManager(), "languages");
                    }

                    else if (config.getLabel().equals(COUNTRY)) {
                        country_dialog dialog = new country_dialog();
                        dialog.show(getSupportFragmentManager(), "country");
                    }

                    else if (config.getLabel().equals(GENDER)) {
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(UserProfileActivity.this);
                        builderSingle.setTitle(getString(R.string.select_gender));

                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UserProfileActivity.this, android.R.layout.simple_list_item_1);
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
                                String gender = arrayAdapter.getItem(which);
                                userDB.child("gender").setValue(gender);
                                myAccount.gender = gender;
                                prefHelper.saveUserInfo(myAccount);
                                myAccount = prefHelper.getUserInfo();
                                setupArrayListInfo(myAccount);
                                infoAdapter.notifyDataSetChanged();

                            }
                        });
                        builderSingle.show();

                    }

                    else if (config.getLabel().equals(BIRTHDATE)) {
                        DialogFragment datePicker = new DateDialog();
                        datePicker.show(getSupportFragmentManager(), "date picker");
                    }

                }
            });
        }


        private void changeUserName(String newName) {
            userDB.child("name").setValue(newName);
            myAccount.name = newName;
            tvUserName.setText(newName);
            prefHelper.saveUserInfo(myAccount);
            myAccount = prefHelper.getUserInfo();
            setupArrayListInfo(myAccount);
            infoAdapter.notifyDataSetChanged();
        }

        void resetPassword(final String email) {
            if (email != null && email.isEmpty())
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            new LovelyInfoDialog(context) {
                                @Override
                                public LovelyInfoDialog setConfirmButtonText(String text) {
                                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dismiss();
                                        }
                                    });
                                    return super.setConfirmButtonText(text);
                                }
                            }
                                    .setTitle("Password Recovery")
                                    .setMessage("Sent email to " + email)
                                    .setConfirmButtonText("Ok")
                                    .show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            new LovelyInfoDialog(context) {
                                @Override
                                public LovelyInfoDialog setConfirmButtonText(String text) {
                                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dismiss();
                                        }
                                    });
                                    return super.setConfirmButtonText(text);
                                }
                            }
                                    .setTitle("False")
                                    .setMessage("False to sent email to " + email)
                                    .setConfirmButtonText("Ok")
                                    .show();
                        }
                    });
        }


        @Override
        public int getItemCount() {
            return profileConfig.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView label, value;
            RelativeLayout relativeLayout;
            ImageView arrow;

            ViewHolder(View view) {
                super(view);
                label = view.findViewById(R.id.tv_title);
                value = view.findViewById(R.id.tv_detail);
                relativeLayout = view.findViewById(R.id.relativeLayout);
                arrow = view.findViewById(R.id.arrow);

            }
        }

    }

}
