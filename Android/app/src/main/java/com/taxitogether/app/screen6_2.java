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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.ViewGroup;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.disklrucache.DiskLruCache;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class screen6_2 extends AppCompatActivity {
    private MapView mapView;
    MapPoint start, end;
    ArrayList<MapPoint> way = new ArrayList<>();
    String apiKey = "af3a07081f830adca6b60768135b5e54";
    private RelativeLayout mapViewContainer;

    MapPolyline polyline = new MapPolyline();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen6_2);

        initMapView();


    }

    //지도 생성
    private void initMapView() {
        mapView = new MapView(this);
        mapViewContainer = findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        if (mapViewContainer == null) {
            Log.e("MapViewError", "mapViewContainer is null");
            return;
        }
        ArrayList<MapPoint> waypoints = ((ValueApplication) getApplication()).get_destinations();
        if (waypoints == null || waypoints.isEmpty()) {
            Log.e("MapViewError", "waypoints is null or empty");
            return;
        }
        MapPolyline polyline = new MapPolyline();
        polyline.setTag(1000);
        polyline.setLineColor(Color.argb(255, 255, 0, 0));
        double x = 0, y = 0;
        x += ((ValueApplication) getApplication()).get_start_latitude();
        y += ((ValueApplication) getApplication()).get_start_longitude();

        MapPOIItem wayMarker = new MapPOIItem();
        wayMarker.setItemName("");
        wayMarker.setTag(1);
        wayMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        wayMarker.setSelectedMarkerType(MapPOIItem.MarkerType.BluePin);

        for (MapPoint point : waypoints) {
            wayMarker.setMapPoint(point);
            mapView.addPOIItem(wayMarker);
            x += point.getMapPointGeoCoord().latitude;
            y += point.getMapPointGeoCoord().longitude;
        }

        x /= (waypoints.size() + 1);
        y /= (waypoints.size() + 1);
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(x, y), 6, true);

        MapPOIItem startMarker = new MapPOIItem();
        startMarker.setItemName("출발지");
        startMarker.setTag(1);
        startMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(((ValueApplication) getApplication()).get_start_latitude(), ((ValueApplication) getApplication()).get_start_longitude()));
        startMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        startMarker.setSelectedMarkerType(MapPOIItem.MarkerType.BluePin);



        MapPOIItem endMarker = new MapPOIItem();
        endMarker.setItemName("도착지");
        endMarker.setTag(2);
        endMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(((ValueApplication) getApplication()).get_end_latitude(), ((ValueApplication) getApplication()).get_end_longitude()));
        endMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        endMarker.setSelectedMarkerType(MapPOIItem.MarkerType.BluePin);

        mapView.addPOIItem(startMarker);
        mapView.addPOIItem(endMarker);
        new GetRouteTask().execute();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mapView.removeAllPolylines();
        mapView.removeAllPOIItems();
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
    public void finish() {

        mapView.removePolyline(polyline);
        mapViewContainer.removeAllViews();
        ((ViewGroup) findViewById(R.id.map_view)).removeView(mapView);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 폴리라인 제거
        mapView.removePolyline(polyline);
        mapViewContainer.removeAllViews(); // 지도 컨테이너도 제거
    }


    public void button1(View v) {
        Intent intent = new Intent(getApplicationContext(), screen4.class);
        startActivity((intent));
        finish();
    }

    public void button2(View v) {
        Intent intent = new Intent(getApplicationContext(), screen7.class);
        startActivity((intent));
    }

    public void button3(View v) {
        Intent intent = new Intent(getApplicationContext(), screen4.class);
        startActivity((intent));
        finish();
    }

    private class GetRouteTask extends AsyncTask<MapPoint, Void, ArrayList<MapPoint>> {
        @Override
        protected ArrayList<MapPoint> doInBackground(MapPoint... points) {
            ArrayList<MapPoint> waypoints = ((ValueApplication) getApplication()).get_destinations();
            MapPoint m1 = MapPoint.mapPointWithGeoCoord(((ValueApplication) getApplication()).get_start_latitude(), ((ValueApplication) getApplication()).get_start_longitude());
            MapPoint m2 = MapPoint.mapPointWithGeoCoord(((ValueApplication) getApplication()).get_end_latitude(), ((ValueApplication) getApplication()).get_end_longitude());
            String urlString;
            ArrayList<MapPoint> routePoints = new ArrayList<>();
            try {
                if (waypoints.size() == 3) { // 경유지까지 설정
                    urlString = String.format("https://apis-navi.kakaomobility.com/v1/directions?origin=%s,%s&destination=%s,%s&waypoints=%s,%s|%s,%s",
                            URLEncoder.encode(String.valueOf(m1.getMapPointGeoCoord().longitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(m1.getMapPointGeoCoord().latitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(waypoints.get(2).getMapPointGeoCoord().longitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(waypoints.get(2).getMapPointGeoCoord().latitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(waypoints.get(0).getMapPointGeoCoord().longitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(waypoints.get(0).getMapPointGeoCoord().latitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(waypoints.get(1).getMapPointGeoCoord().longitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(waypoints.get(1).getMapPointGeoCoord().latitude), "UTF-8"));
                } else if (waypoints.size() == 4) {
                    urlString = String.format("https://apis-navi.kakaomobility.com/v1/directions?origin=%s,%s&destination=%s,%s&waypoints=%s,%s|%s,%s|%s,%s",
                            URLEncoder.encode(String.valueOf(m1.getMapPointGeoCoord().longitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(m1.getMapPointGeoCoord().latitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(waypoints.get(3).getMapPointGeoCoord().longitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(waypoints.get(3).getMapPointGeoCoord().latitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(waypoints.get(0).getMapPointGeoCoord().longitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(waypoints.get(0).getMapPointGeoCoord().latitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(waypoints.get(1).getMapPointGeoCoord().longitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(waypoints.get(1).getMapPointGeoCoord().latitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(waypoints.get(2).getMapPointGeoCoord().longitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(waypoints.get(2).getMapPointGeoCoord().latitude), "UTF-8"));
                } else if (waypoints.size() == 2) {
                    urlString = String.format("https://apis-navi.kakaomobility.com/v1/directions?origin=%s,%s&destination=%s,%s&waypoints=%s,%s",
                            URLEncoder.encode(String.valueOf(m1.getMapPointGeoCoord().longitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(m1.getMapPointGeoCoord().latitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(waypoints.get(1).getMapPointGeoCoord().longitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(waypoints.get(1).getMapPointGeoCoord().latitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(waypoints.get(0).getMapPointGeoCoord().longitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(waypoints.get(0).getMapPointGeoCoord().latitude), "UTF-8"));
                } else {
                    urlString = String.format("https://apis-navi.kakaomobility.com/v1/directions?origin=%s,%s&destination=%s,%s",
                            URLEncoder.encode(String.valueOf(m1.getMapPointGeoCoord().longitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(m1.getMapPointGeoCoord().latitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(m2.getMapPointGeoCoord().longitude), "UTF-8"),
                            URLEncoder.encode(String.valueOf(m2.getMapPointGeoCoord().latitude), "UTF-8"));
                }

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "KakaoAK " + apiKey);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray routes = jsonObject.getJSONArray("routes");
                    if (routes.length() > 0) {
                        JSONArray sections = routes.getJSONObject(0).getJSONArray("sections");
                        for (int i = 0; i < sections.length(); i++) {
                            JSONArray roads = sections.getJSONObject(i).getJSONArray("roads");
                            for (int j = 0; j < roads.length(); j++) {
                                JSONArray vertexes = roads.getJSONObject(j).getJSONArray("vertexes");
                                for (int k = 0; k < vertexes.length(); k += 2) {
                                    double longitude = vertexes.getDouble(k);
                                    double latitude = vertexes.getDouble(k + 1);
                                    routePoints.add(MapPoint.mapPointWithGeoCoord(latitude, longitude));
                                }
                            }
                        }
                    }
                } else {
                    Log.e("GetRouteTask", "Error fetching route: HTTP " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return routePoints;
        }

        @Override
        protected void onPostExecute(ArrayList<MapPoint> routePoints) {
            if (!routePoints.isEmpty()) {
                polyline.setTag(1000);
                polyline.setLineColor(Color.argb(255, 255, 0, 0)); // 빨간색 라인

                for (MapPoint point : routePoints) {
                    polyline.addPoint(point);
                }

                mapView.addPolyline(polyline);
                mapView.fitMapViewAreaToShowAllPolylines();
            }
        }


    }
}
