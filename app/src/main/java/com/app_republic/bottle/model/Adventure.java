package com.app_republic.bottle.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by elhadj on 01/09/2018.
 */

public class Adventure implements Parcelable {
    public String sender,avatar="",name,country,id;
    public long time;

    public  int likes;
    public String text,title;
    public String image = null;
    public String video = null;
    public String font = "normal";
    public String paper = "1";
    public Adventure(){
    }

    protected Adventure(Parcel in) {
        sender = in.readString();
        avatar = in.readString();
        name = in.readString();
        country = in.readString();
        id = in.readString();
        time = in.readLong();
        likes = in.readInt();
        text = in.readString();
        title = in.readString();
        image = in.readString();
        video = in.readString();
        font = in.readString();
        paper = in.readString();
    }

    public static final Creator<Adventure> CREATOR = new Creator<Adventure>() {
        @Override
        public Adventure createFromParcel(Parcel in) {
            return new Adventure(in);
        }

        @Override
        public Adventure[] newArray(int size) {
            return new Adventure[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(sender);
        parcel.writeString(avatar);
        parcel.writeString(name);
        parcel.writeString(country);
        parcel.writeString(id);
        parcel.writeLong(time);
        parcel.writeInt(likes);
        parcel.writeString(text);
        parcel.writeString(title);
        parcel.writeString(image);
        parcel.writeString(video);
        parcel.writeString(font);
        parcel.writeString(paper);
    }
}
