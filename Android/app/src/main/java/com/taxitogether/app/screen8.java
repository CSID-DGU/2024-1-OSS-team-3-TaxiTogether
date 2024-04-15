package com.taxitogether.app;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class screen8 extends AppCompatActivity {

    FrameLayout[] frameLayouts = new FrameLayout[4];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen8);

        // 프레임 레이아웃 배열 초기화
        frameLayouts[0] = findViewById(R.id.screen8_frame1);
        frameLayouts[1] = findViewById(R.id.screen8_frame2);
        frameLayouts[2] = findViewById(R.id.screen8_frame3);
        frameLayouts[3] = findViewById(R.id.screen8_frame4);

        // 백엔드로부터 받을 각 승객들이 내야할 금액
        int[] amount= new int[4];
        // 각 승객들이 내야할 금액을 설정
        // 예시로 1000원씩 내야한다고 설정
        for (int i = 0; i < amount.length; i++) {
            amount[i] = 1000;
        }
        // 전역 변수(몇 명이 타는지)
        int num = ( (ValueApplication) getApplication() ).get_num();

        for(int i=1; i<=num; i++){
            TextView tv = new TextView(this);
            tv.setText(i+"번째 사람이 낼 돈은 " + amount[i-1]+"원이에요");
            tv.setTextColor(Color.BLACK);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(30);
            tv.setTypeface(null, Typeface.BOLD);
            frameLayouts[i-1].addView(tv);
        }
        for(int i=num; i<4; i++){
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.money);
            frameLayouts[i].addView(iv);
        }

    }


    public void button1(View v){
        finish();
    }
    public void button2(View v){
        Intent intent = new Intent(getApplicationContext(), screen1.class);
        startActivity((intent));
        finish();
    }
}
