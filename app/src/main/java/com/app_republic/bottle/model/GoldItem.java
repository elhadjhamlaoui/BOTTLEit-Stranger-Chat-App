package com.app_republic.bottle.model;

import android.os.Parcel;
import android.os.Parcelable;


public class GoldItem implements Parcelable {
    private String price;
    private int coins;
    private int discount;

    public GoldItem(String price, int coins, int discount) {
        this.price = price;
        this.coins = coins;
        this.discount = discount;
    }

    protected GoldItem(Parcel in) {
        price = in.readString();
        coins = in.readInt();
        discount = in.readInt();
    }

    public static final Creator<GoldItem> CREATOR = new Creator<GoldItem>() {
        @Override
        public GoldItem createFromParcel(Parcel in) {
            return new GoldItem(in);
        }

        @Override
        public GoldItem[] newArray(int size) {
            return new GoldItem[size];
        }
    };

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(price);
        parcel.writeInt(coins);
        parcel.writeInt(discount);
    }
}
