package com.app_republic.bottle.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.text.emoji.widget.EmojiTextView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app_republic.bottle.R;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.Adventure;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by elhadj on 16/09/2018.
 */

public class World extends Fragment {

    ArrayList<Adventure> list = new ArrayList<>();
    WorldAdapter adapter;
    SwipeRefreshLayout refreshLayout;
    String start = null;
    boolean notloading;

    public World() {
        // Required empty public constructor
    }

    ProgressBar loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.world_layout, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        loading = rootView.findViewById(R.id.loading);
        notloading = true;

        adapter = new WorldAdapter(getActivity(), list);


        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(adapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                if (!recyclerView.canScrollVertically(1) && dy > 0 && notloading) {

                    notloading = false;
                    loading.setVisibility(View.VISIBLE);
                    getAdventures();
                }
            }
        });

        getAdventures();

        refreshLayout = rootView.findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                notloading = false;

                start = null;
                getAdventures();
            }
        });


        return rootView;
    }

    public void getAdventures() {
        Query ref = FirebaseDatabase.getInstance().getReference().child("adventure");
        if (start != null)
            ref = ref.orderByChild("id").startAt(start);
        ref = ref.limitToFirst(5);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (i != 0 || start == null) {
                        Gson gson = new Gson();
                        String mJsonString = gson.toJson(snapshot.getValue());
                        JsonParser parser = new JsonParser();
                        JsonElement mJson = parser.parse(mJsonString);
                        Adventure adventure = gson.fromJson(mJson, Adventure.class);

                        list.add(adventure);

                        if (i == (dataSnapshot.getChildrenCount() - 1)) {
                            start = snapshot.child("id").getValue().toString();

                        }


                    }

                    i++;

                }


                notloading = true;
                loading.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                notloading = true;
                loading.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
            }
        });

    }

    class WorldAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private ArrayList<Adventure> list;

        public static final int VIEW_TYPE = 0;
        public static final int VIEW_TYPE_IMAGE = 1;

        public WorldAdapter(Context context, ArrayList<Adventure> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            if (viewType == VIEW_TYPE) {
                View view = LayoutInflater.from(context).inflate(R.layout.world_item, parent, false);
                return new viewHolder(view);
            } else if (viewType == VIEW_TYPE_IMAGE) {
                View view = LayoutInflater.from(context).inflate(R.layout.world_item_photo, parent, false);
                return new viewHolder_photo(view);
            }
            return null;


        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            if (holder instanceof viewHolder) {

                String stravata = list.get(position).avatar;

                if (!stravata.equals(StaticConfig.STR_DEFAULT)) {
                    Picasso.get().load(stravata).into(((viewHolder) holder).avatar);

                } else {
                    ((viewHolder) holder).avatar.setImageDrawable(AppCompatResources.getDrawable(getActivity(), R.drawable.default_avata));

                }
                SimpleDateFormat formatter = new SimpleDateFormat("dd  MM,yyyy");
                String dateString = formatter.format(new Date(list.get(position).time));

                ((viewHolder) holder).name.setText(list.get(position).name);
                ((viewHolder) holder).country.setText(list.get(position).country);
                ((viewHolder) holder).title.setText(list.get(position).title);
                ((viewHolder) holder).body.setText(list.get(position).text);
                ((viewHolder) holder).time.setText(dateString);
                ((viewHolder) holder).likes.setText(Integer.toString(list.get(position).likes));
                setBackground(((viewHolder)holder).bac,list.get(position).paper);

            } else if (holder instanceof viewHolder_photo) {
                String stravata = list.get(position).avatar;

                if (!stravata.equals(StaticConfig.STR_DEFAULT)) {
                    Picasso.get().load(stravata).into(((viewHolder_photo)holder).avatar);
                } else {
                    ((viewHolder_photo) holder).avatar.setImageDrawable(AppCompatResources.getDrawable(getActivity(), R.drawable.default_avata));

                }
                SimpleDateFormat formatter = new SimpleDateFormat("dd  MM,yyyy");
                String dateString = formatter.format(new Date(list.get(position).time));

                ((viewHolder_photo) holder).name.setText(list.get(position).name);
                ((viewHolder_photo) holder).country.setText(list.get(position).country);
                ((viewHolder_photo) holder).title.setText(list.get(position).title);
                ((viewHolder_photo) holder).time.setText(dateString);
                ((viewHolder_photo) holder).likes.setText(Integer.toString(list.get(position).likes));
                setBackground(((viewHolder_photo)holder).bac,list.get(position).paper);

                Picasso.get().load(list.get(position).image).into(((viewHolder_photo) holder).imageView);

            }
        }

        public void setBackground(ImageView bac, String paper) {
            int number = Integer.parseInt(paper);
            switch (number) {
                case 1:
                    bac.setImageResource(R.drawable.paper1);
                    break;
                case 2:
                    bac.setImageResource(R.drawable.paper2);
                    break;
                case 3:
                    bac.setImageResource(R.drawable.paper3);
                    break;
                case 4:
                    bac.setImageResource(R.drawable.paper4);
                    break;
                case 5:
                    bac.setImageResource(R.drawable.paper5);
                    break;
                case 6:
                    bac.setImageResource(R.drawable.paper6);
                    break;
                case 7:
                    bac.setImageResource(R.drawable.paper7);
                    break;
                case 8:
                    bac.setImageResource(R.drawable.paper8);
                    break;
                case 9:
                    bac.setImageResource(R.drawable.paper9);
                    break;

            }
        }

        @Override
        public int getItemViewType(int position) {
            return list.get(position).image == null ? VIEW_TYPE : VIEW_TYPE_IMAGE;
        }


        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class viewHolder extends RecyclerView.ViewHolder {
        public EmojiTextView title, body;
        public CircleImageView avatar;
        public TextView name, country, time, likes;
        public ImageView bac;
        public CardView root;


        public viewHolder(View itemView) {
            super(itemView);
            bac = itemView.findViewById(R.id.backgound);
            root = itemView.findViewById(R.id.root);
            title = itemView.findViewById(R.id.title);
            body = itemView.findViewById(R.id.body);
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.name);
            country = itemView.findViewById(R.id.country);
            time = itemView.findViewById(R.id.time);
            likes = itemView.findViewById(R.id.likes);

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    show_adventure(list.get(getAdapterPosition()));
                }
            });
        }
    }

    class viewHolder_photo extends RecyclerView.ViewHolder {
        public EmojiTextView title;
        public CircleImageView avatar;
        public ImageView imageView, bac;
        public TextView name, country, time, likes;
        public CardView root;


        public viewHolder_photo(View itemView) {
            super(itemView);
            bac = itemView.findViewById(R.id.backgound);
            root = itemView.findViewById(R.id.root);
            title = itemView.findViewById(R.id.title);
            avatar = itemView.findViewById(R.id.avatar);
            imageView = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.name);
            country = itemView.findViewById(R.id.country);
            time = itemView.findViewById(R.id.time);
            likes = itemView.findViewById(R.id.likes);

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    show_adventure(list.get(getAdapterPosition()));
                }
            });
        }
    }
    public void show_adventure(final Adventure adventure) {
        receivedAdventure dialog = new receivedAdventure();
        Bundle argumants = new Bundle();
        argumants.putParcelable("adventure",adventure);

        dialog.setArguments(argumants);
        if(!getActivity().isDestroyed() && !getActivity().isFinishing())
            getActivity().getSupportFragmentManager().beginTransaction().add(dialog, "received_adventure").commitAllowingStateLoss();

    }


}