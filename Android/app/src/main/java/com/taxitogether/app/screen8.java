package com.taxitogether.app;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;

import net.daum.mf.map.api.MapPoint;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class screen8 extends AppCompatActivity {

    LinearLayout linearLayouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen8);

        ImageView imageView = findViewById(R.id.money);
        Animation animIn = AnimationUtils.loadAnimation(this, R.anim.rotate_in);
        Animation animOut = AnimationUtils.loadAnimation(this, R.anim.rotate_out);

        animIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.startAnimation(animOut); // 첫 번째 애니메이션이 끝나면 두 번째 애니메이션 시작
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.startAnimation(animIn); // 첫 번째 애니메이션이 끝나면 두 번째 애니메이션 시작
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        imageView.startAnimation(animIn); // 애니메이션 시작

        int more_time= ((ValueApplication) getApplication()).get_more_time();
        int profit= ((ValueApplication) getApplication()).get_profit();
        int past_time= ((ValueApplication) getApplication()).get_past_time();
        int past_cost= ((ValueApplication) getApplication()).get_past_cost();
        int rate= ((ValueApplication) getApplication()).get_rate();

        TextView tv = findViewById(R.id.textView1);
        tv.setText(more_time + "분정도 더 걸리지만...\n" + profit + "원이나\n아낄 수 있어요!");

        TextView tv2 = findViewById(R.id.textView2);
        tv2.setText("혼자 타면 " + past_time + "분 걸리고 " + past_cost + "원을 내야해요\n" + rate + "%를 이득 본 셈이에요!");

    }



    private void button1(View v){
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

