package com.app_republic.bottle.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.text.emoji.widget.EmojiTextView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.fragment.CommentsFragment;
import com.app_republic.bottle.model.Adventure;
import com.app_republic.bottle.model.Language;
import com.app_republic.bottle.service.ServiceUtils;
import com.app_republic.bottle.util.Static;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by elhadj on 07/09/2018.
 */


public class receivedAdventure extends Fragment {
    Dialog dialog;
    static final int name = 0;

    ImageView video, image, report, picture;
    TextView nameView, date;
    EmojiTextView textView, titleView;
    ScrollView scrollView;
    FloatingActionButton close, like, comments;
    RelativeLayout message;
    Adventure adventure;
    FloatingActionButton translate;
    boolean liked;
    FirebaseFunctions firebaseFunctions;
    RequestQueue queue;
    String[] langs, codes;
    private int currentLang = 0;

    final String TAG = "tag";
    String OriginalText;
    Map<String, String> map = new HashMap();

   /* @Override
    public void onStart() {

        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;

    }*/

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.received_ad, null);
       // getDialog().getWindow().setBackgroundDrawableResource(R.color.cardview_light_background);

        queue = Volley.newRequestQueue(getActivity());
        scrollView = view.findViewById(R.id.scrollView);
        message = view.findViewById(R.id.message);
        comments = view.findViewById(R.id.comments);

        like = view.findViewById(R.id.like);

        video = view.findViewById(R.id.video);
        image = view.findViewById(R.id.image);
        translate = view.findViewById(R.id.translate);

        translate.hide();

        report = view.findViewById(R.id.report);
        picture = view.findViewById(R.id.picture);

        nameView = view.findViewById(R.id.name);
        date = view.findViewById(R.id.date);
        textView = view.findViewById(R.id.text);
        titleView = view.findViewById(R.id.title);


        firebaseFunctions = FirebaseFunctions.getInstance();

        adventure = getArguments().getParcelable("adventure");
        loadPicture();

        getSupportedLanguages();


        final String sender = adventure.sender;

        final String id = adventure.id;
        final String videoText = adventure.video;

        final String imageText = adventure.image;


        final String name = adventure.name;

        OriginalText = adventure.text;

        long time = adventure.time;

        String title = adventure.title;


        if (!adventure.font.equals("normal")) {
            Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/" + adventure.font);


            textView.setTypeface(type);
            titleView.setTypeface(type);

        }

        queue = Volley.newRequestQueue(getActivity());


        setBackground(adventure.paper);

        SimpleDateFormat formatter = new SimpleDateFormat("dd  MM,yyyy");
        final String dateString = formatter.format(new Date(time));
        date.setText(dateString);
        nameView.setText(name);
        titleView.setText(title);
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


        close.setOnClickListener(view13 -> getActivity().onBackPressed());
        report.setOnClickListener(view14 -> Toast.makeText(getActivity(), "Comming soon.", Toast.LENGTH_SHORT).show());


        image.setOnClickListener(view15 -> {
            play_image dialog = new play_image();
            Bundle argumants = new Bundle();
            argumants.putString("image", imageText);
            dialog.setArguments(argumants);

            if (!getActivity().isDestroyed() && !getActivity().isFinishing())
                getActivity().getSupportFragmentManager().beginTransaction().add(dialog, "image").commitAllowingStateLoss();
        });
        video.setOnClickListener(view16 -> {
            play_video dialog = new play_video();
            Bundle argumants = new Bundle();
            argumants.putString("video", videoText);
            dialog.setArguments(argumants);

            if (!getActivity().isDestroyed() && !getActivity().isFinishing())
                getActivity().getSupportFragmentManager().beginTransaction().add(dialog, "video").commitAllowingStateLoss();


        });

        comments.setOnClickListener(v -> {
            CommentsFragment fragment = CommentsFragment.newInstance();
            Bundle args = new Bundle();

            args.putString(Static.TARGET_TYPE,
                    Static.STORY);
            args.putString(Static.TARGET_ID,
                    adventure.id);

            args.putString(Static.ROOT_TYPE,
                    Static.STORY);
            args.putString(Static.ROOT_ID,
                    adventure.id);


            args.putParcelable(Static.ADVENTURE,
                    adventure);

            fragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .addToBackStack(Static.FRAGMENT_COMMENTS)
                    .add(R.id.container, fragment)
                    .commit();
        });

        picture.setOnClickListener(view1 -> {
            profile_view dialog = new profile_view();

            Bundle bundle = new Bundle();
            bundle.putString("uid", adventure.sender);
            dialog.setArguments(bundle);
            dialog.show(getActivity().getSupportFragmentManager(), "profile_view");
        });

        like.setOnClickListener(view12 -> {
            like.setEnabled(false);
            FirebaseDatabase.getInstance().getReference().child("adventure").child(adventure.id)
                    .child("likes").runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Long value = mutableData.getValue(Long.class);

                    if (value > 0) {
                        if (liked)
                            mutableData.setValue(value - 1);
                        else
                            mutableData.setValue(value + 1);
                    } else {
                        if (!liked)
                            mutableData.setValue(value + 1);
                    }




                    return Transaction.success(mutableData);
                }



                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {

                    FirebaseDatabase.getInstance().getReference().child("adventure").child(adventure.id)
                            .child("liked_by").child(StaticConfig.UID).setValue(!liked).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            like.setEnabled(true);
                        }
                    });

                }
            });

        });

        like.hide();

        FirebaseDatabase.getInstance().getReference().child("adventure").child(adventure.id)
                .child("liked_by").child(StaticConfig.UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null && dataSnapshot.getValue().toString().equals("true")) {
                    like.setImageResource(R.drawable.like);
                    liked = true;
                } else {
                    like.setImageResource(R.drawable.like_light);
                    liked = false;
                }
                like.show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = object.getString(key);
                        map.put(key, value);
                    }

                    Language[] languages = ServiceUtils.convertMap(map);

                    Arrays.sort(languages);

                    languages[0] = new Language("or", "Original language");

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
                + "&text=";

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


    public void loadPicture() {
        if (adventure.avatar.equals(StaticConfig.STR_DEFAULT)) {
            picture.setImageDrawable(AppCompatResources.getDrawable(getActivity(), R.drawable.default_avata));
        } else {
            Picasso.get().load(adventure.avatar).into(picture);
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

