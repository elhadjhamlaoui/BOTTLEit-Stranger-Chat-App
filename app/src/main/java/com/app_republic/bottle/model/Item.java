package com.app_republic.bottle.model;

public class Item {
    private String name,desc,key;
    private int icon;
    private int price;

    public Item(String key, String name, String desc ,int icon, int price) {
        this.name = name;
        this.desc = desc;
        this.icon = icon;
        this.price = price;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
