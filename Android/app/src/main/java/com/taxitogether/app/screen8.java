package com.taxitogether.app;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;

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

        // 승객이 혼자 탑승했을 경우 지불했을 금액과 걸린 시간
        int past_cost=1; int past_time=1;
        // 백엔드로부터 얻어낼 본인이 낼 금액과 걸린 시간(시간은 api 콜)
        int cost=1; int time=1;
        // 승객이 얼마를 이득봤는지와 시간은 얼마나 더 걸렸는지
        int profit = past_cost - cost; //
        int more_time = time - past_time;
        // 얼마나 이득봤는지
        float time_rate = time/past_time;
        float cost_rate = cost/past_cost;
        float rate = Math.round(time_rate * cost_rate);
        // 전역 변수(몇 명이 타는지)
        int num = ( (ValueApplication) getApplication() ).get_num();

        TextView tv = findViewById(R.id.textView1);
        tv.setText(more_time+"분정도 더 걸리지만...\n"+profit+"원이나\n아낄 수 있어요!");
        //linearLayouts.addView(tv);

        TextView tv2 = findViewById(R.id.textView2);
        tv2.setText("혼자 타면 "+past_time+"분 걸리고 "+past_cost+"원을 내야해요\n"+rate+"%를 이득 본 셈이에요!");
        //linearLayouts.addView(tv2);

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
