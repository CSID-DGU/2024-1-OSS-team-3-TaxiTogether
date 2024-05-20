package com.taxitogether.app;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.widget.ProgressBar;
public class screen7 extends AppCompatActivity {
    private Handler handler = new Handler();

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen7);

        progressBar = findViewById(R.id.progressBar);
        startHandler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopHandler();
    }

    private void startHandler() {
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100);
        progressAnimator.setDuration(3000); // 3초
        progressAnimator.start();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), screen8.class); //화면 전환
                startActivity(intent);
                finish();
            }
        }, 3000); //딜레이 타임 조절
    }

    private void stopHandler() {
        handler.removeCallbacksAndMessages(null);
    }

    public void button1(View v){
        Intent intent = new Intent(getApplicationContext(), screen6_2.class);
        startActivity((intent));
        finish();
    }
}
