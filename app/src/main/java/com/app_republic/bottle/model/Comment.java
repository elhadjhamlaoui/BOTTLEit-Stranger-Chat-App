package com.app_republic.bottle.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {

    private String targetId, targetType, uid, text, photo, name, id;
    private long likes, dislikes, timeStamp;
    private boolean feeling;
    private int like;
    private String rootType;
    private String rootId;
    private long replies;



    public Comment() {

    }

    public Comment(String targetId, String targetType, String uid, String text, String photo,
                   String name, String id, long likes, long dislikes,
                   long timeStamp, boolean feeling, int like, String rootType,
                   String rootId, long replies) {
        this.targetId = targetId;
        this.targetType = targetType;
        this.uid = uid;
        this.text = text;
        this.photo = photo;
        this.name = name;
        this.id = id;
        this.likes = likes;
        this.dislikes = dislikes;
        this.timeStamp = timeStamp;
        this.feeling = feeling;
        this.like = like;
        this.rootType = rootType;
        this.rootId = rootId;
        this.replies = replies;
    }

    protected Comment(Parcel in) {
        targetId = in.readString();
        targetType = in.readString();
        uid = in.readString();
        text = in.readString();
        photo = in.readString();
        name = in.readString();
        id = in.readString();
        likes = in.readLong();
        dislikes = in.readLong();
        timeStamp = in.readLong();
        feeling = in.readByte() != 0;
        like = in.readInt();
        rootType = in.readString();
        rootId = in.readString();
        replies = in.readLong();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getDislikes() {
        return dislikes;
    }

    public void setDislikes(long dislikes) {
        this.dislikes = dislikes;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isFeeling() {
        return feeling;
    }

    public void setFeeling(boolean feeling) {
        this.feeling = feeling;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getRootType() {
        return rootType;
    }

    public void setRootType(String rootType) {
        this.rootType = rootType;
    }

    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    public long getReplies() {
        return replies;
    }

    public void setReplies(long replies) {
        this.replies = replies;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(targetId);
        parcel.writeString(targetType);
        parcel.writeString(uid);
        parcel.writeString(text);
        parcel.writeString(photo);
        parcel.writeString(name);
        parcel.writeString(id);
        parcel.writeLong(likes);
        parcel.writeLong(dislikes);
        parcel.writeLong(timeStamp);
        parcel.writeByte((byte) (feeling ? 1 : 0));
        parcel.writeInt(like);
        parcel.writeString(rootType);
        parcel.writeString(rootId);
        parcel.writeLong(replies);
    }


}
