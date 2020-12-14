package com.sebastianstext.pegasusbeta.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sebastianstext.pegasusbeta.DataStorage.Horse;
import com.sebastianstext.pegasusbeta.DataStorage.User;
import com.sebastianstext.pegasusbeta.DataStorage.Workout;
import com.sebastianstext.pegasusbeta.UserRelatedClasses.LoginActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPrefManager {

    //the constants
    private static final String SHARED_PREF_NAME = "simplifiedcodingsharedpref";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_ID = "keyid";
    private static final String KEY_DIST = "keydist";
    private static final String KEY_NAME = "keyname";
    private static final String KEY_HEIGHT = "keyheight";
    private static final String KEY_BREED = "keybreed";
    private static final String KEY_METERS = "meters";
    private static final String KEY_METERSSKRITT = "metersskritt";
    private static final String KEY_METERSTRAV = "meters";
    private static final String KEY_METERSGALLOPP = "metersgallopp";
    private static final String KEY_STOPS = "stops";
    private static final String KEY_RIGHTVOLT = "rightvolt";
    private static final String KEY_LEFTVOLT = "leftvolt";
    private static final String KEY_SPEED = "speed";

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

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.apply();
    }

    public void putHorse(Horse horse) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME, horse.getName());
        editor.putString(KEY_BREED, horse.getBreed());
        editor.putInt(KEY_HEIGHT, horse.getHeight());
        editor.apply();
    }

    public void saveArrayList(ArrayList<String> list, String key){
        SharedPreferences prefs = SharedPrefManager.mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();

    }

    public ArrayList<String> getArrayList(String key){
        SharedPreferences prefs = SharedPrefManager.mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }



    public void saveWorkoutlistList(ArrayList<String> list, String key){
        SharedPreferences prefs = SharedPrefManager.mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();

    }

    public ArrayList<String> getWorkoutList(String key){
        SharedPreferences prefs = SharedPrefManager.mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }


    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    //this method will give the logged in user
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getInt(KEY_ID, -1),
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_EMAIL, null)
        );
    }

    public Horse getHorse() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new Horse(
                sharedPreferences.getString(KEY_NAME, null),
                sharedPreferences.getInt(KEY_HEIGHT, -1),
                sharedPreferences.getString(KEY_BREED, null)

        );
    }

    public Workout getWorkout(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new Workout(
                sharedPreferences.getInt(KEY_METERS, -1),
                sharedPreferences.getInt(KEY_STOPS, -1),
                sharedPreferences.getInt(KEY_SPEED, -1),
                sharedPreferences.getInt(KEY_METERSGALLOPP, -1),
                sharedPreferences.getInt(KEY_METERSSKRITT, -1),
                sharedPreferences.getInt(KEY_METERSTRAV, -1),
                sharedPreferences.getInt(KEY_RIGHTVOLT, -1),
                sharedPreferences.getInt(KEY_LEFTVOLT, -1)
        );
    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }
}
