package com.app_republic.bottle.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.app_republic.bottle.R;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.Comment;
import com.app_republic.bottle.util.AppSingleton;
import com.app_republic.bottle.util.Utils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    List<Comment> list;
    Picasso picasso;
    Context context;
    OnItemClick onItemClick;
    FirebaseFirestore db;
    AppSingleton appSingleton;

    public CommentsAdapter(List<Comment> list, Context context, OnItemClick onItemClick) {
        this.context = context;
        this.onItemClick = onItemClick;
        this.list = list;
        appSingleton = AppSingleton.getInstance(context);
        picasso = appSingleton.getPicasso();
        db = appSingleton.getDb();
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_comment, viewGroup, false);

        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder viewHolder, int i) {

        Comment comment = list.get(i);

        viewHolder.name.setText(comment.getName());
        viewHolder.num_likes.setText(String.valueOf(comment.getLikes()));
        viewHolder.num_dislikes.setText(String.valueOf(comment.getDislikes()));
        viewHolder.body.setText(comment.getText());
        viewHolder.time.setText(Utils.getCommentDate(comment.getTimeStamp()));

        if (comment.getReplies() > 0)
            viewHolder.replies.setText(comment.getReplies() + " " + context.getString(R.string.comments));
        else
            viewHolder.replies.setText("");

        if (!comment.getPhoto().equals(StaticConfig.STR_DEFAULT))
            picasso.load(comment.getPhoto()).fit().into(viewHolder.photo);
        else
            viewHolder.photo.setImageResource(R.drawable.ic_account);


        if (comment.getLike() == 1) {
            viewHolder.like.setImageResource(R.drawable.ic_like_blue);
            viewHolder.dislike.setImageResource(R.drawable.ic_dislike);
        } else if (comment.getLike() == 0) {
            viewHolder.like.setImageResource(R.drawable.ic_like);
            viewHolder.dislike.setImageResource(R.drawable.ic_dislike_blue);
        } else {
            viewHolder.like.setImageResource(R.drawable.ic_like);
            viewHolder.dislike.setImageResource(R.drawable.ic_dislike);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView photo, like, dislike;

        View V_root, V_like, V_dislike, V_replies;

        TextView name, time, body, num_likes, num_dislikes, replies;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            V_root = itemView.findViewById(R.id.root);
            V_like = itemView.findViewById(R.id.like_layout);
            V_dislike = itemView.findViewById(R.id.dislike_layout);
            V_replies = itemView.findViewById(R.id.replies_layout);

            photo = itemView.findViewById(R.id.photo);
            name = itemView.findViewById(R.id.name);

            like = itemView.findViewById(R.id.like);
            dislike = itemView.findViewById(R.id.dislike);
            time = itemView.findViewById(R.id.time);
            body = itemView.findViewById(R.id.body);
            num_likes = itemView.findViewById(R.id.num_likes);
            num_dislikes = itemView.findViewById(R.id.num_dislikes);
            replies = itemView.findViewById(R.id.replies);

            V_root.setOnClickListener(view -> onItemClick.onClick(view, getAdapterPosition()));

            V_replies.setOnClickListener(view -> onItemClick.onClick(view, getAdapterPosition()));

            V_like.setOnClickListener(view -> {
                Comment comment = list.get(getAdapterPosition());
                V_like.setEnabled(false);
                Utils.updateFeelings(context, comment, view, true);
                onItemClick.onClick(view, getAdapterPosition());
            });

            V_dislike.setOnClickListener(view -> {
                Comment comment = list.get(getAdapterPosition());
                V_dislike.setEnabled(false);
                Utils.updateFeelings(context, comment, view, false);
                onItemClick.onClick(view, getAdapterPosition());
            });


        }


    }


    public interface OnItemClick {
        void onClick(View view, int position);
    }

}
