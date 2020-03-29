package com.app_republic.bottle.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app_republic.bottle.MainActivity;
import com.app_republic.bottle.R;
import com.app_republic.bottle.data.FriendDB;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.Friend;
import com.app_republic.bottle.model.ListFriend;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MainActivity.OnDataReceivedListener {

    private RecyclerView recyclerListFrends;
    private ListFriendsAdapter adapter;
    public FragFriendClickFloatButton onClickFloatButton;
    private ListFriend dataListFriend = null;
    private ArrayList<String> listFriendID = null;
    private ProgressDialog dialogFindAllFriend;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Button BT_rooms, BT_random;
    private CountDownTimer detectFriendOnline;
    public static int ACTION_START_CHAT = 1;
    private RelativeLayout empty_layout;
    private RelativeLayout friends_layout;
    public static final String ACTION_DELETE_FRIEND = "com.android.rivchat.DELETE_FRIEND";
    LovelyProgressDialog dialogWait;
    public Map<String, ChildEventListener> mapChildListenerOnline = new HashMap<>();
    public Map<String, DatabaseReference> mapQueryOnline = new HashMap<>();

    public Map<String, Query> mapQuery = new HashMap<>();
    public Map<String, ChildEventListener> mapChildListener = new HashMap<>();

    public static Map<String, Boolean> mapMark = new HashMap<>();


    private TextView title;
    private BroadcastReceiver deleteFriendReceiver;

    public FriendsFragment() {
        onClickFloatButton = new FragFriendClickFloatButton();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogWait = new LovelyProgressDialog(getActivity());
        MainActivity mActivity = (MainActivity) getActivity();
        dialogWait = new LovelyProgressDialog(getActivity());
        mActivity.setAboutDataListener(this);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        detectFriendOnline = new CountDownTimer(System.currentTimeMillis(), StaticConfig.TIME_TO_REFRESH) {
            @Override
            public void onTick(long l) {
                //ServiceUtils.updateFriendStatus(getContext(), dataListFriend);
               // ServiceUtils.updateUserStatus(getContext());
            }

            @Override
            public void onFinish() {

            }
        };

        dataListFriend = new ListFriend();
        /*if (dataListFriend == null) {
            dataListFriend = FriendDB.getInstance(getContext()).getListFriend();
            if (dataListFriend.getListFriend().size() > 0) {
                listFriendID = new ArrayList<>();
                for (Friend friend : dataListFriend.getListFriend()) {
                    listFriendID.add(friend.id);
                }
                detectFriendOnline.start();
            }
        }*/
        View layout = inflater.inflate(R.layout.fragment_people, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerListFrends = layout.findViewById(R.id.recycleListFriend);
        recyclerListFrends.setLayoutManager(linearLayoutManager);
        mSwipeRefreshLayout = layout.findViewById(R.id.swipeRefreshLayout);
        title = layout.findViewById(R.id.title);

        BT_random = layout.findViewById(R.id.random);
        BT_rooms = layout.findViewById(R.id.chat_rooms);

        empty_layout = layout.findViewById(R.id.empty_layout);
        friends_layout = layout.findViewById(R.id.friends_layout);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        adapter = new ListFriendsAdapter(getContext(), dataListFriend, this);
        recyclerListFrends.setAdapter(adapter);
        dialogFindAllFriend = new ProgressDialog(getActivity());

        listFriendID = new ArrayList<>();
        dialogFindAllFriend.setCancelable(true);
        dialogFindAllFriend.setMessage("Updating friend list...");
        dialogFindAllFriend.setTitle(null);
        dialogFindAllFriend.show();
        getListFriendUId();


        deleteFriendReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String idDeleted = intent.getExtras().getString("idFriend");
                for (Friend friend : dataListFriend.getListFriend()) {
                    if (idDeleted.equals(friend.id)) {
                        ArrayList<Friend> friends = dataListFriend.getListFriend();
                        friends.remove(friend);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        };

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/candy.otf");

        title = layout.findViewById(R.id.empty_text);
        title.setTypeface(type);

        IntentFilter intentFilter = new IntentFilter(ACTION_DELETE_FRIEND);
        getContext().registerReceiver(deleteFriendReceiver, intentFilter);


        BT_rooms.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CountriesActivity.class));
        });
        BT_random.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Comming soon", Toast.LENGTH_SHORT).show();
           // startActivity(new Intent(getActivity(), RandomChat.class));
        });
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        getContext().unregisterReceiver(deleteFriendReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ACTION_START_CHAT == requestCode && data != null && mapMark != null) {
            mapMark.put(data.getStringExtra("idFriend"), false);
        }
    }

    @Override
    public void onRefresh() {
        listFriendID.clear();
        dataListFriend.getListFriend().clear();
        clearListeners();
        FriendDB.getInstance(getContext()).dropDB();
        detectFriendOnline.cancel();
        getListFriendUId();
    }

    @Override
    public void onDataReceived(String idFriend, String id) {
        findIDEmail(idFriend, id);
    }

    public void findIDEmail(final String uid, final String bottleId) {
        dialogWait.setCancelable(false)
                .setTitle("Finding friend....")
                .show();
        FirebaseDatabase.getInstance().getReference().child("user").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialogWait.dismiss();
                if (dataSnapshot.getValue() == null) {
                    //user not found

                } else {
                    HashMap userMap = (HashMap) dataSnapshot.getValue();
                    String id = uid;
                    Friend user = new Friend();
                    user.name = (String) userMap.get("name");
                    user.email = (String) userMap.get("email");
                    user.avatar = (String) userMap.get("avatar");
                    user.id = id;
                    user.idRoom = id.compareTo(StaticConfig.UID) > 0 ? (StaticConfig.UID + id).hashCode() + "" : "" + (id + StaticConfig.UID).hashCode();
                    checkBeforAddFriend(id, user, bottleId);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Lay danh sach friend cua m?t UID
     */
    private void checkBeforAddFriend(final String idFriend, Friend userInfo, String id) {

        dialogWait.setCancelable(false)
                .setTitle("Add friend....")
                .show();

        //Check xem da ton tai id trong danh sach id chua

        addFriend(idFriend, id, userInfo);


    }


    private void addFriend(final String idFriend, final String id, final Friend userInfo) {
        final String uid = StaticConfig.UID;
        Map<String, String> data = new HashMap<>();
        data.put("uid", uid);
        data.put("friend", idFriend);
        FirebaseFunctions
                .getInstance()
                .getHttpsCallable("friend")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        if (task.isSuccessful()) {
                            JSONObject object = new JSONObject(task.getResult().getData().toString());
                            String success = "false";
                            try {
                                success = object.getString("success");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (success.equals("true")) {
                                dialogWait.dismiss();
                                new LovelyInfoDialog(getContext())
                                        .setTitle("Success")
                                        .setMessage("Add friend success")
                                        .show();

                                if (!listFriendID.contains(idFriend)) {
                                    listFriendID.add(idFriend);
                                    dataListFriend.getListFriend().add(userInfo);


                                    try {
                                        FriendDB.getInstance(getContext()).addFriend(userInfo);

                                    } catch (SQLiteConstraintException e) {
                                        e.printStackTrace();
                                    }
                                }

                                adapter.notifyDataSetChanged();
                            } else {
                                dialogWait.dismiss();
                                new LovelyInfoDialog(getContext())
                                        .setTitle("Success")
                                        .setMessage("Add friend failed")
                                        .show();
                            }

                            String result = task.getResult().getData().toString();

                            return result;

                        } else {
                            dialogWait.dismiss();
                            new LovelyInfoDialog(getContext())
                                    .setTitle("Success")
                                    .setMessage(task.getException().getMessage() +
                                            "  " + task.getResult().toString())
                                    .show();
                        }
                        return null;

                    }
                });

    }

    public class FragFriendClickFloatButton {
        Context context;
        LovelyProgressDialog dialogWait;

        public FragFriendClickFloatButton() {
        }

        public FragFriendClickFloatButton getInstance(Context context) {
            this.context = context;
            dialogWait = new LovelyProgressDialog(context);
            return this;
        }
    }

    /**
     * Lay danh sach ban be tren server
     */
    private void getListFriendUId() {
        FirebaseDatabase.getInstance().getReference().child("friend/" + StaticConfig.UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    empty_layout.setVisibility(View.INVISIBLE);
                    friends_layout.setVisibility(View.VISIBLE);

                    HashMap mapRecord = (HashMap) dataSnapshot.getValue();
                    Iterator listKey = mapRecord.keySet().iterator();
                    while (listKey.hasNext()) {
                        String key = listKey.next().toString();
                        listFriendID.add(mapRecord.get(key).toString());
                    }
                    getAllFriendInfo(0);

                } else {
                    empty_layout.setVisibility(View.VISIBLE);
                    friends_layout.setVisibility(View.INVISIBLE);

                    dialogFindAllFriend.dismiss();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    /**
     * Truy cap bang user lay thong tin id nguoi dung
     */

    private void getAllFriendInfo(final int index) {
        if (index >= listFriendID.size()) {
            //save list friend
            adapter.notifyDataSetChanged();
            dialogFindAllFriend.dismiss();
            mSwipeRefreshLayout.setRefreshing(false);
            detectFriendOnline.start();
        } else {
                final String id = listFriendID.get(index);
                FirebaseDatabase.getInstance().getReference().child("user/" + id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            final Friend user = new Friend();
                            HashMap mapUserInfo = (HashMap) dataSnapshot.getValue();
                            user.name = (String) mapUserInfo.get("name");
                            user.email = (String) mapUserInfo.get("email");
                            user.avatar = (String) mapUserInfo.get("avatar");
                            user.id = id;
                            user.idRoom = id.compareTo(StaticConfig.UID) > 0 ? (StaticConfig.UID + id).hashCode() + "" : "" + (id + StaticConfig.UID).hashCode();

                            if (!dataListFriend.getListFriend().contains(user)) {
                                dataListFriend.getListFriend().add(user);
                                try {
                                    FriendDB.getInstance(getContext()).addFriend(user);


                                    if (mapQueryOnline.get(id) == null && mapChildListenerOnline.get(id) == null) {
                                        mapQueryOnline.put(id, FirebaseDatabase.getInstance().getReference().child("user/" + id + "/status"));
                                        mapChildListenerOnline.put(id, new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                if (dataSnapshot.getValue() != null && dataSnapshot.getKey().equals("isOnline")) {
                                                    user.status.isOnline = (boolean) dataSnapshot.getValue();
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }

                                            @Override
                                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                                if (dataSnapshot.getValue() != null && dataSnapshot.getKey().equals("isOnline")) {
                                                    user.status.isOnline = (boolean) dataSnapshot.getValue();
                                                    adapter.notifyDataSetChanged();


                                                }
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
                                        mapQueryOnline.get(id).addChildEventListener(mapChildListenerOnline.get(id));
                                    }


                                    if (mapQuery.get(id) == null && mapChildListener.get(id) == null) {
                                        mapQuery.put(id, FirebaseDatabase.getInstance().getReference().child("message/" + user.idRoom).limitToLast(1));
                                        mapChildListener.put(id, new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                HashMap mapMessage = (HashMap) dataSnapshot.getValue();

                                                user.message.timestamp = (long) mapMessage.get("timestamp");

                                                if (mapMark.get(id) != null) {
                                                    if (!mapMark.get(id)) {
                                                        user.message.text = id + mapMessage.get("text");
                                                    } else {
                                                        user.message.text = (String) mapMessage.get("text");
                                                    }
                                                    adapter.notifyDataSetChanged();
                                                    mapMark.put(id, false);
                                                } else {
                                                    user.message.text = (String) mapMessage.get("text");
                                                    adapter.notifyDataSetChanged();
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
                                        mapQuery.get(id).addChildEventListener(mapChildListener.get(id));
                                        mapMark.put(id, true);
                                    } else {
                                        mapMark.put(id, true);
                                    }
                                } catch (SQLiteConstraintException e) {
                                    e.printStackTrace();
                                }
                            }



                        }
                        getAllFriendInfo(index + 1);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        }
    }

    public void clearListeners() {
        Iterator it = mapQuery.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            mapQuery.get(pair.getKey()).removeEventListener(mapChildListener.get(pair.getKey()));
            it.remove();
        }

        Iterator it2 = mapQueryOnline.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry pair = (Map.Entry) it2.next();
            mapQueryOnline.get(pair.getKey()).removeEventListener(mapChildListenerOnline.get(pair.getKey()));
            it2.remove();

        }

        mapQueryOnline.clear();
        mapChildListenerOnline.clear();
        mapQuery.clear();
        mapChildListener.clear();
    }
}

class ListFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ListFriend listFriend;
    private Context context;

    private FriendsFragment fragment;
    LovelyProgressDialog dialogWaitDeleting;

    public ListFriendsAdapter(Context context, ListFriend listFriend, FriendsFragment fragment) {
        this.listFriend = listFriend;
        this.context = context;

        this.fragment = fragment;
        dialogWaitDeleting = new LovelyProgressDialog(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_friend, parent, false);
        return new ItemFriendViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        String name = listFriend.getListFriend().get(holder.getAdapterPosition()).name;
        String id = listFriend.getListFriend().get(holder.getAdapterPosition()).id;

        ((ItemFriendViewHolder) holder).txtName.setText(name);

        ((View) ((ItemFriendViewHolder) holder).txtName.getParent().getParent().getParent())
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.getAdapterPosition() < listFriend.getListFriend().size()) {
                            String name = listFriend.getListFriend().get(holder.getAdapterPosition()).name;
                            String id = listFriend.getListFriend().get(holder.getAdapterPosition()).id;
                            String idRoom = listFriend.getListFriend().get(holder.getAdapterPosition()).idRoom;
                            String avatar = listFriend.getListFriend().get(holder.getAdapterPosition()).avatar;

                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra(StaticConfig.INTENT_KEY_CHAT_FRIEND, name);
                            intent.putExtra(StaticConfig.INTENT_KEY_CHAT_ID, id);
                            intent.putExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID, idRoom);
                            intent.putExtra(StaticConfig.INTENT_KEY_CHAT_AVATAR, avatar);

                            ChatActivity.bitmapAvataFriend = new HashMap<>();
                            ChatActivity.bitmapAvataFriend.put(id, avatar);

                            FriendsFragment.mapMark.put(id, null);
                            ((AppCompatActivity) context).startActivityForResult(intent, FriendsFragment.ACTION_START_CHAT);
                        }

                    }
                });

        ((View) ((ItemFriendViewHolder) holder).txtName.getParent().getParent().getParent())
                .setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (holder.getAdapterPosition() < listFriend.getListFriend().size()) {
                            String friendName = (String) ((ItemFriendViewHolder) holder).txtName.getText();

                            new AlertDialog.Builder(context)
                                    .setTitle("Delete Friend")
                                    .setMessage("Are you sure want to delete " + friendName + "?")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            final String idFriendRemoval = listFriend.getListFriend().get(holder.getAdapterPosition()).id;
                                            dialogWaitDeleting.setTitle("Deleting...")
                                                    .setCancelable(false)
                                                    .setTopColorRes(R.color.colorAccent)
                                                    .show();
                                            deleteFriend(idFriendRemoval);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).show();

                        }

                        return true;
                    }
                });


        if (listFriend.getListFriend().get(position).message.text.length() > 0) {
            ((ItemFriendViewHolder) holder).txtMessage.setVisibility(View.VISIBLE);
            ((ItemFriendViewHolder) holder).txtTime.setVisibility(View.VISIBLE);
            if (!listFriend.getListFriend().get(position).message.text.startsWith(id)) {
                ((ItemFriendViewHolder) holder).txtMessage.setText(listFriend.getListFriend().get(position).message.text);
                ((ItemFriendViewHolder) holder).txtMessage.setTypeface(Typeface.DEFAULT);
                ((ItemFriendViewHolder) holder).txtName.setTypeface(Typeface.DEFAULT);
            } else {
                ((ItemFriendViewHolder) holder).txtMessage.setText(listFriend.getListFriend().get(position).message.text.substring((id + "").length()));
                ((ItemFriendViewHolder) holder).txtMessage.setTypeface(Typeface.DEFAULT_BOLD);
                ((ItemFriendViewHolder) holder).txtName.setTypeface(Typeface.DEFAULT_BOLD);
            }
            String time = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(listFriend.getListFriend().get(position).message.timestamp));
            String today = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(System.currentTimeMillis()));
            if (today.equals(time)) {
                ((ItemFriendViewHolder) holder).txtTime.setText(new SimpleDateFormat("HH:mm").format(new Date(listFriend.getListFriend().get(position).message.timestamp)));
            } else {
                ((ItemFriendViewHolder) holder).txtTime.setText(new SimpleDateFormat("MMM d").format(new Date(listFriend.getListFriend().get(position).message.timestamp)));
            }
        } else {
            ((ItemFriendViewHolder) holder).txtMessage.setVisibility(View.GONE);
            ((ItemFriendViewHolder) holder).txtTime.setVisibility(View.GONE);

        }
        if (listFriend.getListFriend().get(position).avatar.equals(StaticConfig.STR_DEFAULT)) {
            ((ItemFriendViewHolder) holder).avatar.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.default_avata));
        } else {
            Picasso.get().load(listFriend.getListFriend().get(position).avatar).into(((ItemFriendViewHolder) holder).avatar);

        }


        if (listFriend.getListFriend().get(position).status.isOnline) {
            ((ItemFriendViewHolder) holder).online.setVisibility(View.VISIBLE);
        } else {
            ((ItemFriendViewHolder) holder).online.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return listFriend.getListFriend() != null ? listFriend.getListFriend().size() : 0;
    }

    /**
     * Delete friend
     *
     * @param idFriend
     */
    private void deleteFriend(final String idFriend) {
        if (idFriend != null) {
            FirebaseDatabase.getInstance().getReference().child("friend").child(StaticConfig.UID)
                    .orderByValue().equalTo(idFriend).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() == null) {
                        //email not found
                        dialogWaitDeleting.dismiss();
                        new LovelyInfoDialog(context)
                                .setTopColorRes(R.color.colorAccent)
                                .setTitle("Error")
                                .setMessage("Error occurred during deleting friend")
                                .show();
                    } else {
                        String idRemoval = ((HashMap) dataSnapshot.getValue()).keySet().iterator().next().toString();
                        FirebaseDatabase.getInstance().getReference().child("friend")
                                .child(StaticConfig.UID).child(idRemoval).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialogWaitDeleting.dismiss();

                                        new LovelyInfoDialog(context)
                                                .setTopColorRes(R.color.colorAccent)
                                                .setTitle("Success")
                                                .setMessage("Friend deleting successfully")
                                                .show();

                                        Intent intentDeleted = new Intent(FriendsFragment.ACTION_DELETE_FRIEND);
                                        intentDeleted.putExtra("idFriend", idFriend);
                                        context.sendBroadcast(intentDeleted);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialogWaitDeleting.dismiss();
                                        new LovelyInfoDialog(context)
                                                .setTopColorRes(R.color.colorAccent)
                                                .setTitle("Error")
                                                .setMessage("Error occurred during deleting friend")
                                                .show();
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            dialogWaitDeleting.dismiss();
            new LovelyInfoDialog(context)
                    .setTopColorRes(R.color.colorPrimary)
                    .setTitle("Error")
                    .setMessage("Error occurred during deleting friend")
                    .show();
        }
    }
}

class ItemFriendViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView avatar;
    public TextView txtName, txtTime, txtMessage;
    private Context context;
    public ImageView online;

    ItemFriendViewHolder(Context context, View itemView) {
        super(itemView);
        avatar = itemView.findViewById(R.id.icon_avata);
        txtName = itemView.findViewById(R.id.txtName);
        txtTime = itemView.findViewById(R.id.txtTime);
        txtMessage = itemView.findViewById(R.id.txtMessage);
        online = itemView.findViewById(R.id.online);

        this.context = context;
    }
}