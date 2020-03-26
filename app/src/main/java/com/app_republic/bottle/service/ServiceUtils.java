package com.app_republic.bottle.service;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.IBinder;

import com.app_republic.bottle.model.Language;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.Friend;
import com.app_republic.bottle.model.ListFriend;
import com.mukesh.countrypicker.CountryPicker;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class ServiceUtils {

    private static ServiceConnection connectionServiceFriendChatForStart = null;
    private static ServiceConnection connectionServiceFriendChatForDestroy = null;

    public static boolean isServiceFriendChatRunning(Context context) {
        Class<?> serviceClass = FriendChatService.class;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void stopServiceFriendChat(Context context, final boolean kill) {
        if (isServiceFriendChatRunning(context)) {
            Intent intent = new Intent(context, FriendChatService.class);
            if (connectionServiceFriendChatForDestroy != null) {
                context.unbindService(connectionServiceFriendChatForDestroy);
            }
            connectionServiceFriendChatForDestroy = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName className,
                                               IBinder service) {
                    FriendChatService.LocalBinder binder = (FriendChatService.LocalBinder) service;
                    binder.getService().stopSelf();
                }

                @Override
                public void onServiceDisconnected(ComponentName arg0) {
                }
            };
            context.bindService(intent, connectionServiceFriendChatForDestroy, Context.BIND_NOT_FOREGROUND);
        }
    }

    public static void stopRoom(Context context, final String idRoom) {
        if (isServiceFriendChatRunning(context)) {
            Intent intent = new Intent(context, FriendChatService.class);
            if (connectionServiceFriendChatForDestroy != null) {
                context.unbindService(connectionServiceFriendChatForDestroy);
                connectionServiceFriendChatForDestroy = null;
            }
            connectionServiceFriendChatForDestroy = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName className,
                                               IBinder service) {
                    FriendChatService.LocalBinder binder = (FriendChatService.LocalBinder) service;
                    binder.getService().stopNotify(idRoom);
                }

                @Override
                public void onServiceDisconnected(ComponentName arg0) {
                }
            };
            context.bindService(intent, connectionServiceFriendChatForDestroy, Context.BIND_NOT_FOREGROUND);
        }
    }

    public static void startServiceFriendChat(Context context) {
        if (!isServiceFriendChatRunning(context)) {
            Intent myIntent = new Intent(context, FriendChatService.class);
            context.startService(myIntent);
        } else {
            if (connectionServiceFriendChatForStart != null) {
                context.unbindService(connectionServiceFriendChatForStart);
            }
            connectionServiceFriendChatForStart = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName className,
                                               IBinder service) {
                    FriendChatService.LocalBinder binder = (FriendChatService.LocalBinder) service;
                    for (Friend friend : binder.getService().listFriend.getListFriend()) {
                        binder.getService().mapMark.put(friend.idRoom, true);
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName arg0) {
                }
            };
            Intent intent = new Intent(context, FriendChatService.class);
            context.bindService(intent, connectionServiceFriendChatForStart, Context.BIND_NOT_FOREGROUND);
        }
    }

    public static void updateUserStatus(Context context) {
        if (isNetworkConnected(context)) {
            String uid = SharedPreferenceHelper.getInstance(context).getUID();
            if (!uid.equals("")) {
                FirebaseDatabase.getInstance().getReference().child("user/" + uid + "/status/isOnline").setValue(true);
                FirebaseDatabase.getInstance().getReference().child("user/" + uid + "/status/timestamp").setValue(System.currentTimeMillis());
            }
        }
    }


    public static void updateFriendStatus(Context context, ListFriend listFriend) {
        if (isNetworkConnected(context)) {
            for (Friend friend : listFriend.getListFriend()) {
                final String fid = friend.id;
                FirebaseDatabase.getInstance().getReference().child("user/" + fid + "/status").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            HashMap mapStatus = (HashMap) dataSnapshot.getValue();
                            if ((boolean) mapStatus.get("isOnline") && (System.currentTimeMillis() - (long) mapStatus.get("timestamp")) > StaticConfig.TIME_TO_OFFLINE) {
                                FirebaseDatabase.getInstance().getReference().child("user/" + fid + "/status/isOnline").setValue(false);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    public static boolean isNetworkConnected(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null;
        } catch (Exception e) {
            return true;
        }
    }

    public static int getCount(String s) {
        int count = 0;
        char slash = '/';
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == slash) {
                count++;
            }
        }
        return count;
    }

    public static String getCountryName(Context context, String code) {

        CountryPicker picker = new CountryPicker.Builder().with(context).build();

        picker.getCountryByISO("CD").setName("Congo");
        picker.getCountryByISO("FM").setName("Micronesia");
        picker.getCountryByISO("VE").setName("Venezuela");
        picker.getCountryByISO("BO").setName("Bolivia");
        picker.getCountryByISO("IR").setName("Iran");
        picker.getCountryByISO("MD").setName("Moldova");
        picker.getCountryByISO("MK").setName("Macedonia");
        picker.getCountryByISO("PS").setName("Palestine");
        picker.getCountryByISO("TZ").setName("Tanzania");
        picker.getCountryByISO("LY").setName("Libya");


        String name = "";
        if (picker.getCountryByISO(code) != null)
            name = picker.getCountryByISO(code).getName();
        return name;
    }
    public static int getCountryFlag(Context context, String code) {

        CountryPicker picker = new CountryPicker.Builder().with(context).build();

        picker.getCountryByISO("CD").setName("Congo");
        picker.getCountryByISO("FM").setName("Micronesia");
        picker.getCountryByISO("VE").setName("Venezuela");
        picker.getCountryByISO("BO").setName("Bolivia");
        picker.getCountryByISO("IR").setName("Iran");
        picker.getCountryByISO("MD").setName("Moldova");
        picker.getCountryByISO("MK").setName("Macedonia");
        picker.getCountryByISO("PS").setName("Palestine");
        picker.getCountryByISO("TZ").setName("Tanzania");
        picker.getCountryByISO("LY").setName("Libya");

        if (picker.getCountryByISO(code) != null)
        return picker.getCountryByISO(code).getFlag();

        return -1;
    }

    public static int getAge(String birthdate) {
        String[] array = birthdate.split("/");

        if (array.length != 3)
            return 0;

        int day = Integer.parseInt(array[0]);
        int month = Integer.parseInt(array[1]);
        int year = Integer.parseInt(array[2]);

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        return age;
    }

    public static Language[] add2BeginningOfArray(Language[] elements,Language element)
    {
        Language[] newArray = Arrays.copyOf(elements, elements.length + 1);
        newArray[0] = element;
        System.arraycopy(elements, 0, newArray, 1, elements.length);

        return newArray;
    }

    public static Language[] convertMap(Map<String, String> elements) {
        Language[] languages = new Language[elements.size()+1];
        languages[0] = new Language("or","AAAA");

        int index = 1;
        for (Map.Entry<String, String> mapEntry : elements.entrySet()) {
            languages[index] = new Language(mapEntry.getKey(),mapEntry.getValue());
            index++;
        }

        return languages;

    }
}


