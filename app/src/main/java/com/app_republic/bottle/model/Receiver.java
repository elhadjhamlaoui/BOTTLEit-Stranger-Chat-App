package com.app_republic.bottle.model;


import android.os.Parcel;
import android.os.Parcelable;


public class Receiver implements Parcelable {
    private String name;
    private String country;
    private String status;
    private String uid;
    private long timeStamp;

    public Receiver(String uid,String name, String country, String status, long timeStamp) {
        this.name = name;
        this.uid = uid;
        this.country = country;
        this.status = status;
        this.timeStamp = timeStamp;

    }

    protected Receiver(Parcel in) {
        name = in.readString();
        country = in.readString();
        status = in.readString();
        uid = in.readString();
        timeStamp = in.readLong();
    }

    public static final Creator<Receiver> CREATOR = new Creator<Receiver>() {
        @Override
        public Receiver createFromParcel(Parcel in) {
            return new Receiver(in);
        }

        @Override
        public Receiver[] newArray(int size) {
            return new Receiver[size];
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(country);
        parcel.writeString(status);
        parcel.writeString(uid);
        parcel.writeLong(timeStamp);
    }
}
