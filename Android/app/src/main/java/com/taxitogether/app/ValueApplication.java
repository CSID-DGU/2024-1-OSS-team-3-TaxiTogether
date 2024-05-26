package com.taxitogether.app;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

public class ValueApplication extends Application {

    private int num_of_person;
    private double start_latitude, start_longitude;
    private double end_latitude, end_longitude;
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
    public double get_start_latitude(){
        return start_latitude;
    }
    public void set_start_latitude(double num){
        this.start_latitude = num;
    }

    public double get_start_longitude(){
        return start_longitude;
    }
    public void set_start_longitude(double num){
        this.start_longitude = num;
    }

    public double get_end_latitude(){
        return end_latitude;
    }
    public void set_end_latitude(double num){
        this.end_latitude = num;
    }

    public double get_end_longitude(){
        return end_longitude;
    }
    public void set_end_longitude(double num){
        this.end_longitude = num;
    }

}
