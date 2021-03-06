package com.app_republic.bottle.ui;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app_republic.bottle.service.DateInterface;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.app_republic.bottle.MainActivity;
import com.app_republic.bottle.R;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.User;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity implements DateInterface, View.OnClickListener {
    private static final int RC_SIGN_IN = 11;
    FloatingActionButton fab;
    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private EditText editTextUsername, editTextPassword;
    private LovelyProgressDialog waitingDialog;
    private CallbackManager callbackManager;
    private AuthUtils authUtils;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private boolean firstTimeAccess;
    private static final String EMAIL = "email";
    LoginButton loginButton;
    TextView privacy;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        //printHashKey(this);
    }

    /*public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.wtf("hash", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
        } catch (Exception e) {
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());
        fab = findViewById(R.id.fab);
        editTextUsername = findViewById(R.id.et_username);
        editTextPassword = findViewById(R.id.et_password);
        privacy = findViewById(R.id.privacy);

        firstTimeAccess = true;

        callbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("116901023209-puuoc81lthl9jscbutang1pujofen345.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.sign_in_button).setOnClickListener(this);


        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                authUtils.signIn_facebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
        initFirebase();

        privacy.setText(Html.fromHtml("<p><u>Privacy policy</u></p>"));


        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_url)));
                startActivity(browserIntent);
            }
        });

        final RelativeLayout rootLayout = findViewById(R.id.root);
       /* fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rootLayout.isAttachedToWindow())
                clickRegisterLayout(v);
            }
        });*/

        fab.hide();
        new Handler().postDelayed(() -> {
            fab.show();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rootLayout.isAttachedToWindow())
                        clickRegisterLayout(v);
                }
            });
        }, 2000);


    }


    /**
     * Khởi tạo các thành phần cần thiết cho việc quản lý đăng nhập
     */
    private void initFirebase() {
        //Khoi tao thanh phan de dang nhap, dang ky
        mAuth = FirebaseAuth.getInstance();
        authUtils = new AuthUtils();
        mAuthListener = firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                StaticConfig.UID = user.getUid();
                if (firstTimeAccess) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                    LoginActivity.this.finish();
                }


            }
            firstTimeAccess = false;


        };

        waitingDialog = new LovelyProgressDialog(this).setCancelable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                startGoogleSignin();
                break;
        }
    }
    private void startGoogleSignin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public void clickRegisterLayout(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(null);
            getWindow().setExitTransition(null);
        }

        if (view != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(this, fab, fab.getTransitionName());
            startActivityForResult(new Intent(this, RegisterActivity.class), StaticConfig.REQUEST_CODE_REGISTER, options.toBundle());
        } else {
            startActivityForResult(new Intent(this, RegisterActivity.class), StaticConfig.REQUEST_CODE_REGISTER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StaticConfig.REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {
            authUtils.createUser(data.getStringExtra(StaticConfig.STR_EXTRA_USERNAME), data.getStringExtra(StaticConfig.STR_EXTRA_PASSWORD));
        } else if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                authUtils.signIn_google(task.getResult());
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else  {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void clickLogin(View view) {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        if (validate(username, password)) {
            authUtils.signIn(username, password);
        } else {
            Toast.makeText(this, "Invalid email or empty password", Toast.LENGTH_SHORT).show();
        }
    }

    public void clickSignup(View view) {
            clickRegisterLayout(null);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED, null);
        finish();
    }

    private boolean validate(String emailStr, String password) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return (password.length() > 0 || password.equals(";")) && matcher.find();
    }

    public void clickResetPassword(View view) {
        String username = editTextUsername.getText().toString();
        if (validate(username, ";")) {
            authUtils.resetPassword(username);
        } else {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDateSet(int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String birthdate = dayOfMonth + "/" + month + "/" + year;

        final User newUser = new User();
        newUser.birthdate = birthdate;
        newUser.email = user.getEmail() != null ? user.getEmail() : "";
        newUser.name = user.getEmail() != null ?
                user.getEmail()
                        .substring(0, user.getEmail().indexOf("@"))
                : "name";

        if (user.getPhotoUrl() != null && !user.getPhotoUrl().toString().isEmpty())
            newUser.avatar = user.getPhotoUrl().toString();

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle(getString(R.string.select_gender));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        arrayAdapter.add(getString(R.string.male));
        arrayAdapter.add(getString(R.string.female));
        arrayAdapter.add(getString(R.string.nonbinary));


        builderSingle.setCancelable(false);
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String gender = arrayAdapter.getItem(which);
                newUser.gender = gender;
                findCountry(newUser);

            }
        });
        builderSingle.show();
    }

    /**
     * Dinh nghia cac ham tien ich cho quas trinhf dang nhap, dang ky,...
     */
    class AuthUtils {
        /**
         * Action register
         *
         * @param email
         * @param password
         */
        void createUser(String email, String password) {
            waitingDialog
                    .setTitle("Registering....")
                    .show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            waitingDialog.dismiss();
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                new LovelyInfoDialog(LoginActivity.this) {
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
                                        .setTitle("Register false")
                                        .setMessage("Email exist or weak password!")
                                        .setConfirmButtonText("ok")
                                        .setCancelable(false)
                                        .show();
                            } else {
                                user = task.getResult().getUser();

                                initNewUserInfo();

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            waitingDialog.dismiss();
                        }
                    })
            ;
        }


        /**
         * Action Login
         *
         * @param email
         * @param password
         */
        void signIn(String email, String password) {
            waitingDialog
                    .setTitle("Login....")
                    .show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            waitingDialog.dismiss();
                            if (!task.isSuccessful()) {
                                new LovelyInfoDialog(LoginActivity.this) {
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
                                        .setTitle("Login false")
                                        .setMessage("Email not exist or wrong password!")
                                        .setCancelable(false)
                                        .setConfirmButtonText("Ok")
                                        .show();
                            } else {
                                user = task.getResult().getUser();
                                saveUserInfo();

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            waitingDialog.dismiss();
                        }
                    });
        }

        void signIn_facebook(AccessToken token) {
            waitingDialog
                    .setTitle("Login....")
                    .show();

            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            waitingDialog.dismiss();
                            if (!task.isSuccessful()) {
                                new LovelyInfoDialog(LoginActivity.this) {
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
                                        .setMessage("Login failed!")
                                        .setCancelable(false)
                                        .setConfirmButtonText("Ok")
                                        .show();
                                LoginManager.getInstance().logOut();

                            } else {
                                user = task.getResult().getUser();
                                if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                    initNewUserInfo();
                                } else {
                                    saveUserInfo();
                                }

                            }

                        }
                    });
        }

        void signIn_google(GoogleSignInAccount result) {
            waitingDialog
                    .setTitle("Login....")
                    .show();

            AuthCredential credential = GoogleAuthProvider.getCredential(result.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            waitingDialog.dismiss();
                            if (!task.isSuccessful()) {
                                new LovelyInfoDialog(LoginActivity.this) {
                                    @Override
                                    public LovelyInfoDialog setConfirmButtonText(String text) {
                                        findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(view -> dismiss());
                                        return super.setConfirmButtonText(text);
                                    }
                                }
                                        .setMessage("Login failed!")
                                        .setCancelable(false)
                                        .setConfirmButtonText("Ok")
                                        .show();
                                mGoogleSignInClient.signOut();
                            } else {
                                user = task.getResult().getUser();
                                if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                    initNewUserInfo();
                                } else {
                                    saveUserInfo();
                                }

                            }

                        }
                    });


        }


        /**
         * Action reset password
         *
         * @param email
         */
        void resetPassword(final String email) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            new LovelyInfoDialog(LoginActivity.this) {
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
                            new LovelyInfoDialog(LoginActivity.this) {
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

        /**
         * Luu thong tin user info cho nguoi dung dang nhap
         */
        void saveUserInfo() {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        String token = task.getResult().getToken();
                        FirebaseDatabase.getInstance().getReference().child("user/" + StaticConfig.UID).child("token").setValue(token).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FirebaseDatabase.getInstance().getReference().child("user/" + StaticConfig.UID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            waitingDialog.dismiss();
                                            User userInfo = dataSnapshot.getValue(User.class);
                                            SharedPreferenceHelper.getInstance(LoginActivity.this).saveUserInfo(userInfo);


                                            FirebaseDatabase.getInstance().getReference().child("settings").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    SharedPreferenceHelper.getInstance(LoginActivity.this)
                                                            .saveAdmobId(dataSnapshot.child("admob_reward_unit_id").getValue().toString());
                                                    SharedPreferenceHelper.getInstance(LoginActivity.this)
                                                            .saveAdmobNativeId(dataSnapshot.child("admob_native_unit_id").getValue().toString());

                                                    SharedPreferenceHelper.getInstance(LoginActivity.this)
                                                            .saveAdmobBannerId(dataSnapshot.child("admob_banner_unit_id").getValue().toString());

                                                    FirebaseMessaging.getInstance().subscribeToTopic("all")
                                                            .addOnCompleteListener(task -> {
                                                                LoginActivity.this.finish();
                                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));

                                                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                                            });

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        });


                    });


        }

        /**
         * Khoi tao thong tin mac dinh cho tai khoan moi
         */
        void initNewUserInfo() {
            DialogFragment datePicker = new DateDialog();
            datePicker.show(getSupportFragmentManager(), "date picker");
        }
    }

    public void findCountry(final User newUser) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(null);
        String countryCodeValue;
        if (getSystemService(Context.TELEPHONY_SERVICE) != null) {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            countryCodeValue = tm.getNetworkCountryIso();
        } else {
            countryCodeValue = getResources().getConfiguration().locale.getCountry();
        }

        newUser.country = countryCodeValue.isEmpty() ? "gb" : countryCodeValue;
        FirebaseDatabase.getInstance().getReference().child("user/" + user.getUid()).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Register and Login success", Toast.LENGTH_SHORT).show();
                    authUtils.saveUserInfo();
                }
            }
        });


        /*RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ip-api.com/json";
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(null);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            String code = object.getString("countryCode");
                            newUser.country = code;
                            FirebaseDatabase.getInstance().getReference().child("user/" + user.getUid()).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Register and Login success", Toast.LENGTH_SHORT).show();
                                        authUtils.saveUserInfo();
                                    }
                                }
                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                            alertDialogBuilder.setMessage(getString(R.string.error));
                            alertDialogBuilder.create().show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alertDialogBuilder.setMessage(getString(R.string.error));
                alertDialogBuilder.create().show();
            }
        });

        queue.add(stringRequest);
         */
    }
}
