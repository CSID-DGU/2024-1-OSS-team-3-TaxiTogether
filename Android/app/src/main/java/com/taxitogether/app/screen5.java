package com.taxitogether.app;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.widget.ProgressBar;

import net.daum.mf.map.api.MapPoint;

import java.util.ArrayList;
import java.util.Random;


// 10개의 목적지 집합을 생성하고 이를 백엔드에 제공하는 java 코드
public class screen5 extends AppCompatActivity {
    private Handler handler = new Handler();
    private int num_of_endpoints=10; // 목적지 집합 개수
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen5);
        MapPoint start, end;
        // 시작점과 목적지 좌표 반환
        start = MapPoint.mapPointWithGeoCoord(((ValueApplication) getApplication()).get_start_latitude(), ((ValueApplication) getApplication()).get_start_longitude());
        end = MapPoint.mapPointWithGeoCoord(((ValueApplication) getApplication()).get_end_latitude(), ((ValueApplication) getApplication()).get_end_longitude());
        // 거리 반환
        int r = (int)calculateDistance(start, end);
        // 출발점과 목적지의 각도 계산
        double angle = calculateAngleInRadians(start, end);
        ArrayList<MapPoint> end_points = new ArrayList<>();
        for(int i=0; i<num_of_endpoints; i++){
            MapPoint randompoint;
            if (r<2000) {
                randompoint = getRandomLocation(end, r * 3, angle);
            }
            else{
                randompoint = getRandomLocation(end, r * 2, angle);
            }
            end_points.add(randompoint);
            Log.d("RandomLocation", "Location " + (i+1) + ": " + randompoint.getMapPointGeoCoord().latitude + ", " + randompoint.getMapPointGeoCoord().longitude);
        }

        progressBar = findViewById(R.id.progressBar);
        startHandler();
    }

    private MapPoint getRandomLocation(MapPoint c, int radius, double angle) {
        double d2r = Math.PI / 180;
        double r2d = 180 / Math.PI;
        double earth_rad = 6378000f; //지구 반지름 근사값

        double r = new Random().nextInt(radius) + new Random().nextDouble();
        double rlat = (r / earth_rad) * r2d;
        double rlng = rlat / Math.cos(c.getMapPointGeoCoord().latitude * d2r);

        double theta = Math.PI/2 * (new Random().nextInt(2) + new Random().nextDouble());
        theta = theta + angle;
        double y = c.getMapPointGeoCoord().longitude + (rlng * Math.cos(theta));
        double x = c.getMapPointGeoCoord().latitude + (rlat * Math.sin(theta));
        return MapPoint.mapPointWithGeoCoord(x, y);
    }

    private static double calculateAngleInRadians(MapPoint point1, MapPoint point2) {
        double lat1 = point1.getMapPointGeoCoord().latitude;
        double lon1 = point1.getMapPointGeoCoord().longitude;
        double lat2 = point2.getMapPointGeoCoord().latitude;
        double lon2 = point2.getMapPointGeoCoord().longitude;

        // 위도와 경도를 라디안으로 변환
        double radLat1 = Math.toRadians(lat1);
        double radLon1 = Math.toRadians(lon1);
        double radLat2 = Math.toRadians(lat2);
        double radLon2 = Math.toRadians(lon2);

        // 두 점 사이의 경도 차
        double deltaLon = radLon2 - radLon1;

        // X축(동서 방향)을 기준으로 각도 계산
        double y = Math.sin(deltaLon) * Math.cos(radLat2);
        double x = Math.cos(radLat1) * Math.sin(radLat2) - Math.sin(radLat1) * Math.cos(radLat2) * Math.cos(deltaLon);

        // 아크탄젠트를 사용하여 라디안 각도 계산
        double angle = Math.atan2(y, x);

        return angle;
    }

    private static double calculateDistance(MapPoint point1, MapPoint point2) {
        double lat1 = point1.getMapPointGeoCoord().latitude;
        double lon1 = point1.getMapPointGeoCoord().longitude;
        double lat2 = point2.getMapPointGeoCoord().latitude;
        double lon2 = point2.getMapPointGeoCoord().longitude;

        // 지구 반지름 (미터 단위)
        double R = 6371000;

        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        // 하버사인 공식 계산
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(radLat1) * Math.cos(radLat2) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 최종 거리
        double distance = R * c;

        return distance;
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
                Intent intent = new Intent(getApplicationContext(), screen6_2.class); //화면 전환
                startActivity(intent);
                finish();
            }
        }, 3000); //딜레이 타임 조절
    }

    private void stopHandler() {
        handler.removeCallbacksAndMessages(null);
    }

    public void button1(View v){
        Intent intent = new Intent(getApplicationContext(), screen4.class);
        startActivity((intent));
        finish();
    }
}
