package com.app_republic.bottle.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.text.emoji.widget.EmojiTextView;
import android.support.v4.app.DialogFragment;

/**
 * Created by elhadj on 07/09/2018.
 */

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app_republic.bottle.R;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.Bottle_view;
import com.app_republic.bottle.model.Language;
import com.app_republic.bottle.service.ServiceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Created by neo on 01/01/2017.
 */


public class receivedBottle extends DialogFragment {
    Dialog dialog;
    static final int name = 0;
    Button keep, release;
    Communicator communicator;
    ImageView video, image, report, picture;
    TextView nameView, date;
    EmojiTextView textView;
    ScrollView scrollView;
    FloatingActionButton close;
    RelativeLayout message;
    Bottle_view bottle;
    final String TAG = "tag";
    RequestQueue queue;
    String[] codes;
    String[] langs;
    HashMap<String,String> map = new HashMap();

    FloatingActionButton translate;
    private int currentLang = 0;
    String OriginalText;
    @Override
    public void onStart() {

        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;

    }

    @Override
    public void onAttach(Activity activity) {
        communicator = (Communicator) activity;


        super.onAttach(activity);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.received, null);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.cardview_light_background);

        queue = Volley.newRequestQueue(getActivity());
        scrollView = view.findViewById(R.id.scrollView);
        message = view.findViewById(R.id.message);
        translate = view.findViewById(R.id.translate);

        translate.hide();
        keep = view.findViewById(R.id.keep);
        release = view.findViewById(R.id.release);

        if (getArguments().getString("class") != null && getArguments().getString("class").equals("my_bottle")) {
            keep.setVisibility(View.GONE);
            release.setVisibility(View.GONE);
        }
        video = view.findViewById(R.id.video);
        image = view.findViewById(R.id.image);

        report = view.findViewById(R.id.report);
        picture = view.findViewById(R.id.picture);

        nameView = view.findViewById(R.id.name);
        date = view.findViewById(R.id.date);
        textView = view.findViewById(R.id.text);


        bottle = getArguments().getParcelable("bottle");
        loadPicture();


        getSupportedLanguages();

        final String sender = bottle.sender;

        final String id = bottle.id;
        final String videoText = bottle.message.video;
        final String imageText = bottle.message.image;

        final String name = bottle.name;
        OriginalText = bottle.message.text;
        Long time = bottle.time;

        if (!bottle.message.font.equals("normal")) {
            Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/" + bottle.message.font);


            textView.setTypeface(type);
        }


        setBackground(bottle.message.paper);


        final String uid = SharedPreferenceHelper.getInstance(getContext()).getUID();

        SimpleDateFormat formatter = new SimpleDateFormat("dd  MM,yyyy");
        String dateString = formatter.format(new Date());
        date.setText(dateString);
        nameView.setText(name);
        textView.setText(OriginalText);





        if (imageText != null) {
            image.setVisibility(View.VISIBLE);

            Picasso.get()

                    .load(imageText)
                    .resize(70, 70)

                    .into(image);
        }
        if (videoText != null)
            video.setVisibility(View.VISIBLE);


        close = view.findViewById(R.id.close);


        release.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("received").child(id).child("receivers").child(uid + "/status").setValue("released").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dismiss();
                        }
                    }
                });
            }
        });
        keep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("received").child(id).child("receivers").child(uid + "/status").setValue("keep").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        communicator.keep(sender, id);
                        dismiss();
                    }
                });
            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Comming soon.", Toast.LENGTH_SHORT).show();
            }
        });


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play_image dialog = new play_image();
                Bundle argumants = new Bundle();
                argumants.putString("image", imageText);
                dialog.setArguments(argumants);

                if (!getActivity().isDestroyed() && !getActivity().isFinishing())
                    getActivity().getSupportFragmentManager().beginTransaction().add(dialog, "image").commitAllowingStateLoss();
            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                play_video dialog = new play_video();
                Bundle argumants = new Bundle();
                argumants.putString("video", videoText);
                dialog.setArguments(argumants);

                if (!getActivity().isDestroyed() && !getActivity().isFinishing())
                    getActivity().getSupportFragmentManager().beginTransaction().add(dialog, "video").commitAllowingStateLoss();


            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile_view dialog = new profile_view();

                Bundle bundle = new Bundle();
                bundle.putString("uid", bottle.sender);
                dialog.setArguments(bundle);
                dialog.show(getActivity().getSupportFragmentManager(), "profile_view");
            }
        });


        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setTitle(getString(R.string.choose_language));
                mBuilder.setSingleChoiceItems(langs, currentLang, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i != 0) {
                            currentLang = i;
                            getTranslation(codes[i]);
                        } else {
                            textView.setText(OriginalText);
                        }

                        dialogInterface.dismiss();
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();




            }
        });

        return view;
    }

    void getSupportedLanguages() {

        String url = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?"
                + "key="
                + getString(R.string.yandex_key)
                + "&ui="
                + "en";

        final StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject object = jsonObject.getJSONObject("langs");
                    Iterator<String> keys = object.keys();

                    while (keys.hasNext())
                    {
                        String key = keys.next();
                        String value = object.getString(key);
                        map.put(key,value);
                    }

                    Language[] languages = ServiceUtils.convertMap(map);

                    Arrays.sort(languages);

                    languages[0] = new Language("or","Original language");

                    codes = new String[languages.length];
                    langs = new String[languages.length];

                    for (int i = 0; i < languages.length; i++) {
                        codes[i] = languages[i].code;
                        langs[i] = languages[i].name;
                    }


                    translate.show();

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), getString(R.string.translation_error), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), getString(R.string.translation_error), Toast.LENGTH_SHORT).show();
            }
        });
        request.setTag(TAG);

        queue.add(request);
    }

    void getTranslation(String code) {
        String url = "https://translate.yandex.net/api/v1.5/tr.json/translate?"
                + "key="
                + getString(R.string.yandex_key)
                + "&lang="
                + code
                +"&text=";

        //url = url.replace(" ","%20");
        url = url + Uri.encode(OriginalText);


        final StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String translation = jsonObject.getJSONArray("text").getString(0);
                    textView.setText(translation);

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), getString(R.string.translation_error), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), getString(R.string.translation_error), Toast.LENGTH_SHORT).show();
            }
        });
        request.setTag(TAG);

        queue.add(request);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

    public void loadPicture() {
        if (bottle.avatar.equals(StaticConfig.STR_DEFAULT)) {
            picture.setImageDrawable(AppCompatResources.getDrawable(getActivity(), R.drawable.default_avata));
        } else {
            Picasso.get().load(bottle.avatar).into(picture);
        }
    }

    public void setBackground(String paper) {
        int number = Integer.parseInt(paper);
        switch (number) {
            case 1:
                message.setBackgroundResource(R.drawable.paper1);
                break;
            case 2:
                message.setBackgroundResource(R.drawable.paper2);
                break;
            case 3:
                message.setBackgroundResource(R.drawable.paper3);
                break;
            case 4:
                message.setBackgroundResource(R.drawable.paper4);
                break;
            case 5:
                message.setBackgroundResource(R.drawable.paper5);
                break;
            case 6:
                message.setBackgroundResource(R.drawable.paper6);
                break;
            case 7:
                message.setBackgroundResource(R.drawable.paper7);
                break;
            case 8:
                message.setBackgroundResource(R.drawable.paper8);
                break;
            case 9:
                message.setBackgroundResource(R.drawable.paper9);
                break;

        }
    }

}

