package com.app_republic.bottle.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Feeling implements Parcelable {
    private boolean state;
    private String targetType;
    private String targetId;
    private String id;



    public Feeling() {
    }

    public Feeling(boolean state, String targetType, String targetId, String id) {
        this.state = state;
        this.targetType = targetType;
        this.targetId = targetId;
        this.id = id;
    }

    protected Feeling(Parcel in) {
        state = in.readByte() != 0;
        targetType = in.readString();
        targetId = in.readString();
        id = in.readString();
    }

    public static final Creator<Feeling> CREATOR = new Creator<Feeling>() {
        @Override
        public Feeling createFromParcel(Parcel in) {
            return new Feeling(in);
        }

        @Override
        public Feeling[] newArray(int size) {
            return new Feeling[size];
        }
    };

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (state ? 1 : 0));
        parcel.writeString(targetType);
        parcel.writeString(targetId);
        parcel.writeString(id);
    }
}
