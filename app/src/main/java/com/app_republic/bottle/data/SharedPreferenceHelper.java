package com.app_republic.bottle.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.app_republic.bottle.model.User;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SharedPreferenceHelper {
    private static SharedPreferenceHelper instance = null;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private static String SHARE_KEY_NAME = "name";
    private static String SHARE_KEY_EMAIL = "email";
    private static String SHARE_KEY_AVATA = "avatar";
    private static String SHARE_KEY_UID = "uid";
    private static String SHARE_KEY_interests = "interests";
    private static String SHARE_KEY_languages = "languages";
    private static String SHARE_KEY_birthdate = "birthdate";
    private static String SHARE_KEY_admob = "admob";

    private static String SHARE_KEY_country = "country";
    private static String SHARE_KEY_gender = "gender";
    private static String SHARE_KEY_wall = "wall";

    private static String SHARE_KEY_gold = "gold";
    private static String SHARE_KEY_reputation = "reputation";
    private static String SHARE_KEY_bottles = "bottles";
    private static String SHARE_KEY_owls = "owls";
    private static String SHARE_KEY_feathers = "feathers";

    private SharedPreferenceHelper() {
    }

    public static SharedPreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceHelper();
            String SHARE_USER_INFO = "userinfo";
            preferences = context.getSharedPreferences(SHARE_USER_INFO, Context.MODE_PRIVATE);
            editor = preferences.edit();
            editor.commit();

        }
        return instance;
    }

    public void saveUserInfo(User user) {

        Class<?> c = user.getClass();
        Field[] fields = c.getDeclaredFields();
        Map<String, Object> temp = new HashMap<String, Object>();

        editor.putString(SHARE_KEY_NAME, user.name);
        editor.putString(SHARE_KEY_EMAIL, user.email);
        editor.putString(SHARE_KEY_AVATA, user.avatar);

        editor.putString(SHARE_KEY_country, user.country);
        editor.putString(SHARE_KEY_gender, user.gender);
        editor.putString(SHARE_KEY_wall, user.wall);

        editor.putInt(SHARE_KEY_bottles, user.bottles);
        editor.putInt(SHARE_KEY_owls, user.owls);
        editor.putInt(SHARE_KEY_gold, user.gold);
        editor.putInt(SHARE_KEY_reputation, user.reputation);
        editor.putInt(SHARE_KEY_feathers, user.feathers);

        editor.putString(SHARE_KEY_interests, user.interests);
        editor.putString(SHARE_KEY_languages, user.languages);
        editor.putString(SHARE_KEY_birthdate, user.birthdate);
        editor.putString(SHARE_KEY_UID, StaticConfig.UID);
        editor.commit();
    }

    public User getUserInfo() {
        String userName = preferences.getString(SHARE_KEY_NAME, "");
        String email = preferences.getString(SHARE_KEY_EMAIL, "");
        String avatar = preferences.getString(SHARE_KEY_AVATA, "default");

        String country = preferences.getString(SHARE_KEY_country, "");
        String gender = preferences.getString(SHARE_KEY_gender, "");
        String wall = preferences.getString(SHARE_KEY_wall, "");
        String interests = preferences.getString(SHARE_KEY_interests, "");
        String languages = preferences.getString(SHARE_KEY_languages, "");
        String birthdate = preferences.getString(SHARE_KEY_birthdate, "0/01/1945");

        int gold = preferences.getInt(SHARE_KEY_gold, 0);
        int reputation = preferences.getInt(SHARE_KEY_reputation, 0);
        int bottles = preferences.getInt(SHARE_KEY_bottles, 0);
        int owls = preferences.getInt(SHARE_KEY_owls, 0);
        int feathers = preferences.getInt(SHARE_KEY_feathers, 0);

        User user = new User();
        user.name = userName;
        user.email = email;
        user.avatar = avatar;

        user.country = country;
        user.gender = gender;
        user.wall = wall;
        user.interests = interests;
        user.languages = languages;
        user.birthdate = birthdate;

        user.gold = gold;
        user.reputation = reputation;
        user.bottles = bottles;
        user.owls = owls;
        user.feathers = feathers;

        return user;
    }

    public String getUID() {
        return preferences.getString(SHARE_KEY_UID, "");
    }

    public String getAdmobId() {
        return preferences.getString(SHARE_KEY_admob, "ca-app-pub-5411690074381837/5048643943");
    }

    public void saveAdmobId(String id) {
        editor.putString(SHARE_KEY_admob, id);
        editor.commit();
    }

}
