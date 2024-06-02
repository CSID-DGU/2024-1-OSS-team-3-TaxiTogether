package com.taxitogether.app;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.daum.mf.map.api.MapPoint;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class screen7 extends AppCompatActivity {
    private Handler handler = new Handler();
    // 승객이 혼자 탑승했을 경우 지불했을 금액과 걸린 시간
    int past_cost=1; int past_time=1;
    int time=1;
    MapPoint start, end;
    ArrayList<MapPoint> way = new ArrayList<>();
    String apiKey = "af3a07081f830adca6b60768135b5e54";

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen7);

        way = ((ValueApplication) getApplication()).get_waypoints();
        start = MapPoint.mapPointWithGeoCoord(((ValueApplication)getApplication()).get_start_latitude(), ((ValueApplication)getApplication()).get_start_longitude());
        end = MapPoint.mapPointWithGeoCoord(((ValueApplication)getApplication()).get_end_latitude(), ((ValueApplication)getApplication()).get_end_longitude());

        new LoadDataTask().execute();

        progressBar = findViewById(R.id.progressBar);
        startHandler();
    }

    private class LoadDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            // 여기에 데이터를 로드하는 로직을 추가합니다.
            try {
                gettimeKakaoAPI(apiKey, start, end, way);
                getpastKakaoAPI(apiKey, start, end);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateUI();
        }
    }

    private void gettimeKakaoAPI(String apiKey, MapPoint m1, MapPoint m2, ArrayList<MapPoint> waypoints) throws Exception {
        String urlString;
        if(waypoints.size()==3){ // 경유지까지 설정
            urlString = String.format("https://apis-navi.kakaomobility.com/v1/directions?origin=%s,%s&destination=%s,%s&waypoints=%s,%s|%s,%s",
                    URLEncoder.encode(String.valueOf(m1.getMapPointGeoCoord().longitude), "UTF-8"),
                    URLEncoder.encode(String.valueOf(m1.getMapPointGeoCoord().latitude), "UTF-8"),
                    URLEncoder.encode(String.valueOf(waypoints.get(2).getMapPointGeoCoord().longitude), "UTF-8"),
                    URLEncoder.encode(String.valueOf(waypoints.get(2).getMapPointGeoCoord().latitude), "UTF-8"),
                    URLEncoder.encode(String.valueOf(waypoints.get(0).getMapPointGeoCoord().longitude), "UTF-8"),
                    URLEncoder.encode(String.valueOf(waypoints.get(0).getMapPointGeoCoord().latitude), "UTF-8"),
                    URLEncoder.encode(String.valueOf(waypoints.get(1).getMapPointGeoCoord().longitude), "UTF-8"),
                    URLEncoder.encode(String.valueOf(waypoints.get(1).getMapPointGeoCoord().latitude), "UTF-8"));
        }
        else if(waypoints.size()==4){
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
        }
        else if(waypoints.size()==2){
            urlString = String.format("https://apis-navi.kakaomobility.com/v1/directions?origin=%s,%s&destination=%s,%s&waypoints=%s,%s",
                    URLEncoder.encode(String.valueOf(m1.getMapPointGeoCoord().longitude), "UTF-8"),
                    URLEncoder.encode(String.valueOf(m1.getMapPointGeoCoord().latitude), "UTF-8"),
                    URLEncoder.encode(String.valueOf(waypoints.get(1).getMapPointGeoCoord().longitude), "UTF-8"),
                    URLEncoder.encode(String.valueOf(waypoints.get(1).getMapPointGeoCoord().latitude), "UTF-8"),
                    URLEncoder.encode(String.valueOf(waypoints.get(0).getMapPointGeoCoord().longitude), "UTF-8"),
                    URLEncoder.encode(String.valueOf(waypoints.get(0).getMapPointGeoCoord().latitude), "UTF-8"));
        }
        else{
            urlString = String.format("https://apis-navi.kakaomobility.com/v1/directions?origin=%s,%s&destination=%s,%s",
                    URLEncoder.encode(String.valueOf(m1.getMapPointGeoCoord().longitude), "UTF-8"),
                    URLEncoder.encode(String.valueOf(m1.getMapPointGeoCoord().latitude), "UTF-8"),
                    URLEncoder.encode(String.valueOf(m2.getMapPointGeoCoord().longitude), "UTF-8"),
                    URLEncoder.encode(String.valueOf(m2.getMapPointGeoCoord().latitude), "UTF-8"));
        }

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
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
                time = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONObject("summary").getInt("duration") / 60;
            } else {
                throw new Exception("Error fetching data from Kakao API: HTTP " + responseCode);
            }
        }finally{
            connection.disconnect();
        }
    }

    private void getpastKakaoAPI(String apiKey, MapPoint m1, MapPoint m2) throws Exception {
        String urlString;
        urlString = String.format("https://apis-navi.kakaomobility.com/v1/directions?origin=%s,%s&destination=%s,%s",
                URLEncoder.encode(String.valueOf(m1.getMapPointGeoCoord().longitude), "UTF-8"),
                URLEncoder.encode(String.valueOf(m1.getMapPointGeoCoord().latitude), "UTF-8"),
                URLEncoder.encode(String.valueOf(m2.getMapPointGeoCoord().longitude), "UTF-8"),
                URLEncoder.encode(String.valueOf(m2.getMapPointGeoCoord().latitude), "UTF-8"));

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
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
                past_time = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONObject("summary").getInt("duration") / 60;
                past_cost = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONObject("summary").getJSONObject("fare").getInt("taxi");
            }else{
                throw new Exception("Error fetching data from Kakao API: HTTP " + responseCode);
            }
        }finally{
            connection.disconnect();
        }
    }

    private void updateUI() {
        int cost = ((ValueApplication) getApplication()).get_my_fare();
        int profit = past_cost - cost;
        int more_time = time - past_time;

        float cost_rate = 2 - (float) cost / past_cost; // 금액은 무조건 싸지기 때문에 따로 조정할 필요 없다
        float time_rate = 1 - (float) more_time/(4*past_time); // 과거 시간의 네배보다 더 걸리면 걍 무조건 손해라고 가정
        if (time_rate<=0){
            time_rate = 2/cost_rate; // 퍼센트 계산을 위함
        }
        int rate = (int)(time_rate * cost_rate*100)-100;
        ((ValueApplication) getApplication()).set_more_time(more_time);
        ((ValueApplication) getApplication()).set_profit(profit);
        ((ValueApplication) getApplication()).set_past_time(past_time);
        ((ValueApplication) getApplication()).set_past_cost(past_cost);
        ((ValueApplication) getApplication()).set_rate(rate);
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
