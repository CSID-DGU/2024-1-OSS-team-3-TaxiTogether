package com.example.myapplication;

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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private RelativeLayout mapViewContainer;
     // 여기다가 좌표 넣으면 됨
    MapPoint[] m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("start", "onCreate 시작");

        initializeMapPoints();

        try {
            initMapView(); // 지도 생성
        } catch (Exception e) {
            Log.e("MapView", "initMapView 실패", e);
        }
        //getHashKey();



    }
    private void initializeMapPoints() {
        m = new MapPoint[]{

                MapPoint.mapPointWithGeoCoord(37.582299, 126.950291), // 무악재
                MapPoint.mapPointWithGeoCoord(37.575762, 126.97353), // 경복궁
                MapPoint.mapPointWithGeoCoord(37.516334, 127.020114), // 신사
                MapPoint.mapPointWithGeoCoord(37.55434, 127.010655), // 약수
                MapPoint.mapPointWithGeoCoord(37.528105, 126.917874), // 국회의사당
                MapPoint.mapPointWithGeoCoord(37.559973, 126.963672), // 충정로
                MapPoint.mapPointWithGeoCoord(37.57142, 127.009745), // 동대문
                MapPoint.mapPointWithGeoCoord(37.479252, 126.854876), // 광명사거리
                MapPoint.mapPointWithGeoCoord(37.619001, 126.921008), // 연신내
                MapPoint.mapPointWithGeoCoord(37.576646, 126.900984), // 디지털미디어시티
                MapPoint.mapPointWithGeoCoord(37.559052, 127.005602), // 동대입구
                MapPoint.mapPointWithGeoCoord(37.559973, 126.963672), // 충정로
                MapPoint.mapPointWithGeoCoord(37.549463, 126.913739), // 합정
                MapPoint.mapPointWithGeoCoord(37.541021, 126.9713), // 남영
                MapPoint.mapPointWithGeoCoord(37.562434, 126.801058), // 김포공항
                MapPoint.mapPointWithGeoCoord(37.560183, 126.825448), // 마곡
                MapPoint.mapPointWithGeoCoord(37.517122, 126.917169), // 신길
                MapPoint.mapPointWithGeoCoord(37.548014, 127.074658), // 어린이대공원
                MapPoint.mapPointWithGeoCoord(37.508725, 126.891295), // 신도림
                MapPoint.mapPointWithGeoCoord(37.524496, 126.875181), // 오목교
                MapPoint.mapPointWithGeoCoord(37.594917, 127.076116), // 중랑
                MapPoint.mapPointWithGeoCoord(37.653166, 127.047731), // 창동
                MapPoint.mapPointWithGeoCoord(37.561533, 127.037732), // 왕십리
                MapPoint.mapPointWithGeoCoord(37.540685, 127.017965), // 옥수
                MapPoint.mapPointWithGeoCoord(37.571026, 126.976669), // 광화문
                MapPoint.mapPointWithGeoCoord(37.481426, 126.997596), // 방배
                MapPoint.mapPointWithGeoCoord(37.503415, 126.995925), // 신반포
                MapPoint.mapPointWithGeoCoord(37.558514, 126.978246), // 회현
                MapPoint.mapPointWithGeoCoord(37.571462, 126.735637), // 계양
                MapPoint.mapPointWithGeoCoord(37.553703, 126.745077), // 박촌
                MapPoint.mapPointWithGeoCoord(37.510997, 127.073642), // 종합운동장
                MapPoint.mapPointWithGeoCoord(37.612314, 126.843223), // 강매
                MapPoint.mapPointWithGeoCoord(37.619001, 126.921008), // 연신내
                MapPoint.mapPointWithGeoCoord(37.576477, 126.985443), // 안국
                MapPoint.mapPointWithGeoCoord(37.694023, 126.761086), // 탄현
                MapPoint.mapPointWithGeoCoord(37.539261, 126.961351), // 효창공원앞
                MapPoint.mapPointWithGeoCoord(37.558514, 126.978246), // 회현
                MapPoint.mapPointWithGeoCoord(37.575762, 126.97353), // 경복궁
                MapPoint.mapPointWithGeoCoord(37.618808, 126.820783), // 능곡
                MapPoint.mapPointWithGeoCoord(37.653324, 126.843041), // 원당
                MapPoint.mapPointWithGeoCoord(37.493105, 127.14415), // 거여
                MapPoint.mapPointWithGeoCoord(37.55137, 127.143999), // 명일
                MapPoint.mapPointWithGeoCoord(37.555004, 127.154151), // 고덕
                MapPoint.mapPointWithGeoCoord(37.516078, 127.130848), // 올림픽공원
                MapPoint.mapPointWithGeoCoord(37.534488, 126.994302), // 이태원
                MapPoint.mapPointWithGeoCoord(37.514219, 126.942454), // 노량진
                MapPoint.mapPointWithGeoCoord(37.576646, 126.900984), // 디지털미디어시티
                MapPoint.mapPointWithGeoCoord(37.612102, 126.834146), // 행신
                MapPoint.mapPointWithGeoCoord(37.582336, 127.001844), // 혜화
                MapPoint.mapPointWithGeoCoord(37.519365, 127.05335), // 청담
                MapPoint.mapPointWithGeoCoord(37.648048, 126.913951), // 지축
                MapPoint.mapPointWithGeoCoord(37.571026, 126.976669), // 광화문
                MapPoint.mapPointWithGeoCoord(37.5661, 127.042973), // 마장
                MapPoint.mapPointWithGeoCoord(37.527072, 127.028461), // 압구정
                MapPoint.mapPointWithGeoCoord(37.613292, 127.030053), // 미아사거리
                MapPoint.mapPointWithGeoCoord(37.638052, 127.025732), // 수유
                MapPoint.mapPointWithGeoCoord(37.527381, 127.040534), // 압구정로데오
                MapPoint.mapPointWithGeoCoord(37.578103, 127.034893), // 제기동
                MapPoint.mapPointWithGeoCoord(37.603392, 127.143869), // 구리
                MapPoint.mapPointWithGeoCoord(37.484147, 127.034631), // 양재
                MapPoint.mapPointWithGeoCoord(37.487371, 127.10188), // 수서
                MapPoint.mapPointWithGeoCoord(37.484147, 127.034631), // 양재
                MapPoint.mapPointWithGeoCoord(37.545477, 127.142853), // 굽은다리
                MapPoint.mapPointWithGeoCoord(37.500622, 127.036456), // 역삼


        };
    }
    private void initMapView(){
        Log.d("MapView", "중간 확인");
        try {
            mapView = new MapView(this);
            Log.d("MapView", "initMapView: MapView 객체 생성 성공");
        } catch (Exception e) {
            Log.e("MapView", "MapView 객체 생성 실패", e);
            throw e;
        }
        mapViewContainer = findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        if (mapViewContainer == null) {
            Log.e("MapViewError", "mapViewContainer is null");
            return;
        }
        int x=0; int y=0;
        MapPOIItem wayMarker = new MapPOIItem();
        wayMarker.setItemName("");
        wayMarker.setTag(1);
        wayMarker.setMarkerType(MapPOIItem.MarkerType.RedPin);
        wayMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        for(MapPoint point : m){
            wayMarker.setMapPoint(point);
            mapView.addPOIItem(wayMarker);
        }


        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.5435, 126.9866), 6, true);
    }

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








}