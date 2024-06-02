package com.taxitogether.app;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

import net.daum.mf.map.api.MapPoint;

import java.util.ArrayList;

public class ValueApplication extends Application {

    private int num_of_person;
    private double start_latitude, start_longitude;
    private double end_latitude, end_longitude;

    private int total_fare;

    private int my_fare;
    private int more_time, profit, past_time, past_cost, rate;

    private ArrayList<MapPoint> destinations = new ArrayList<>();
    private ArrayList<MapPoint> waypoints = new ArrayList<>();
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

    public void set_destinations(ArrayList<MapPoint> destinations){
        this.destinations = new ArrayList<>(destinations);
    }

    public ArrayList<MapPoint> get_destinations(){
        return new ArrayList<>(this.destinations);
    }

    public void set_waypoints(ArrayList<MapPoint> waypoints){
        this.waypoints = new ArrayList<>(waypoints);
    }

    public ArrayList<MapPoint> get_waypoints(){
        return new ArrayList<>(this.waypoints);
    }


    public int get_total_fare(){
        return total_fare;
    }
    public void set_total_fare(int num){
        this.total_fare = num;
    }

    public int get_my_fare(){
        return my_fare;
    }
    public void set_my_fare(int num){
        this.my_fare = num;
    }

    public int get_more_time(){
        return more_time;
    }
    public void set_more_time(int num){
        this.more_time = num;
    }
    public int get_profit(){
        return profit;
    }
    public void set_profit(int num){
        this.profit = num;
    }
    public int get_past_time(){
        return past_time;
    }
    public void set_past_time(int num){
        this.past_time = num;
    }
    public int get_past_cost(){
        return past_cost;
    }
    public void set_past_cost(int num){
        this.past_cost = num;
    }

    public int get_rate(){
        return rate;
    }
    public void set_rate(int num){
        this.rate = num;
    }


}
