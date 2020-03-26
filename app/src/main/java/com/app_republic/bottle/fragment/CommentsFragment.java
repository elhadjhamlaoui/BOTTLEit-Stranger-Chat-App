package com.app_republic.bottle.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app_republic.bottle.R;
import com.app_republic.bottle.adapter.CommentsAdapter;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.Adventure;
import com.app_republic.bottle.model.Comment;
import com.app_republic.bottle.model.Country;
import com.app_republic.bottle.model.Feeling;
import com.app_republic.bottle.model.User;
import com.app_republic.bottle.util.AppSingleton;
import com.app_republic.bottle.util.Static;
import com.app_republic.bottle.util.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.firestore.Query.Direction.DESCENDING;

public class CommentsFragment extends Fragment implements FirebaseAuth.AuthStateListener {

    List<Comment> comments = new ArrayList<>();
    ArrayList<Feeling> feelings = new ArrayList<>();

    TextView TV_name, TV_time, TV_body;
    FloatingActionButton FB_close;
    View V_root;
    CircleImageView IV_photo;
    Gson gson;
    Comment mainComment;



    CommentsAdapter commentsAdapter;
    RecyclerView commentsRecycler;
    EditText ET_comment;
    FloatingActionButton FB_send;
    AppSingleton appSingleton;

    EventListener commentsListener, feelingsListener;
    String rootType, rootId;

    CircleImageView IV_user_photo;
    FirebaseFirestore db;
    User user;

    boolean firstRead = true, firstRead2 = true;
    String targetType, targetId;

    Adventure adventure;
    Country country;
    private ListenerRegistration commentsRegistration,
            feelingsRegistration;

    public CommentsFragment() {
        // Required empty public constructor
    }

    public static CommentsFragment newInstance() {
        CommentsFragment fragment = new CommentsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appSingleton = AppSingleton.getInstance(getActivity());

        gson = appSingleton.getGson();
        db = appSingleton.getDb();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;


        mainComment = getArguments().getParcelable(Static.COMMENT);
        country = getArguments().getParcelable(Static.COUNTRY);
        adventure = getArguments().getParcelable(Static.ADVENTURE);

        targetId = getArguments().getString(Static.TARGET_ID);
        targetType = getArguments().getString(Static.TARGET_TYPE);

        rootType = getArguments().getString(Static.ROOT_TYPE);
        rootId = getArguments().getString(Static.ROOT_ID);


        if (targetType.equals(Static.COMMENT))
            view = inflater.inflate(R.layout.fragment_sub_comments, container, false);
        else
            view = inflater.inflate(R.layout.fragment_comments, container, false);


        initialiseViews(view);

        if (targetType.equals(Static.CHAT)) {
            getActivity().setTitle(country.getName());
        }


        if (!targetType.equals(Static.COMMENT))
            AppSingleton.getInstance(getActivity()).loadNativeAd(view.findViewById(R.id.adView));


        commentsAdapter = new CommentsAdapter(comments, getActivity(), (view1, position) -> {
            switch (view1.getId()) {
                case R.id.root:
                    if (mainComment == null)
                        showSubComments(position);
                    break;
                case R.id.replies_layout:
                    if (mainComment == null)
                        showSubComments(position);

                    break;
                case R.id.like:

                    break;
                case R.id.dislike:

                    break;
                case R.id.reply:

                    break;
            }
        });


        commentsRecycler.setAdapter(commentsAdapter);

        commentsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));


        FB_send.setOnClickListener(view1 -> {

            if (!ET_comment.getText().toString().isEmpty()) {

                FB_send.hide();
                sendComment(ET_comment.getText().toString());
            }


        });


        firstRead = true;
        firstRead2 = true;
        user = SharedPreferenceHelper.getInstance(getActivity()).getUserInfo();

        if (!user.avatar.equals(StaticConfig.STR_DEFAULT))
            appSingleton.getPicasso().load(user.avatar).into(IV_user_photo);
        else
            IV_user_photo.setImageResource(R.drawable.ic_account);


        return view;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (targetType.equals(Static.CHAT)) {
            getActivity().setTitle(getString(R.string.chat));
        }

    }


    void showSubComments(int position) {
        Fragment fragment = CommentsFragment.newInstance();

        Bundle args = new Bundle();


        args.putParcelable(Static.COMMENT,
                comments.get(position));

        args.putString(Static.TARGET_TYPE,
                Static.COMMENT);

        args.putString(Static.TARGET_ID,
                comments.get(position).getId());


        args.putString(Static.ROOT_TYPE,
                rootType);

        args.putString(Static.ROOT_ID,
                rootId);

        args.putParcelable(Static.COUNTRY,
                country);

        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.comment_in, R.anim.comment_out);
        fragmentTransaction.replace(R.id.sub_comments, fragment, Static.SUB_COMMENT);

        fragmentTransaction.addToBackStack(Static.SUB_COMMENT);
        fragmentTransaction.commit();

    }



    private void sendComment(String body) {

        db.runTransaction(transaction -> {


            DocumentReference commentRef = db.collection("comments")
                    .document();

            Comment comment = new Comment(targetId, targetType,
                    appSingleton.getFirebaseAuth().getUid(), body, user.avatar, user.name,
                    commentRef.getId(),
                    0, 0,
                    System.currentTimeMillis(), false, -1, rootType,
                    rootId, 0);


            DocumentReference mainCommentRef = db.collection("comments")
                    .document(comment.getTargetId());


            if (targetType.equals(Static.COMMENT)) {

                DocumentSnapshot mainCommentSnapshot = transaction.get(mainCommentRef);

                double newReplies = mainCommentSnapshot.getLong("replies") + 1;
                transaction.update(mainCommentRef, "replies", newReplies);
            }

            transaction.set(commentRef, comment);


            return 0;


        }).addOnSuccessListener(result -> {
            FB_send.show();
            ET_comment.setText("");
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            FB_send.show();

        });


    }

    private void initialiseViews(View view) {
        commentsRecycler = view.findViewById(R.id.comments_recycler);
        FB_send = view.findViewById(R.id.send);
        ET_comment = view.findViewById(R.id.comment_body);
        IV_user_photo = view.findViewById(R.id.user_photo);

        if (targetType.equals(Static.CHAT)) {

        } else if (targetType.equals(Static.COMMENT)) {


            IV_photo = view.findViewById(R.id.photo);
            TV_name = view.findViewById(R.id.name);

            V_root = view.findViewById(R.id.parent_comment);
            FB_close = view.findViewById(R.id.close);

            TV_time = view.findViewById(R.id.time);
            TV_body = view.findViewById(R.id.body);
            TV_name.setText(mainComment.getName());
            TV_body.setText(mainComment.getText());
            TV_time.setText(Utils.getCommentDate(mainComment.getTimeStamp()));


            if (!mainComment.getPhoto().equals(StaticConfig.STR_DEFAULT))
                appSingleton.getPicasso().load(mainComment.getPhoto()).into(IV_photo);
            else
                IV_photo.setImageResource(R.drawable.ic_account);


            View V_header = view.findViewById(R.id.header_layout);
            V_header.setVisibility(View.VISIBLE);


            FB_close.setOnClickListener(view1 -> getActivity().onBackPressed());

        }


    }


    private void getComments() {
        comments.clear();
        Query query;
        query = db.collection("comments")
                .orderBy("timeStamp", DESCENDING)
                .orderBy("likes", DESCENDING)
                .orderBy("replies", DESCENDING)
                .whereEqualTo("targetType", targetType)
                .whereEqualTo("targetId", targetId)
                .limit(50);

        commentsListener = (EventListener<QuerySnapshot>) (value, e) -> {
            String source = value != null && value.getMetadata().hasPendingWrites()
                    ? "Local" : "Server";


            if (value != null)
                for (DocumentChange dc : value.getDocumentChanges()) {
                    QueryDocumentSnapshot document = dc.getDocument();
                    Comment comment = document.toObject(Comment.class);

                    comment.setId(document.getId());

                    switch (dc.getType()) {
                        case ADDED:
                            comments.add(comment);
                            break;
                        case MODIFIED:
                            Comment c = comments.get(Utils.getCommentIndexById(comments, document.getId()));
                            c.setLikes(comment.getLikes());
                            c.setDislikes(comment.getDislikes());
                            c.setReplies(comment.getReplies());

                            break;
                        case REMOVED:
                            comments.remove(Utils.getCommentIndexById(comments, document.getId()));
                            break;

                    }
                }

            Collections.sort(comments, (comment, t1) -> {
                if (comment.getTimeStamp() > t1.getTimeStamp())
                    return -1;
                else if (comment.getTimeStamp() < t1.getTimeStamp())
                    return 1;
                return 0;
            });

            commentsAdapter.notifyDataSetChanged();

            if (firstRead) {
                firstRead = false;
                getFeelings();
            }

        };

        commentsRegistration = query.addSnapshotListener(commentsListener);


    }

    void getFeelings() {

        feelings.clear();
        Query query = db.collection("feelings")
                .document(appSingleton.getFirebaseAuth().getUid())
                .collection("topics")
                .whereEqualTo("targetId", targetId);


        feelingsListener = (EventListener<QuerySnapshot>) (value, e) -> {
            String source = value != null && value.getMetadata().hasPendingWrites()
                    ? "Local" : "Server";


            if (value != null) {
                for (DocumentChange dc : value.getDocumentChanges()) {
                    QueryDocumentSnapshot document = dc.getDocument();
                    Feeling feeling = document.toObject(Feeling.class);

                    switch (dc.getType()) {
                        case ADDED:
                            feelings.add(feeling);

                            break;
                        case MODIFIED:
                            feelings.set(Utils.getFeelingIndexById(feelings, document.getId()), feeling);
                            break;
                        case REMOVED:
                            feelings.remove(Utils.getFeelingIndexById(feelings, document.getId()));
                            break;

                    }

                    if (Utils.getCommentIndexById(comments, feeling.getId()) != -1) {
                        Comment comment = comments.get(Utils.getCommentIndexById(comments, feeling.getId()));

                        if (Utils.getFeelingIndexById(feelings, comment.getId()) == -1)
                            comment.setLike(-1);
                        else if (feelings.get(Utils.getFeelingIndexById(feelings, comment.getId())).isState())
                            comment.setLike(1);
                        else
                            comment.setLike(0);
                    }

                }

                commentsAdapter.notifyDataSetChanged();
            }


        };
        feelingsRegistration = query.addSnapshotListener(feelingsListener);


    }




    @Override
    public void onResume() {
        super.onResume();
        getComments();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (commentsRegistration != null)
            commentsRegistration.remove();
        if (feelingsRegistration != null)
            feelingsRegistration.remove();
        firstRead = true;
        firstRead2 = true;
    }


    @Override
    public void onStart() {
        super.onStart();
        appSingleton.getFirebaseAuth().addAuthStateListener(this);

    }

    @Override
    public void onStop() {
        super.onStop();
        appSingleton.getFirebaseAuth().removeAuthStateListener(this);

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            if (feelingsRegistration != null)
                feelingsRegistration.remove();
            firstRead = true;
            firstRead2 = true;
        }
    }
}
