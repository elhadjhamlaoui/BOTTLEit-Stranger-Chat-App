package com.app_republic.bottle.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Country implements Parcelable {
    private String name, id;
    private int index;


    public Country() {
    }

    public Country(String name, String id, int index) {
        this.name = name;
        this.id = id;
        this.index = index;
    }

    protected Country(Parcel in) {
        name = in.readString();
        id = in.readString();
        index = in.readInt();
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(id);
        parcel.writeInt(index);
    }
}
