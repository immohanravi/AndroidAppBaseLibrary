package com.bogarsoft.baselibrary;

import android.content.Context;
import android.content.SharedPreferences;

public class StorageUtility {

    private String STORAGE;
    private SharedPreferences preferences;
    private Context context;

    public StorageUtility(Context context) {
        this.context = context;
        this.STORAGE = context.getPackageName()+".STORAGE";
    }

    public void setName(String name){
        preferences = context.getSharedPreferences(STORAGE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("name",name);
        editor.apply();
    }

    public String getName(){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getString("name", "");//return -1 if no data found
    }

    public void setEmail(String email){
        preferences = context.getSharedPreferences(STORAGE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email",email);
        editor.apply();
    }

    public String getEmail(){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getString("email", "");//return -1 if no data found
    }

    public void setuuid(String uuid){
        preferences = context.getSharedPreferences(STORAGE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("uuid",uuid);
        editor.apply();
    }

    public String getuuid(){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getString("uuid", "");//return -1 if no data found
    }

    public void settoken1(String token) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token1", token);
        editor.apply();
    }
    public String gettoken1(){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getString("token1", "");//return -1 if no data found
    }

    public void settoken2(String token) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token2", token);
        editor.apply();
    }


    public String gettoken2(){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getString("token2", "");//return -1 if no data found
    }

    public void setLogin(boolean an){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean("isloggedin",an);
        editor.commit();
    }

    public boolean isLoggedIn(){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        return preferences.getBoolean("isloggedin",false);
    }

    public void subscribeduser(String user_uuid){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(user_uuid,true);
        editor.commit();
    }

    public boolean isSubscribeduser(String user_uuid){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        return preferences.getBoolean(user_uuid,false);
    }
    public void subscribed(String match_uuid){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(match_uuid,true);
        editor.commit();
    }

    public boolean isSubscribed(String match_uuid){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        return preferences.getBoolean(match_uuid,false);
    }

    public void unsubscribed(String match_uuid){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(match_uuid,true);
        editor.commit();
    }

    public boolean isunSubscribed(String match_uuid){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        return preferences.getBoolean(match_uuid,false);
    }

    public void pubgsubscribed(){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("pubggod",true);
        editor.commit();
    }

    public boolean ispubgSubscribed(){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        return preferences.getBoolean("pubggod",false);
    }

    public void setFirstMatchJoined(){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("joined",true);
        editor.commit();
    }

    public void clearAll(){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public boolean isFirstMatchJoined(){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        return preferences.getBoolean("joined",false);
    }

    public void clear(){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

}
