package com.taxitogether.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
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
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class screen4 extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener {
    private MapView mapView;
    private RelativeLayout mapViewContainer;
    private MapPOIItem marker_start;
    private MapPOIItem marker_end;

    private boolean start_check=false;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private GoogleApiClient googleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Google API 클라이언트 생성
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

        setContentView(R.layout.screen4);
        initMapView();
        checkLocationSettings();
    }

    // gps가 켜져있는지 체크하고 현 위치를 추적하거나 하지 않음
    private void checkLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        // GPS가 이미 켜져 있다면 코드 실행
                        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((com.google.android.gms.common.api.ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    // 사용자에게 대화 상자를 통해 GPS를 켜도록 요청
                                    Status status = ((com.google.android.gms.common.api.ResolvableApiException) e).getStatus();
                                    status.startResolutionForResult(screen4.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    // GPS 설정 변경 요청을 보낼 수 없음
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // 위치 설정 변경 불가
                                break;
                        }
                    }
                });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                // 사용자가 GPS를 켬
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            } else {
                // 사용자가 GPS를 켜는 것을 거부함
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    //지도 생성
    private void initMapView(){
        mapView = new MapView(this);
        mapViewContainer = findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);

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

    public void find_current_location(View v){
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
    }

    public void button1(View v){
        Intent intent = new Intent(getApplicationContext(), screen2.class);
        startActivity((intent));
        finish();
    }
    public void button2(View v){
        Intent intent = new Intent(getApplicationContext(), screen6_2.class);
        startActivity((intent));
        finish();
    }
    public void button3(View v){
        Intent intent = new Intent(getApplicationContext(), screen6_1.class);
        startActivity((intent));
        finish();
    }
    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        // 현재 위치 얻어내야 함

        // 터치한 위치의 위도 경도를 얻어내고
        MapPoint.GeoCoordinate geoCoordinate = mapPoint.getMapPointGeoCoord();

        if(!start_check) {
            updateMarker_start(geoCoordinate.latitude, geoCoordinate.longitude);
        }
        else{
            updateMarker_end(geoCoordinate.latitude, geoCoordinate.longitude);
        }
    }

    // 출발지 마커를 띄우는 메서드
    private void updateMarker_start(double latitude, double longitude){
        if(marker_start!=null){
            mapView.removePOIItem(marker_start);
        } // 시작 마커가 존재할 경우 제거 후 재생성

        MapPoint Marker_point = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        // 마커를 출력한다
        marker_start = new MapPOIItem();
        marker_start.setItemName("");
        marker_start.setShowCalloutBalloonOnTouch(false);
        marker_start.setTag(0);
        marker_start.setMapPoint(Marker_point);
        marker_start.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker_start.setSelectedMarkerType(MapPOIItem.MarkerType.BluePin);
        mapView.addPOIItem(marker_start);

        showCustomDialog_start(latitude, longitude);
    }
    
    // 목적지 마커를 띄우는 메서드
    private void updateMarker_end(double latitude, double longitude){
        if(marker_end!=null){
            mapView.removePOIItem(marker_end);
        } // 시작 마커가 존재할 경우 제거 후 재생성

        MapPoint Marker_point = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        // 마커를 출력한다
        marker_end = new MapPOIItem();
        marker_end.setItemName("");
        marker_end.setShowCalloutBalloonOnTouch(false);
        marker_end.setTag(0);
        marker_end.setMapPoint(Marker_point);
        marker_end.setMarkerType(MapPOIItem.MarkerType.RedPin);
        marker_end.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(marker_end);

        showCustomDialog_end(latitude, longitude);
    }
    
    // 출발지 선택 확인 창을 띄우는 메서드
    private void showCustomDialog_start(final double latitude, final double longitude){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_start_coordinate);
        dialog.setTitle("출발지 설정");

        Button buttonYes = (Button) dialog.findViewById(R.id.button_yes);
        Button buttonNo = (Button) dialog.findViewById(R.id.button_no);

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStartLocation(latitude, longitude);
                dialog.dismiss();
            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showCustomDialog_end(final double latitude, final double longitude){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_end_coordinate);
        dialog.setTitle("출발지 설정");

        Button buttonYes = (Button) dialog.findViewById(R.id.button_yes);
        Button buttonNo = (Button) dialog.findViewById(R.id.button_no);

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEndLocation(latitude, longitude);
                dialog.dismiss();
            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setStartLocation(double latitude, double longitude){
        ((ValueApplication) getApplication()).set_start_latitude(latitude);
        ((ValueApplication) getApplication()).set_start_longitude(longitude);
        start_check = true;
    }
    private void setEndLocation(double latitude, double longitude){
        ((ValueApplication) getApplication()).set_end_latitude(latitude);
        ((ValueApplication) getApplication()).set_end_longitude(longitude);
        if(start_check) {
            Intent intent = new Intent(getApplicationContext(), screen5.class);
            startActivity((intent));
            finish();
        }
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

}
