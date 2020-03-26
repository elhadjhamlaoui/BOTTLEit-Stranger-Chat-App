package com.app_republic.bottle.model;


import com.app_republic.bottle.data.StaticConfig;

public class User {

    public String interests = "",languages = "";
    public String wall = "";
    public String birthdate = "";
    public String country = "GB";
    public String gender = "";
    public String name;
    public String email;
    public String token;
    public long followings;
    public long followers;
    public String avatar = StaticConfig.STR_DEFAULT;
    public Status status;
    public Message message;
    public int owls = 5;
    public int compass = 5;
    public int bottles = 5;
    public int feathers = 5;
    public int reputation = 5;
    public int gold = 10;



    public User(){
        status = new Status();
        message = new Message();
        status.isOnline = false;
        status.timestamp = 0;
        message.idReceiver = "0";
        message.idSender = "0";
        message.text = "";
        message.timestamp = 0;
    }
}
