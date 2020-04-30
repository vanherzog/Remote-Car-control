package com.example.remotecarcontrol;

import android.content.SharedPreferences;
import android.util.Log;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Saving_settings {

    protected int xB;
    protected int yB;

    protected int xF;
    protected int yF;

    protected int xL;
    protected int yL;

    protected int xR;
    protected int yR;

    private static final String TAG ="Saving_settings";
    private static Saving_settings instance;
    private static SharedPreferences mSharedPreferences;

    private Saving_settings(){}

    public static synchronized Saving_settings getInstance (SharedPreferences sharedPreferences) {
        if (Saving_settings.instance == null) {
            Saving_settings.instance = new Saving_settings();
        }
        mSharedPreferences = sharedPreferences;
        loadData();
        return Saving_settings.instance;
    }



    public void setUp(){
        instance.xB = 150;
        instance.yB = 150;

        instance.xF = 20;
        instance.yF = 20;

        instance.xR = 80;
        instance.yR = 80;

        instance.xL = 200;
        instance.yL = 200;
        //saveData();
    }


    public void savePosition(int xB, int yB, int xF, int yF, int xL, int yL, int xR, int yR){
        instance.xB = xB;
        instance.yB = yB;

        instance.xF = xF;
        instance.yF = yF;

        instance.xR = xR;
        instance.yR = yR;

        instance.xL = xL;
        instance.yL = yL;
        saveData();
    }

    public void savePositionB(int xB, int yB){
        instance.xB = xB;
        instance.yB = yB;
        saveData();
    }

    public void savePositionF(int xF, int yF){
        instance.xF = xF;
        instance.yF = yF;
        saveData();
    }

    public void savePositionR(int xR, int yR){
        instance.xR = xR;
        instance.yR = yR;
        saveData();
    }

    public void savePositionL(int xL, int yL){
        instance.xL = xL;
        instance.yL = yL;
        saveData();
    }







    public static void loadData(){
        Gson gson = new Gson();
        String json = mSharedPreferences.getString("Saving_settings", null);
        if(null!=json) {
            Log.d(TAG, "loadData: " + json);
            Type typ = new TypeToken<Saving_settings>() {
            }.getType();
            instance = gson.fromJson(json, typ);
        }
    }



    public void saveData(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(instance);
        Log.d(TAG, "saveData: " + json);
        editor.putString("Saving_settings", json);
        editor.apply();
    }


}
