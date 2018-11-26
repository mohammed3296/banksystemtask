package com.example.nedalhossam.banksystem.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.nedalhossam.banksystem.LoginActivity;
import com.example.nedalhossam.banksystem.model.User;

/**
 * Created by Nedal Hossam on 11/13/2017.
 */

public class Sesstion {
    private static final String SHARED_PREF_NAME = "UserSesstion";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_Account_Number = "keyaccountnumber";
    private static final String KEY_FIRSTNAME = "keyfirstname";
    private static final String KEY_SECONDNAME = "keysecondname";

    private static Sesstion mInstance;
    private static Context mCtx;

    private Sesstion(Context context) {
        mCtx = context;
    }

    public static synchronized Sesstion getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Sesstion(context);
        }
        return mInstance;
    }

    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putInt(KEY_Account_Number, user.getAccountNumber());
        editor.putString(KEY_FIRSTNAME, user.getFirstName());
        editor.putString(KEY_SECONDNAME, user.getSecondName());
        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL, null) != null;
    }

    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getInt(KEY_Account_Number, -1),
                sharedPreferences.getString(KEY_FIRSTNAME, null),
                sharedPreferences.getString(KEY_SECONDNAME, null),
                sharedPreferences.getString(KEY_EMAIL, null)

        );
    }

    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }
}
