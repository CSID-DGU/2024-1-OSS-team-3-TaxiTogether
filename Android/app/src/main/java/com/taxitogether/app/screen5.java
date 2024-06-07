package com.taxitogether.app;

import static java.lang.Math.round;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


// 10개의 목적지 집합을 생성하고 이를 백엔드에 제공하는 java 코드
public class screen5 extends AppCompatActivity {
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean running = true;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ProgressBar progressBar;
    private boolean is_route_valid= false;
    private boolean isResultReady = false;
    private String apiResult;
    private JSONArray bestRoute;
    private JSONObject fares;
    private double totalFare;
    private JSONObject percentages;
    private JSONObject points;
    private Thread myThread;
    private ArrayList<MapPoint> destinations;
    private MapPoint start, end, ra;
    private int r, size;
    private double angle, temp_angle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen5);
        // 시작점과 목적지 좌표 반환
        start = MapPoint.mapPointWithGeoCoord(((ValueApplication) getApplication()).get_start_latitude(), ((ValueApplication) getApplication()).get_start_longitude());
        end = MapPoint.mapPointWithGeoCoord(((ValueApplication) getApplication()).get_end_latitude(), ((ValueApplication) getApplication()).get_end_longitude());
        // 거리 반환
        r = (int)calculateDistance(start, end);
        if(r>10000){
            size = 1;
        }
        else if(r>5000){
            size = 2;
        }
        else{
            size = 3;
        }
        // 출발점과 목적지의 각도 계산
        angle = calculateAngleInRadians(start, end);
        destinations = new ArrayList<>();
        destinations.add(end); // 사용자의 목적지를 Queue에 삽입
        progressBar = findViewById(R.id.progressBar);
        startHandler();
        ra = getRandomLocation(end, r*size);
        temp_angle=calculateAngleInRadians(start, ra);
        while(temp_angle>angle+Math.PI/4||temp_angle<angle-Math.PI/4){ // 범위 안에 있는 것을 골라냄
            Log.d("Point_is_not_in_sector", "Point is not in sector!");
            ra = getRandomLocation(end, r*size);
            temp_angle = calculateAngleInRadians(start, ra);
        }
        destinations.add(ra);

        startDestinationUpdates();
    }

    private void startDestinationUpdates(){
        executor.submit(() -> {
            MapPoint randompoint = ra;
            while(running){ // 끝없이 다른 사용자의 목적지가 Queue에 들어온다고 가정
                PostTask task = new PostTask(start, destinations);
                task.execute();

                // 결과가 준비될 때까지 대기
                synchronized (screen5.this) {
                    while (!isResultReady) {
                        try {
                            screen5.this.wait(100); // 100ms 단위로 대기
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    isResultReady=false;
                }
                // 결과 처리
                if (apiResult!=null) {
                    // 결과를 로그에 출력
                    Log.d("API Response", apiResult);
                    try {
                        JSONObject jsonResponse = new JSONObject(apiResult);
                        bestRoute = jsonResponse.getJSONArray("best_route");
                        fares = jsonResponse.getJSONObject("fares");
                        totalFare = jsonResponse.getDouble("total_fare");
                        percentages = jsonResponse.getJSONObject("percentage");
                        points = jsonResponse.getJSONObject("points");
                        if(is_route_valid && destinations.size()==4){
                            ArrayList<MapPoint> newdestinations = new ArrayList<>();
                            ((ValueApplication) getApplication()).set_total_fare((int)round(totalFare)); // 총 금액을 전역 변수로
                            try{
                                MapPoint point;
                                for(int i=0; i<bestRoute.length(); i++){ // 경로 순서대로 받아오는 함수
                                    String key = bestRoute.getString(i);
                                    JSONArray coords = points.getJSONArray(key);
                                    point = MapPoint.mapPointWithGeoCoord(coords.getDouble(0), coords.getDouble(1));
                                    newdestinations.add(point);
                                    if(key.equals("0")){
                                        double fare = fares.getDouble(key);
                                        ((ValueApplication) getApplication()).set_my_fare((int)fare); // 사용자의 금액을 전역 변수로
                                        ((ValueApplication) getApplication()).set_waypoints(newdestinations); // 걸린 시간 계산을 위한 사용자까지의 경로
                                    }
                                }
                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                            ((ValueApplication) getApplication()).set_destinations(newdestinations); // 목적지 집합을 전역 변수로
                            Log.d("Route_is_valid_and_complete", "Location: " + randompoint.getMapPointGeoCoord().latitude + ", " + randompoint.getMapPointGeoCoord().longitude);
                            running = false;
                            executor.shutdownNow();
                            new Thread(() -> {
                                try {
                                    while (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                                        Log.d("ExecutorService", "Waiting for tasks to terminate");
                                    }
                                } catch (InterruptedException e) {
                                    Log.e("Interrupted", "Interrupted while waiting for tasks to finish.");
                                    Thread.currentThread().interrupt();
                                }

                                handler.post(() -> {
                                    Intent intent = new Intent(getApplicationContext(), screen6_2.class);
                                    startActivity(intent);
                                    finish();
                                });
                            }).start();
                        }
                        else if(is_route_valid){
                            Log.d("Route_is_valid", "Location: " + randompoint.getMapPointGeoCoord().latitude + ", " + randompoint.getMapPointGeoCoord().longitude);
                            is_route_valid=false;
                            randompoint = getRandomLocation(end, r*size);
                            temp_angle = calculateAngleInRadians(start, randompoint);
                            while(temp_angle>angle+Math.PI/4||temp_angle<angle-Math.PI/4){ // 범위 안에 있는 것을 골라냄
                                Log.d("Point_is_not_in_sector", "Point is not in sector!");
                                randompoint = getRandomLocation(end, r*size);
                                temp_angle = calculateAngleInRadians(start, randompoint);
                            }
                            destinations.add(randompoint);
                        }
                    } catch (JSONException e) {
                        Log.e("JSON parsing error", "error", e);
                        e.printStackTrace();
                    }
                    // 결과가 준비된 후 플래그를 리셋
                    isResultReady = false;
                }else{
                    Log.d("Route_is_not_valid", "Location: " + randompoint.getMapPointGeoCoord().latitude + ", " + randompoint.getMapPointGeoCoord().longitude);
                    destinations.remove(destinations.size()-1); // 만약 유효하지 않을 경우 제거
                    randompoint = getRandomLocation(end, r*size);
                    temp_angle = calculateAngleInRadians(start, randompoint);
                    while(temp_angle>angle+Math.PI/4||temp_angle<angle-Math.PI/4){ // 범위 안에 있는 것을 골라냄
                        Log.d("Point_is_not_in_sector", "Point is not in sector!");
                        randompoint = getRandomLocation(end, r*size);
                        temp_angle = calculateAngleInRadians(start, randompoint);
                    }
                    destinations.add(randompoint);
                }
            }
        });
    }

    // 비동기적으로 API 콜 처리
    private class PostTask extends AsyncTask<Void, Void, String> {
        private MapPoint start;
        private ArrayList<MapPoint> destination;

        public PostTask(MapPoint start, ArrayList<MapPoint> destinations) {
            this.start = start;
            this.destination = destinations;
        }
        @Override
        protected String doInBackground(Void... voids) {
            Log.d("PostTask", "doInBackground started");
            String urlString = "http://beatmania.app:8000/validate_route";
            //String urlString = "http://127.0.0.1:8000/validate_route";
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                Log.d("PostTask", "doInBackground middle");
                JSONObject data = new JSONObject(); // 출발지를 json에 담는다
                JSONObject startCoord = new JSONObject();
                startCoord.put("lat", start.getMapPointGeoCoord().latitude);
                startCoord.put("lon", start.getMapPointGeoCoord().longitude);
                data.put("start", startCoord);

                JSONObject points = new JSONObject(); // 목적지 집합을 json에 담는다
                for(int i=0; i<destination.size(); i++){
                    MapPoint point = destination.get(i);
                    JSONObject coord = new JSONObject();
                    coord.put("lat", point.getMapPointGeoCoord().latitude);
                    coord.put("lon", point.getMapPointGeoCoord().longitude);
                    points.put(""+i, coord);
                }
                data.put("points", points);

                try (Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"))) {
                    writer.write(data.toString());
                }
                int responseCode = urlConnection.getResponseCode();
                Log.d("PostTask", ""+responseCode);
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) { // response를 담아서 string으로 반환
                    response.append(responseLine.trim());
                }
                if(responseCode == HttpURLConnection.HTTP_OK) {
                    is_route_valid=true;
                    Log.d("Validation Success", "Response received:\n" + response.toString());
                    return response.toString();
                } else{
                    is_route_valid = false;
                    Log.d("Validation Failed","Failed to get a valid response: " + responseCode + "\n" + response.toString());
                    return "Failed to get a valid response: " + responseCode + "\n" + response.toString();
                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Connection Failed","Failed to get a valid response", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);// 결과를 받아서 apiResult에 담는다
                synchronized (screen5.this) {
                    apiResult = result;
                    isResultReady = true;
                    screen5.this.notifyAll();
                }

        }
    }

    // 원형 범위 내에서 랜덤 좌표 생성
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


    // 목적지 방향 각도 계산
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

    // 거리 계산
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
        running = false;
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                Log.e("ExecutorService", "Tasks did not terminate in onPause");
            }
        } catch (InterruptedException e) {
            Log.e("Interrupted", "Interrupted while waiting for tasks to finish in onPause.");
            Thread.currentThread().interrupt();
        }
    }

    private void startHandler() {
        Log.d("startHandler", "Handler started");

        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100);
        progressAnimator.setDuration(1000); // 3초
        progressAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        progressAnimator.setRepeatMode(ObjectAnimator.RESTART);
        progressAnimator.start();

        handler.postDelayed(() -> {
            Log.d("startHandler", "postDelayed triggered");
            running = false;

            new Thread(() -> {
                Log.d("startHandler", "Background thread started for shutdown");
                executor.shutdownNow();
                try {
                    while (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                        Log.d("ExecutorService", "Waiting for tasks to terminate");
                    }
                } catch (InterruptedException e) {
                    Log.e("Interrupted", "Interrupted while waiting for tasks to finish.");
                    Thread.currentThread().interrupt();
                }

                handler.post(() -> {
                    Intent intent = new Intent(getApplicationContext(), screen6_1.class);
                    startActivity(intent);
                    finish();
                });
            }).start();

        }, 120000); //딜레이 타임 조절

        // ProgressBar 애니메이션을 계속 작동하게 하는 Runnable
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (running) {
                    progressAnimator.start();
                    handler.postDelayed(this, 1000); // 1초마다 반복
                }
            }
        });
    }

    private void stopHandler() {
        handler.removeCallbacksAndMessages(null);
    }

    public void button1(View v){
        running = false;
        executor.shutdownNow(); // 현재 진행 중인 모든 작업을 중단
        new Thread(() -> {
            try {
                while (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    Log.d("ExecutorService", "Waiting for tasks to terminate");
                }
            } catch (InterruptedException e) {
                Log.e("Interrupted", "Interrupted while waiting for tasks to finish.");
                Thread.currentThread().interrupt();
            }

            handler.post(() -> {
                Intent intent = new Intent(getApplicationContext(), screen4.class);
                startActivity(intent);
                finish();
            });
        }).start();
    }

    protected void onDestroy(){
        super.onDestroy();
        running = false;
        executor.shutdownNow();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                Log.e("ExecutorService", "Tasks did not terminate");
            }
        } catch (InterruptedException e) {
            Log.e("Interrupted", "Interrupted while waiting for tasks to finish.");
            Thread.currentThread().interrupt();
        }
    }
}
