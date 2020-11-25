package com.sebastianstext.pegasusbeta.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.sebastianstext.pegasusbeta.UserRelatedClasses.LoginActivity;

public class SharedPrefManager {

    //the constants
    private static final String SHARED_PREF_NAME = "simplifiedcodingsharedpref";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_ID = "keyid";
    private static final String KEY_SPEED = "keyspeed";
    private static final String KEY_STOPS = "keystops";
    private static final String KEY_DIST = "keydist";
    private static final String KEY_HORSE = "keyhorse";
    private static final String KEY_HORSEHEIGHT = "keyhorseheight";

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

    public void workouts(WorkoutsList workoutsList){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, workoutsList.getUser());
        editor.putInt(KEY_STOPS, workoutsList.getNmbrstops());
        editor.putString(KEY_SPEED, workoutsList.getAvrgspeed());
        editor.putInt(KEY_DIST, workoutsList.getMeters());
        editor.apply();
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

    public WorkoutsList getWorkout(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new WorkoutsList(
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getInt(KEY_STOPS, -1),
                sharedPreferences.getString(KEY_SPEED, null),
                sharedPreferences.getInt(KEY_DIST, -1)
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
