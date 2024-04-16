package com.taxitogether.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;

public class screen2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen2);
    }

    public void button1(View v){
        Intent intent = new Intent(getApplicationContext(), screen4.class);
        // (전역 변수) 탑승 인원 설정
        ( (ValueApplication) getApplication() ).set_num(2);
        startActivity((intent));
        finish();
    }

    public void button2(View v){
        Intent intent = new Intent(getApplicationContext(), screen4.class);
        // (전역 변수) 탑승 인원 설정
        ( (ValueApplication) getApplication() ).set_num(3);
        startActivity((intent));
        finish();
    }

    public void button3(View v){
        Intent intent = new Intent(getApplicationContext(), screen4.class);
        // (전역 변수) 탑승 인원 설정
        ( (ValueApplication) getApplication() ).set_num(4);
        startActivity((intent));
        finish();
    }
}