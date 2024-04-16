package com.taxitogether.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import net.daum.mf.map.api.*;

// 키 해시 추출용
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.widget.RelativeLayout;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private RelativeLayout mapViewContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initMapView(); // 지도 생성

        //handler(); // 뒷 부분 테스트 용
        Intent intent = new Intent(getApplicationContext(), screen2.class); //2번 스크린으로 화면 전환
        startActivity(intent);
        finish();

        //getHashKey();
    }
    /*
    private void initMapView(){
        mapView = new MapView(this);
        mapViewContainer = findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
    }
    //재시작 시 다시 지도 초기화를 위함
    @Override
    protected void onRestart() {
        super.onRestart();

        // 액티비티 재시작 시
        // MapView가 포함되어 있지 않다면 추가
        if (mapViewContainer != null && mapView != null && mapViewContainer.indexOfChild(mapView) == -1) {
            try {
                // 다시 맵뷰 초기화 및 추가
                initMapView();
            } catch (RuntimeException re) {
                Log.e("MainActivity", "Error while restarting activity: " + re.getMessage());
            }
        }
    }

    //카카오맵 리소스 제거(다른 액티비티에서 사용하려면 지워야 함)
    @Override
    public void finish(){
        ((ViewGroup) findViewById(R.id.map_view)).removeView(mapView);
        super.finish();
    }

    private void handler(){
        Handler handler = new Handler(); // 뒷부분 테스트 위함
        /*
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), screen6_2.class); //화면 전환
                startActivity(intent);
                finish();
            }
        }, 3000); //딜레이 타임 조절


        Intent intent = new Intent(getApplicationContext(), screen2.class); //화면 전환
        startActivity(intent);
        finish();
    }
*/
/*
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }

    }


 */





}