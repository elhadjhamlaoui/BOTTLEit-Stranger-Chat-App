package com.app_republic.bottle.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by elhadj on 04/09/2018.
 */

public class Bottle_view implements Parcelable {
    public String name;
    public String country;
    public String id;
    public String sender;
    public body message;
    public String avatar;
    public String gender;
    public Long time;

    public Map<String, String> filters;
    public ArrayList<Receiver> receiver;

    public Bottle_view(DataSnapshot snapshot) {
        this.receiver = new ArrayList<>();

        for (DataSnapshot data : snapshot.child("receivers").getChildren()) {
            String name = "";
            String country = "";
            String status = "";
            long timeStamp = 0;
            String uid = data.getKey();

            if (data.child("name").getValue() != null)
                name = data.child("name").getValue().toString();
            if (data.child("country").getValue() != null)
                country = data.child("country").getValue().toString();
            if (data.child("status").getValue() != null)
                status = data.child("status").getValue().toString();
            if (data.child("timeStamp").getValue() != null)
                timeStamp = (long) data.child("timeStamp").getValue();

            this.receiver.add(new Receiver(uid,name,country,status,timeStamp));
        }

        this.filters = new HashMap<>();
        for (DataSnapshot data : snapshot.child("filters").getChildren())
            this.filters.put(data.getKey(), data.getValue().toString());

        this.name = snapshot.child("name").getValue().toString();
        this.country = snapshot.child("country").getValue().toString();
        this.id = snapshot.getKey();
        this.sender = snapshot.child("sender").getValue().toString();
        this.message = new body(snapshot.child("message").child("text").getValue().toString());
        this.message.paper = snapshot.child("message").child("paper").getValue().toString();
        this.message.font = snapshot.child("message").child("font").getValue().toString();


        if (snapshot.child("message").child("image").getValue() != null)
            this.message.image = snapshot.child("message").child("image").getValue().toString();

        if (snapshot.child("message").child("video").getValue() != null)
            this.message.video = snapshot.child("message").child("video").getValue().toString();

        this.avatar = snapshot.child("avatar").getValue().toString();
        this.time = Long.parseLong(snapshot.child("time").getValue().toString());
        this.gender = snapshot.child("gender").getValue().toString();
    }

    protected Bottle_view(Parcel in) {
        name = in.readString();
        country = in.readString();
        id = in.readString();
        sender = in.readString();
        avatar = in.readString();
        gender = in.readString();
        if (in.readByte() == 0) {
            time = null;
        } else {
            time = in.readLong();
        }
        receiver = in.createTypedArrayList(Receiver.CREATOR);
    }

    public static final Creator<Bottle_view> CREATOR = new Creator<Bottle_view>() {
        @Override
        public Bottle_view createFromParcel(Parcel in) {
            return new Bottle_view(in);
        }

        @Override
        public Bottle_view[] newArray(int size) {
            return new Bottle_view[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    public Receiver getLastReceiver() {
        /*long max = 0;
        HashMap<>
        for (Receiver receiver : receiver) {
            if (receiver.getTimeStamp() > max)
                max = receiver.getTimeStamp();
        }*/
        return receiver.size() > 0 ? receiver.get(receiver.size()-1):null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(country);
        parcel.writeString(id);
        parcel.writeString(sender);
        parcel.writeString(avatar);
        parcel.writeString(gender);
        if (time == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(time);
        }
        parcel.writeTypedList(receiver);
    }
}
