package com.taxitogether.app;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

public class ValueApplication extends Application {

    private int num_of_person;
    @Override public void onCreate(){
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        num_of_person=2;
    }
    public int get_num(){
        return num_of_person;
    }
    public void set_num(int num){
        this.num_of_person = num;
    }

}
