package com.example.thomas.hackathonproject;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class StationWebServiceClient {

    public static URL urlBuilder(double lat, double lng)
    {
        try {
            URL url = new URL("http://10.0.2.2:8080/stations?lat="+lat+"&lng="+lng);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Station> getStationsByURL(URL u)
    {
        List<Station> lst = new ArrayList<>();

        try {
            URLConnection connection = u.openConnection();
            InputStreamReader ins = new InputStreamReader(connection.getInputStream());
            BufferedReader in = new BufferedReader(ins);

            String json = "", line = "";
            while ((line = in.readLine()) != null) {
                json = json + line;
            }
            in.close();

            JSONArray ja = new JSONArray(json);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                Station s = new Station();
                s.setName(jo.getString("StationName"));
                s.setLat(jo.getDouble("Latitude"));
                s.setLng(jo.getDouble("Longitude"));
                lst.add(s);
            }

            return lst;

        } catch (Exception e) {
            return new ArrayList<Station>();
        }
    }
}
