package com.app_republic.bottle.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Map;

/**
 * Created by elhadj on 01/09/2018.
 */

public class Bottle implements Parcelable {
    public String sender,name,country,gender,avatar;
    public body message;
    public Long time;
    public String id;

    public Map<String,String> filters;
    public List<Receiver> receiver;
    public Bottle(String sender,List<Receiver> receiver,body message,Map<String,String> map){
        this.receiver = receiver;
        this.sender = sender;
        this.message = message;
        this.filters=map;
    }

    protected Bottle(Parcel in) {
        sender = in.readString();
        name = in.readString();
        country = in.readString();
        gender = in.readString();
        avatar = in.readString();
        if (in.readByte() == 0) {
            time = null;
        } else {
            time = in.readLong();
        }
        id = in.readString();
        receiver = in.createTypedArrayList(Receiver.CREATOR);
    }

    public static final Creator<Bottle> CREATOR = new Creator<Bottle>() {
        @Override
        public Bottle createFromParcel(Parcel in) {
            return new Bottle(in);
        }

        @Override
        public Bottle[] newArray(int size) {
            return new Bottle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(sender);
        parcel.writeString(name);
        parcel.writeString(country);
        parcel.writeString(gender);
        parcel.writeString(avatar);
        if (time == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(time);
        }
        parcel.writeString(id);
        parcel.writeTypedList(receiver);
    }
}
