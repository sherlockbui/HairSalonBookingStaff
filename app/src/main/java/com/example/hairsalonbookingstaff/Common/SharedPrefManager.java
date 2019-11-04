package com.example.hairsalonbookingstaff.Common;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.hairsalonbookingstaff.Model.Barber;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "SHARED_PREF_NAME";
    private static final String KEY_ID = "KEY_ID";
    private static final String KEY_NAME = "KEY_NAME";
    private static final String KEY_USERNAME = "KEY_USERNAME";
    private static final String KEY_ID_BRANCH = "KEY_ID_BRANCH";
    private static final String KEY_RATING = "KEY_RATING";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }
    public void saveInfoBarber(Barber barber) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ID, barber.getId());
        editor.putString(KEY_NAME, barber.getName());
        editor.putString(KEY_USERNAME, barber.getUsername());
        editor.putString(KEY_ID_BRANCH, barber.getIdbranch());
        editor.putLong(KEY_RATING, barber.getRating());
        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ID, null) != null;
    }

    //this method will give the logged in user
    public Barber getBarber() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new Barber(
                sharedPreferences.getString(KEY_ID, null),
                sharedPreferences.getString(KEY_NAME, null),
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_ID_BRANCH, null),
                sharedPreferences.getLong(KEY_RATING, 0)
        );
    }
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
