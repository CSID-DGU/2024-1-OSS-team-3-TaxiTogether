package com.taxitogether.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import net.daum.mf.map.api.MapView;

public class screen6_2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen6_2);
        MapView mapView = new MapView(this);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view6_2);
        mapViewContainer.addView(mapView);
    }

    public void button1(View v){
        Intent intent = new Intent(getApplicationContext(), screen4.class);
        startActivity((intent));
        finish();
    }
    public void button2(View v){
        Intent intent = new Intent(getApplicationContext(), screen7.class);
        startActivity((intent));
    }
    public void button3(View v){
        Intent intent = new Intent(getApplicationContext(), screen4.class);
        startActivity((intent));
        finish();
    }

}
