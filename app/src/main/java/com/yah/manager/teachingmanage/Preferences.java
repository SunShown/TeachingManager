package com.yah.manager.teachingmanage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.yah.manager.teachingmanage.Bean.User;

/**
 * Created by Administrator on 2018/4/5.
 */

public class Preferences{
    private static Preferences preferences;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    public static Preferences getInstance(Context context){
        if (preferences == null){
            preferences = new Preferences(context);
        }
        return preferences;
    }

    public Preferences(Context context) {
        if (sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences("sp",Context.MODE_PRIVATE);
        }
        if (editor == null){
            editor = sharedPreferences.edit();
        }
    }

    public void saveUserMsg(String userBean){
        editor.putString("user_msg",userBean);
        editor.commit();
    }

    public User getUserMsg(){
       String userMsg =  sharedPreferences.getString("user_msg","");
       Gson gson = new Gson();
       User user = null;
       try {
           user = gson.fromJson(userMsg,User.class);
       }catch (Exception e){

       }
       return user;
    }
}
