package com.app_republic.bottle.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.app_republic.bottle.util.Static;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by elhadj on 17/09/2018.
 */

public class filterFollowing extends Fragment {


    FirebaseFunctions mFunctions;



    ArrayList<Adventure> list = new ArrayList<>();
    Adapter adapter;
    SwipeRefreshLayout refreshLayout;
    boolean notloading ;
    ProgressBar loading ;

    String start=null;

    public filterFollowing() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mFunctions = FirebaseFunctions.getInstance();

        View rootView = inflater.inflate(R.layout.world_layout, container, false);
        notloading = true;


        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        loading = rootView.findViewById(R.id.loading);


        adapter = new Adapter(getActivity(),list);


        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(adapter);


        getAdventures();

        refreshLayout = rootView.findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                notloading=false;

                start=null;
                getAdventures();
            }
        });

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);



                if (!recyclerView.canScrollVertically(1)&&dy>0&&notloading) {

                    notloading=false;
                    loading.setVisibility(View.VISIBLE);
                    getAdventures();
                }
            }
        });


        return rootView;
    }

    public void getAdventures(){
        Map<String, String> data = new HashMap<>();
        data.put("uid", StaticConfig.UID);
        data.put("start", start);
         mFunctions
                .getHttpsCallable("following_adventures")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        if (task.isSuccessful()){
                            JSONObject object = new JSONObject(task.getResult().getData().toString());


                            for (int i = 0; i < object.length()-1;i++){

                                String mJsonString = object.getJSONObject(i+"").toString();
                                JsonParser parser = new JsonParser();
                                JsonElement mJson =  parser.parse(mJsonString);
                                Gson gson = new Gson();
                                Adventure adventure = gson.fromJson(mJson, Adventure.class);
                                list.add(adventure);


                            }

                            try {
                                start = object.getString("start");

                            }catch (JSONException e){
                                e.printStackTrace();
                            }

                            String result = task.getResult().getData().toString();

                            notloading=true;
                            loading.setVisibility(View.GONE);
                            refreshLayout.setRefreshing(false);
                            adapter.notifyDataSetChanged();
                            return result;

                        }
                        notloading=true;
                        loading.setVisibility(View.GONE);
                        refreshLayout.setRefreshing(false);
                        adapter.notifyDataSetChanged();
                        return null;

                    }
                });

    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private ArrayList<Adventure> list;

        public static final int VIEW_TYPE = 0;
        public static final int VIEW_TYPE_IMAGE = 1;

        public Adapter(Context context, ArrayList<Adventure> list) {
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
                    Picasso.get().load(stravata)
                            .fit()
                            .centerInside()
                            .into(((viewHolder)holder).avatar);
                }else{
                    ((viewHolder)holder).avatar.setImageDrawable(AppCompatResources.getDrawable(getActivity(), R.drawable.account_white_small));

                }

                SimpleDateFormat formatter = new SimpleDateFormat("dd  MM,yyyy");
                String dateString = formatter.format(new Date(list.get(position).time));

                ((viewHolder)holder).name.setText(list.get(position).name);
                ((viewHolder)holder).country.setText(list.get(position).country);
                ((viewHolder)holder).title.setText(list.get(position).title);
                ((viewHolder)holder).body.setText(list.get(position).text);
                ((viewHolder)holder).time.setText(dateString);
                ((viewHolder)holder).likes.setText(Integer.toString(list.get(position).likes));
                setBackground(((viewHolder)holder).bac,list.get(position).paper);

            } else if (holder instanceof viewHolder_photo) {

                String stravata = list.get(position).avatar;

                if (!stravata.equals(StaticConfig.STR_DEFAULT)) {
                    Picasso.get().load(stravata)
                            .fit()
                            .centerInside()
                            .into(((viewHolder_photo)holder).avatar);

                }else{
                    ((viewHolder_photo)holder).avatar.setImageDrawable(AppCompatResources.getDrawable(getActivity(), R.drawable.account_white_small));

                }

                SimpleDateFormat formatter = new SimpleDateFormat("dd  MM,yyyy");
                String dateString = formatter.format(new Date(list.get(position).time));

                ((viewHolder_photo)holder).name.setText(list.get(position).name);
                ((viewHolder_photo)holder).country.setText(list.get(position).country);
                ((viewHolder_photo)holder).title.setText(list.get(position).title);
                ((viewHolder_photo)holder).time.setText(dateString);
                ((viewHolder_photo)holder).likes.setText(Integer.toString(list.get(position).likes));
                setBackground(((viewHolder_photo)holder).bac,list.get(position).paper);

                Picasso.get().load(list.get(position).image)
                        .fit()
                        .centerInside()
                        .into(((viewHolder_photo)holder).imageView);

            }
        }
        public void setBackground(ImageView bac, String paper){
            int number = Integer.parseInt(paper);
            switch (number) {
                case 1 : bac.setImageResource(R.drawable.paper1); break;
                case 2 : bac.setImageResource(R.drawable.paper2);break;
                case 3 : bac.setImageResource(R.drawable.paper3);break;
                case 4 : bac.setImageResource(R.drawable.paper4);break;
                case 5 : bac.setImageResource(R.drawable.paper5);break;
                case 6 : bac.setImageResource(R.drawable.paper6);break;
                case 7 : bac.setImageResource(R.drawable.paper7);break;
                case 8 : bac.setImageResource(R.drawable.paper8);break;
                case 9 : bac.setImageResource(R.drawable.paper9);break;

            }
        }
        @Override
        public int getItemViewType(int position) {
            return list.get(position).image==null ? VIEW_TYPE : VIEW_TYPE_IMAGE;
        }


        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class viewHolder extends RecyclerView.ViewHolder {
        public EmojiTextView title,body;
        public CircleImageView avatar;
        public TextView name,country,time,likes;
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
        public ImageView imageView,bac;
        public TextView name,country,time,likes;
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
        Intent intent = new Intent(getActivity(), receivedAdventure.class);
        intent.putExtra("adventure", adventure);
        startActivity(intent);
        /*receivedAdventure dialog = new receivedAdventure();
        Bundle argumants = new Bundle();
        argumants.putParcelable("adventure",adventure);

        dialog.setArguments(argumants);
        if(!getActivity().isDestroyed() && !getActivity().isFinishing())
            getActivity().getSupportFragmentManager().beginTransaction()
                    .addToBackStack(Static.FRAGMENT_ADVENTURE)

                    .add(R.id.container, dialog, "received_adventure").commitAllowingStateLoss();


         */

    }
}
