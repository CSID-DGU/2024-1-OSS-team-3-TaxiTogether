package com.taxitogether.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.ViewGroup;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class screen6_2 extends AppCompatActivity {
    private MapView mapView;
    private RelativeLayout mapViewContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen6_2);

        initMapView();

        /*
        // 권한ID를 가져옵니다
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);

        int permission2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int permission3 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        // 권한이 열려있는지 확인
        if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED || permission3 == PackageManager.PERMISSION_DENIED) {
            // 마쉬멜로우 이상버전부터 권한을 물어본다
            if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 권한 체크(READ_PHONE_STATE의 requestCode를 1000으로 세팅
                requestPermissions(
                        new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1000);
            }
            return;
        }
         */
    }

    //지도 생성
    private void initMapView(){
        mapView = new MapView(this);
        mapViewContainer = findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
    }

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

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        // READ_PHONE_STATE의 권한 체크 결과를 불러온다
        super.onRequestPermissionsResult(requestCode, permissions, grandResults);
        if (requestCode == 1000) {
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            // 권한 체크에 동의를 하지 않으면 안드로이드 종료
            if (check_result == false) {
                finish();
            }
        }
    }

     */

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
