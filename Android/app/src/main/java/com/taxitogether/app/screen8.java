package com.taxitogether.app;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class screen8 extends AppCompatActivity {

    LinearLayout linearLayouts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen8);


        // 승객이 혼자 탑승했을 경우 지불했을 금액과 걸린 시간
        int past_cost=0; int past_time=0;
        // 백엔드로부터 얻어낼 본인이 낼 금액과 걸린 시간(시간은 api 콜)
        int cost=0; int time=0;
        // 승객이 얼마를 이득봤는지와 시간은 얼마나 더 걸렸는지
        int profit = past_cost - cost; //
        int more_time = time - past_time;
        // 전역 변수(몇 명이 타는지)
        int num = ( (ValueApplication) getApplication() ).get_num();

        TextView tv = new TextView(this);
        tv.setText(more_time+"분만큼 더 걸렸지만...");
        tv.setTextColor(Color.BLACK);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(30);
        tv.setTypeface(null, Typeface.BOLD);
        linearLayouts.addView(tv);

        TextView tv2 = new TextView(this);
        tv2.setText(profit+"원을 아꼈어요!");
        tv2.setTextColor(Color.BLACK);
        tv2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv2.setGravity(Gravity.CENTER);
        tv2.setTextSize(30);
        tv2.setTypeface(null, Typeface.BOLD);
        linearLayouts.addView(tv2);

    }


    public void button1(View v){
        finish();
    }
    public void button2(View v){
        Intent intent = new Intent(getApplicationContext(), screen2.class);
        startActivity((intent));
        finish();
    }

    // 카카오 택시가 있을 경우 실행하고 아닐 경우 실행하지 않는다.
    public void button3(View v){
        final String kakaoPackage = "com.kakao.taxi";
        final Intent intentKakao = getPackageManager().getLaunchIntentForPackage(kakaoPackage);
        try {
            startActivity(intentKakao); // 라인 앱을 실행해본다.
        } catch (Exception e) {  // 만약 실행이 안된다면 (앱이 없다면)
            Intent intentPlayStore = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + kakaoPackage)); // 설치 링크를 인텐트에 담아
            startActivity(intentPlayStore); // 플레이스토어로 이동
        }


    }
}
