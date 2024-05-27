package com.taxitogether.app;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.widget.ProgressBar;

import net.daum.mf.map.api.MapPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;


// 10개의 목적지 집합을 생성하고 이를 백엔드에 제공하는 java 코드
public class screen5 extends AppCompatActivity {
    private Handler handler = new Handler(Looper.getMainLooper());
    private Handler handler2 = new Handler();
    private ProgressBar progressBar;
    private boolean is_route_valid= false;
    private boolean isResultReady = false;
    private String apiResult;
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
        ArrayList<MapPoint> destinations = new ArrayList<>();
        destinations.add(end); // 사용자의 목적지를 Queue에 삽입

        progressBar = findViewById(R.id.progressBar);
        startHandler();

        Runnable task = new Runnable() {
            public void run(){
                while(true){ // 끝없이 다른 사용자의 목적지가 Queue에 들어온다고 가정
                    MapPoint randompoint;
                    randompoint = getRandomLocation(end, r * 4);
                    destinations.add(randompoint);

                    new PostTask(start, destinations, destinations.size(), handler).execute();

                    // 결과가 준비될 때까지 대기
                    synchronized (screen5.this) {
                        while (!isResultReady) {
                            try {
                                screen5.this.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    // 결과 처리
                    if (apiResult != null) {
                        // 결과를 로그에 출력
                        Log.d("API Response", apiResult);
                        // JSON 응답을 파싱하여 is_route_valid 값을 설정합니다.
                        try {
                            JSONObject jsonResponse = new JSONObject(apiResult);
                            is_route_valid = jsonResponse.getBoolean("is_route_valid");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // 결과가 준비된 후 플래그를 리셋
                        isResultReady = false;
                    }

                    if(is_route_valid && destinations.size()==4){
                        Intent intent = new Intent(getApplicationContext(), screen6_2.class); //화면 전환
                        startActivity(intent);
                        finish();
                    }
                    else if(is_route_valid){
                        is_route_valid=false;
                    }
                    else{
                        destinations.remove(destinations.size()-1); // 만약 유효하지 않을 경우 제거
                    }
                    Log.d("RandomLocation", "Location: " + randompoint.getMapPointGeoCoord().latitude + ", " + randompoint.getMapPointGeoCoord().longitude);
                }
            }
        };
        new Thread(task).start();
    }

    private class PostTask extends AsyncTask<Void, Void, String> {
        private MapPoint start;
        private ArrayList<MapPoint> destinations;
        private int numPeople;
        private Handler handler;

        public PostTask(MapPoint start, ArrayList<MapPoint> destinations, int numPeople, Handler handler) {
            this.start = start;
            this.destinations = destinations;
            this.numPeople = numPeople;
            this.handler = handler;
        }
        @Override
        protected String doInBackground(Void... voids) {
            String urlString = "http://localhost:8000/calculate_fare";
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);

                JSONObject data = new JSONObject(); // 출발지를 json에 담는다
                data.put("start", new JSONArray(new double[]{start.getMapPointGeoCoord().latitude, start.getMapPointGeoCoord().longitude}));

                JSONArray destinationsArray = new JSONArray(); // 목적지 집합을 json에 담는다
                for(MapPoint point: destinations){
                    destinationsArray.put(new JSONArray(new double[]{point.getMapPointGeoCoord().latitude, point.getMapPointGeoCoord().longitude}));
                }
                data.put("destinations", destinationsArray);
                data.put("num_people", numPeople);

                try (Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"))) {
                    writer.write(data.toString());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) { // response를 담아서 string으로 반환
                    response.append(responseLine.trim());
                }

                return response.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            handler.post(new Runnable() {
                public void run(){ // 결과를 받아서 apiResult에 담는다
                    synchronized (screen5.this) {
                        apiResult = result;
                        isResultReady = true;
                        screen5.this.notifyAll();
                    }
                }
            });

        }
    }

    private MapPoint getRandomLocation(MapPoint c, int radius) {
        double d2r = Math.PI / 180;
        double r2d = 180 / Math.PI;
        double earth_rad = 6378000f; //지구 반지름 근사값

        double r = new Random().nextInt(radius) + new Random().nextDouble();
        double rlat = (r / earth_rad) * r2d;
        double rlng = rlat / Math.cos(c.getMapPointGeoCoord().latitude * d2r);

        double theta = Math.PI * (new Random().nextInt(2) + new Random().nextDouble());
        //theta = theta + angle;
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
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), screen6_1.class); //화면 전환
                startActivity(intent);
                finish();
            }
        }, 3000); //딜레이 타임 조절
    }

    private void stopHandler() {
        handler2.removeCallbacksAndMessages(null);
    }

    public void button1(View v){
        Intent intent = new Intent(getApplicationContext(), screen4.class);
        startActivity((intent));
        finish();
    }


}
