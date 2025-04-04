package com.example.homedoctor;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String PREF_NAME = "HomeDoctorSession";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_COURSE = "course";
    private static final String KEY_LAST_PAYMENT_TIME = "lastPaymentTime"; // Stores last payment timestamp

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(String name, String email, String phone, String course, long lastPaymentTime) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_COURSE, course);
        editor.putLong(KEY_LAST_PAYMENT_TIME, lastPaymentTime);
        editor.apply();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_NAME, sharedPreferences.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, sharedPreferences.getString(KEY_EMAIL, null));
        user.put(KEY_PHONE, sharedPreferences.getString(KEY_PHONE, null));
        user.put(KEY_COURSE, sharedPreferences.getString(KEY_COURSE, null));
        return user;
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    public long getLastPaymentTime() {
        return sharedPreferences.getLong(KEY_LAST_PAYMENT_TIME, 0);
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}
