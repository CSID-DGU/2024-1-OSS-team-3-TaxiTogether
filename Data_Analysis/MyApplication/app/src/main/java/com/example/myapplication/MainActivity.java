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
                MapPoint.mapPointWithGeoCoord(37.55315,126.97253),
                MapPoint.mapPointWithGeoCoord(37.56359,126.97541),
                MapPoint.mapPointWithGeoCoord(37.57020,126.98312),
                MapPoint.mapPointWithGeoCoord(37.57043,126.99210),
                MapPoint.mapPointWithGeoCoord(37.57097,127.00190),
                MapPoint.mapPointWithGeoCoord(37.57179,127.01138),
                MapPoint.mapPointWithGeoCoord(37.57327,127.01646),
                MapPoint.mapPointWithGeoCoord(37.57612,127.02471),
                MapPoint.mapPointWithGeoCoord(37.57812,127.03490),
                MapPoint.mapPointWithGeoCoord(37.58015,127.04506),
                MapPoint.mapPointWithGeoCoord(37.56600,126.98257),
                MapPoint.mapPointWithGeoCoord(37.56629,126.99177),
                MapPoint.mapPointWithGeoCoord(37.56661,126.99812),
                MapPoint.mapPointWithGeoCoord(37.56560,127.00911),
                MapPoint.mapPointWithGeoCoord(37.56568,127.01949),
                MapPoint.mapPointWithGeoCoord(37.56450,127.02887),
                MapPoint.mapPointWithGeoCoord(37.56116,127.03551),
                MapPoint.mapPointWithGeoCoord(37.55658,127.04350),
                MapPoint.mapPointWithGeoCoord(37.54718,127.04741),
                MapPoint.mapPointWithGeoCoord(37.54463,127.05598),
                MapPoint.mapPointWithGeoCoord(37.54041,127.06923),
                MapPoint.mapPointWithGeoCoord(37.53686,127.08502),
                MapPoint.mapPointWithGeoCoord(37.53516,127.09468),
                MapPoint.mapPointWithGeoCoord(37.52069,127.10384),
                MapPoint.mapPointWithGeoCoord(37.51331,127.10013),
                MapPoint.mapPointWithGeoCoord(37.52073,127.10374),
                MapPoint.mapPointWithGeoCoord(37.51101,127.07364),
                MapPoint.mapPointWithGeoCoord(37.50883,127.06320),
                MapPoint.mapPointWithGeoCoord(37.50426,127.04817),
                MapPoint.mapPointWithGeoCoord(37.50066,127.03643),
                MapPoint.mapPointWithGeoCoord(37.49796,127.02754),
                MapPoint.mapPointWithGeoCoord(37.49396,127.01463),
                MapPoint.mapPointWithGeoCoord(37.49191,127.00795),
                MapPoint.mapPointWithGeoCoord(37.48147,126.99763),
                MapPoint.mapPointWithGeoCoord(37.47654,126.98163),
                MapPoint.mapPointWithGeoCoord(37.47693,126.96378),
                MapPoint.mapPointWithGeoCoord(37.48123,126.95275),
                MapPoint.mapPointWithGeoCoord(37.48242,126.94190),
                MapPoint.mapPointWithGeoCoord(37.48422,126.92957),
                MapPoint.mapPointWithGeoCoord(37.48753,126.91328),
                MapPoint.mapPointWithGeoCoord(37.48501,126.90263),
                MapPoint.mapPointWithGeoCoord(37.49243,126.89529),
                MapPoint.mapPointWithGeoCoord(37.50882,126.89122),
                MapPoint.mapPointWithGeoCoord(37.51799,126.89477),
                MapPoint.mapPointWithGeoCoord(37.52577,126.89663),
                MapPoint.mapPointWithGeoCoord(37.53388,126.90201),
                MapPoint.mapPointWithGeoCoord(37.55003,126.91456),
                MapPoint.mapPointWithGeoCoord(37.55675,126.92364),
                MapPoint.mapPointWithGeoCoord(37.55515,126.93689),
                MapPoint.mapPointWithGeoCoord(37.55673,126.94590),
                MapPoint.mapPointWithGeoCoord(37.55741,126.95608),
                MapPoint.mapPointWithGeoCoord(37.55974,126.96446),
                MapPoint.mapPointWithGeoCoord(37.56353,126.97527),
                MapPoint.mapPointWithGeoCoord(37.57465,127.02516),
                MapPoint.mapPointWithGeoCoord(37.57401,127.03811),
                MapPoint.mapPointWithGeoCoord(37.56147,127.05635),
                MapPoint.mapPointWithGeoCoord(37.56641,126.97786),
                MapPoint.mapPointWithGeoCoord(37.51476,126.88259),
                MapPoint.mapPointWithGeoCoord(37.51219,126.86519),
                MapPoint.mapPointWithGeoCoord(37.52022,126.85285),
                MapPoint.mapPointWithGeoCoord(37.53181,126.84671),
                MapPoint.mapPointWithGeoCoord(37.64828,126.91255),
                MapPoint.mapPointWithGeoCoord(37.63661,126.91883),
                MapPoint.mapPointWithGeoCoord(37.61886,126.92086),
                MapPoint.mapPointWithGeoCoord(37.61055,126.92984),
                MapPoint.mapPointWithGeoCoord(37.60088,126.93576),
                MapPoint.mapPointWithGeoCoord(37.58885,126.94409),
                MapPoint.mapPointWithGeoCoord(37.58266,126.95013),
                MapPoint.mapPointWithGeoCoord(37.57453,126.95790),
                MapPoint.mapPointWithGeoCoord(37.57584,126.97358),
                MapPoint.mapPointWithGeoCoord(37.57656,126.98547),
                MapPoint.mapPointWithGeoCoord(37.57154,126.99124),
                MapPoint.mapPointWithGeoCoord(37.56630,126.99262),
                MapPoint.mapPointWithGeoCoord(37.56130,126.99547),
                MapPoint.mapPointWithGeoCoord(37.55816,127.00527),
                MapPoint.mapPointWithGeoCoord(37.55467,127.01063),
                MapPoint.mapPointWithGeoCoord(37.54827,127.01579),
                MapPoint.mapPointWithGeoCoord(37.54165,127.01730),
                MapPoint.mapPointWithGeoCoord(37.52617,127.02850),
                MapPoint.mapPointWithGeoCoord(37.51644,127.02025),
                MapPoint.mapPointWithGeoCoord(37.51299,127.01161),
                MapPoint.mapPointWithGeoCoord(37.50495,127.00492),
                MapPoint.mapPointWithGeoCoord(37.49306,127.01380),
                MapPoint.mapPointWithGeoCoord(37.48494,127.01629),
                MapPoint.mapPointWithGeoCoord(37.48466,127.03513),
                MapPoint.mapPointWithGeoCoord(37.48711,127.04691),
                MapPoint.mapPointWithGeoCoord(37.49113,127.05569),
                MapPoint.mapPointWithGeoCoord(37.49460,127.06345),
                MapPoint.mapPointWithGeoCoord(37.49676,127.07054),
                MapPoint.mapPointWithGeoCoord(37.49361,127.07953),
                MapPoint.mapPointWithGeoCoord(37.48389,127.08416),
                MapPoint.mapPointWithGeoCoord(37.48751,127.10132),
                MapPoint.mapPointWithGeoCoord(37.49237,127.11810),
                MapPoint.mapPointWithGeoCoord(37.49575,127.12420),
                MapPoint.mapPointWithGeoCoord(37.50229,127.12834),
                MapPoint.mapPointWithGeoCoord(37.66956,127.07840),
                MapPoint.mapPointWithGeoCoord(37.66058,127.07320),
                MapPoint.mapPointWithGeoCoord(37.65627,127.06318),
                MapPoint.mapPointWithGeoCoord(37.65299,127.04675),
                MapPoint.mapPointWithGeoCoord(37.64827,127.03438),
                MapPoint.mapPointWithGeoCoord(37.63713,127.02473),
                MapPoint.mapPointWithGeoCoord(37.62644,127.02615),
                MapPoint.mapPointWithGeoCoord(37.61328,127.03008),
                MapPoint.mapPointWithGeoCoord(37.60409,127.02535),
                MapPoint.mapPointWithGeoCoord(37.59278,127.01734),
                MapPoint.mapPointWithGeoCoord(37.58838,127.00675),
                MapPoint.mapPointWithGeoCoord(37.58212,127.00176),
                MapPoint.mapPointWithGeoCoord(37.57084,127.00940),
                MapPoint.mapPointWithGeoCoord(37.56508,127.00765),
                MapPoint.mapPointWithGeoCoord(37.56130,126.99547),
                MapPoint.mapPointWithGeoCoord(37.56106,126.98827),
                MapPoint.mapPointWithGeoCoord(37.55970,126.97957),
                MapPoint.mapPointWithGeoCoord(37.55317,126.97284),
                MapPoint.mapPointWithGeoCoord(37.54512,126.97195),
                MapPoint.mapPointWithGeoCoord(37.53506,126.97335),
                MapPoint.mapPointWithGeoCoord(37.52919,126.96858),
                MapPoint.mapPointWithGeoCoord(37.52253,126.97335),
                MapPoint.mapPointWithGeoCoord(37.50357,126.98017),
                MapPoint.mapPointWithGeoCoord(37.48752,126.98231),
                MapPoint.mapPointWithGeoCoord(37.47656,126.98175),
                MapPoint.mapPointWithGeoCoord(37.46434,126.98908),
                MapPoint.mapPointWithGeoCoord(37.57767,126.81282),
                MapPoint.mapPointWithGeoCoord(37.57246,126.80684),
                MapPoint.mapPointWithGeoCoord(37.56217,126.80127),
                MapPoint.mapPointWithGeoCoord(37.56141,126.81205),
                MapPoint.mapPointWithGeoCoord(37.56218,126.82693),
                MapPoint.mapPointWithGeoCoord(37.56218,126.82693),
                MapPoint.mapPointWithGeoCoord(37.54886,126.83633),
                MapPoint.mapPointWithGeoCoord(37.54159,126.84044),
                MapPoint.mapPointWithGeoCoord(37.53181,126.84671),
                MapPoint.mapPointWithGeoCoord(37.52500,126.85618),
                MapPoint.mapPointWithGeoCoord(37.52609,126.86430),
                MapPoint.mapPointWithGeoCoord(37.52456,126.87505),
                MapPoint.mapPointWithGeoCoord(37.52561,126.88618),
                MapPoint.mapPointWithGeoCoord(37.52421,126.89502),
                MapPoint.mapPointWithGeoCoord(37.52276,126.90514),
                MapPoint.mapPointWithGeoCoord(37.51763,126.91489),
                MapPoint.mapPointWithGeoCoord(37.52158,126.92432),
                MapPoint.mapPointWithGeoCoord(37.52715,126.93281),
                MapPoint.mapPointWithGeoCoord(37.53972,126.94604),
                MapPoint.mapPointWithGeoCoord(37.54401,126.95106),
                MapPoint.mapPointWithGeoCoord(37.55359,126.95673),
                MapPoint.mapPointWithGeoCoord(37.56006,126.96278),
                MapPoint.mapPointWithGeoCoord(37.56581,126.96664),
                MapPoint.mapPointWithGeoCoord(37.57055,126.97657),
                MapPoint.mapPointWithGeoCoord(37.57255,126.99045),
                MapPoint.mapPointWithGeoCoord(37.56658,126.99813),
                MapPoint.mapPointWithGeoCoord(37.56468,127.00533),
                MapPoint.mapPointWithGeoCoord(37.56024,127.01379),
                MapPoint.mapPointWithGeoCoord(37.55450,127.02040),
                MapPoint.mapPointWithGeoCoord(37.55730,127.02948),
                MapPoint.mapPointWithGeoCoord(37.56197,127.03726),
                MapPoint.mapPointWithGeoCoord(37.56607,127.04292),
                MapPoint.mapPointWithGeoCoord(37.56683,127.05266),
                MapPoint.mapPointWithGeoCoord(37.56144,127.06460),
                MapPoint.mapPointWithGeoCoord(37.55710,127.07956),
                MapPoint.mapPointWithGeoCoord(37.55201,127.08961),
                MapPoint.mapPointWithGeoCoord(37.54530,127.10348),
                MapPoint.mapPointWithGeoCoord(37.53857,127.12354),
                MapPoint.mapPointWithGeoCoord(37.53581,127.13249),
                MapPoint.mapPointWithGeoCoord(37.53802,127.14009),
                MapPoint.mapPointWithGeoCoord(37.54544,127.14284),
                MapPoint.mapPointWithGeoCoord(37.55132,127.14400),
                MapPoint.mapPointWithGeoCoord(37.55500,127.15421),
                MapPoint.mapPointWithGeoCoord(37.55671,127.16638),
                MapPoint.mapPointWithGeoCoord(37.52779,127.13622),
                MapPoint.mapPointWithGeoCoord(37.51622,127.13096),
                MapPoint.mapPointWithGeoCoord(37.50875,127.12605),
                MapPoint.mapPointWithGeoCoord(37.50223,127.12770),
                MapPoint.mapPointWithGeoCoord(37.49810,127.13482),
                MapPoint.mapPointWithGeoCoord(37.49321,127.14398),
                MapPoint.mapPointWithGeoCoord(37.49497,127.15278),
                MapPoint.mapPointWithGeoCoord(37.55752,127.17602),
                MapPoint.mapPointWithGeoCoord(37.56329,127.19295),
                MapPoint.mapPointWithGeoCoord(37.55220,127.20390),
                MapPoint.mapPointWithGeoCoord(37.54172,127.20690),
                MapPoint.mapPointWithGeoCoord(37.53973,127.22343),
                MapPoint.mapPointWithGeoCoord(37.59859,126.91558),
                MapPoint.mapPointWithGeoCoord(37.60605,126.92276),
                MapPoint.mapPointWithGeoCoord(37.61092,126.92950),
                MapPoint.mapPointWithGeoCoord(37.61841,126.93304),
                MapPoint.mapPointWithGeoCoord(37.61860,126.92052),
                MapPoint.mapPointWithGeoCoord(37.61122,126.91725),
                MapPoint.mapPointWithGeoCoord(37.59115,126.91361),
                MapPoint.mapPointWithGeoCoord(37.58399,126.90979),
                MapPoint.mapPointWithGeoCoord(37.57701,126.89864),
                MapPoint.mapPointWithGeoCoord(37.56944,126.89908),
                MapPoint.mapPointWithGeoCoord(37.56354,126.90333),
                MapPoint.mapPointWithGeoCoord(37.55603,126.91013),
                MapPoint.mapPointWithGeoCoord(37.54903,126.91355),
                MapPoint.mapPointWithGeoCoord(37.54770,126.92292),
                MapPoint.mapPointWithGeoCoord(37.54746,126.93197),
                MapPoint.mapPointWithGeoCoord(37.54773,126.94221),
                MapPoint.mapPointWithGeoCoord(37.54359,126.95166),
                MapPoint.mapPointWithGeoCoord(37.53928,126.96135),
                MapPoint.mapPointWithGeoCoord(37.53556,126.97400),
                MapPoint.mapPointWithGeoCoord(37.53469,126.98665),
                MapPoint.mapPointWithGeoCoord(37.53449,126.99437),
                MapPoint.mapPointWithGeoCoord(37.53956,127.00173),
                MapPoint.mapPointWithGeoCoord(37.54793,127.00695),
                MapPoint.mapPointWithGeoCoord(37.55409,127.01024),
                MapPoint.mapPointWithGeoCoord(37.56022,127.01378),
                MapPoint.mapPointWithGeoCoord(37.56617,127.01616),
                MapPoint.mapPointWithGeoCoord(37.57228,127.01567),
                MapPoint.mapPointWithGeoCoord(37.57977,127.01525),
                MapPoint.mapPointWithGeoCoord(37.58529,127.01938),
                MapPoint.mapPointWithGeoCoord(37.58626,127.02903),
                MapPoint.mapPointWithGeoCoord(37.59034,127.03626),
                MapPoint.mapPointWithGeoCoord(37.60192,127.04149),
                MapPoint.mapPointWithGeoCoord(37.60639,127.04851),
                MapPoint.mapPointWithGeoCoord(37.61052,127.05642),
                MapPoint.mapPointWithGeoCoord(37.61494,127.06592),
                MapPoint.mapPointWithGeoCoord(37.61732,127.07474),
                MapPoint.mapPointWithGeoCoord(37.61988,127.08411),
                MapPoint.mapPointWithGeoCoord(37.61729,127.09138),
                MapPoint.mapPointWithGeoCoord(37.61257,127.10433),
                MapPoint.mapPointWithGeoCoord(37.70015,127.05313),
                MapPoint.mapPointWithGeoCoord(37.68913,127.04655),
                MapPoint.mapPointWithGeoCoord(37.67780,127.05531),
                MapPoint.mapPointWithGeoCoord(37.66499,127.05770),
                MapPoint.mapPointWithGeoCoord(37.65448,127.06056),
                MapPoint.mapPointWithGeoCoord(37.64505,127.06408),
                MapPoint.mapPointWithGeoCoord(37.63636,127.06800),
                MapPoint.mapPointWithGeoCoord(37.62564,127.07297),
                MapPoint.mapPointWithGeoCoord(37.61732,127.07474),
                MapPoint.mapPointWithGeoCoord(37.61064,127.07772),
                MapPoint.mapPointWithGeoCoord(37.60260,127.07925),
                MapPoint.mapPointWithGeoCoord(37.59567,127.08571),
                MapPoint.mapPointWithGeoCoord(37.58867,127.08750),
                MapPoint.mapPointWithGeoCoord(37.58091,127.08850),
                MapPoint.mapPointWithGeoCoord(37.57375,127.08680),
                MapPoint.mapPointWithGeoCoord(37.56588,127.08429),
                MapPoint.mapPointWithGeoCoord(37.55715,127.07948),
                MapPoint.mapPointWithGeoCoord(37.54796,127.07465),
                MapPoint.mapPointWithGeoCoord(37.54088,127.07110),
                MapPoint.mapPointWithGeoCoord(37.53156,127.06671),
                MapPoint.mapPointWithGeoCoord(37.51910,127.05185),
                MapPoint.mapPointWithGeoCoord(37.51719,127.04122),
                MapPoint.mapPointWithGeoCoord(37.51426,127.03174),
                MapPoint.mapPointWithGeoCoord(37.51111,127.02139),
                MapPoint.mapPointWithGeoCoord(37.50817,127.01172),
                MapPoint.mapPointWithGeoCoord(37.50339,127.00506),
                MapPoint.mapPointWithGeoCoord(37.48764,126.99354),
                MapPoint.mapPointWithGeoCoord(37.48526,126.98177),
                MapPoint.mapPointWithGeoCoord(37.48469,126.97111),
                MapPoint.mapPointWithGeoCoord(37.49626,126.95365),
                MapPoint.mapPointWithGeoCoord(37.50279,126.94795),
                MapPoint.mapPointWithGeoCoord(37.50485,126.93903),
                MapPoint.mapPointWithGeoCoord(37.49972,126.92822),
                MapPoint.mapPointWithGeoCoord(37.49992,126.92011),
                MapPoint.mapPointWithGeoCoord(37.50011,126.90981),
                MapPoint.mapPointWithGeoCoord(37.49298,126.89700),
                MapPoint.mapPointWithGeoCoord(37.48618,126.88737),
                MapPoint.mapPointWithGeoCoord(37.48038,126.88270),
                MapPoint.mapPointWithGeoCoord(37.47616,126.86822),
                MapPoint.mapPointWithGeoCoord(37.47927,126.85485),
                MapPoint.mapPointWithGeoCoord(37.48670,126.83868),
                MapPoint.mapPointWithGeoCoord(37.49206,126.82329),
                MapPoint.mapPointWithGeoCoord(37.55013,127.12752),
                MapPoint.mapPointWithGeoCoord(37.53792,127.12318),
                MapPoint.mapPointWithGeoCoord(37.53035,127.12046),
                MapPoint.mapPointWithGeoCoord(37.51769,127.11274),
                MapPoint.mapPointWithGeoCoord(37.51465,127.10427),
                MapPoint.mapPointWithGeoCoord(37.50540,127.10700),
                MapPoint.mapPointWithGeoCoord(37.49978,127.11212),
                MapPoint.mapPointWithGeoCoord(37.49300,127.11828),
                MapPoint.mapPointWithGeoCoord(37.48593,127.12247),
                MapPoint.mapPointWithGeoCoord(37.47861,127.12623),
                MapPoint.mapPointWithGeoCoord(37.47102,127.12675),
                MapPoint.mapPointWithGeoCoord(37.46284,127.13905),
                MapPoint.mapPointWithGeoCoord(37.45689,127.14993),
                MapPoint.mapPointWithGeoCoord(37.45157,127.15985),
                MapPoint.mapPointWithGeoCoord(37.44506,127.15674),
                MapPoint.mapPointWithGeoCoord(37.44095,127.14759),
                MapPoint.mapPointWithGeoCoord(37.43758,127.14094),
                MapPoint.mapPointWithGeoCoord(37.43389,127.12992),

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