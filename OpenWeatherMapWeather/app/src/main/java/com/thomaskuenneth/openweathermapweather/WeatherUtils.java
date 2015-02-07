package com.thomaskuenneth.openweathermapweather;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;

public class WeatherUtils {

    private static final String URL = "http://api.openweathermap.org/data/2.5/weather?q={0}&lang=de";

    private static final String NAME = "name";
    private static final String WEATHER = "weather";
    private static final String DESCRIPTION = "description";
    private static final String ICON = "icon";
    private static final String MAIN = "main";
    private static final String TEMP = "temp";

    public static WeatherData getWeather(String city) throws JSONException,
            IOException /* MalformedURLException */ {
        String name = null;
        String description = null;
        String icon = null;
        Double temp = null;
        JSONObject jsonObject = new JSONObject(
                WeatherUtils.getFromServer(MessageFormat.format(URL, city)));
        if (jsonObject.has(NAME)) {
            name = jsonObject.getString(NAME);
        }
        if (jsonObject.has(WEATHER)) {
            JSONArray jsonArrayWeather = jsonObject.getJSONArray(WEATHER);
            if (jsonArrayWeather.length() > 0) {
                JSONObject jsonWeather = jsonArrayWeather.getJSONObject(0);
                if (jsonWeather.has(DESCRIPTION)) {
                    description = jsonWeather.getString(DESCRIPTION);
                }
                if (jsonWeather.has(ICON)) {
                    icon = jsonWeather.getString(ICON);
                }
            }
        }
        if (jsonObject.has(MAIN)) {
            JSONObject main = jsonObject.getJSONObject(MAIN);
            temp = main.getDouble(TEMP);
        }
        return new WeatherData(name, description, icon, temp);
    }

    public static String getFromServer(String url)
            throws IOException /* MalformedURLException */ {
        StringBuilder sb = new StringBuilder();
        URL _url = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) _url
                .openConnection();
        final int responseCode = httpURLConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    httpURLConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            try {
                bufferedReader.close();
            } catch (IOException e) {
                // ein Fehler beim Schließen wird bewusst ignoriert
            }
        }
        httpURLConnection.disconnect();
        return sb.toString();
    }

    public static Bitmap getImage(WeatherData w) throws IOException /* MalformedURLException */ {
        URL req = new URL("http://openweathermap.org/img/w/" + w.icon + ".png");
        HttpURLConnection c = (HttpURLConnection) req.openConnection();
        Bitmap bmp = BitmapFactory.decodeStream(c
                .getInputStream());
        c.disconnect();
        return bmp;
    }
}
