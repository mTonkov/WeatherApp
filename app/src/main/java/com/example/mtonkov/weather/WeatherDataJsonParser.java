package com.example.mtonkov.weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by M.Tonkov on 17.2.2015 г..
 */
public class WeatherDataJsonParser {
    private String mWeatherDataAsJSON;

    public WeatherDataJsonParser(String mWeatherDataAsJSON) {
        this.mWeatherDataAsJSON = mWeatherDataAsJSON;
    }

    public WeatherDetailsModel getWeatherDetails() {
        WeatherDetailsModel model = new WeatherDetailsModel();

        try {
            JSONObject forecastData = new JSONObject(mWeatherDataAsJSON).getJSONObject("current_observation");

            model.setmLocation(forecastData.getJSONObject("display_location")
                    .getString("full"));
            model.setmConditions(forecastData.getString("weather"));
            model.setmTemperature(forecastData.getString("temp_c"));
            model.setmFeelsLike(forecastData.getString("feelslike_c"));
            model.setmWind(forecastData.getString("wind_kph"));
            model.setmPressure(forecastData.getString("pressure_mb"));
            model.setmHumidity(forecastData.getString("relative_humidity"));
            model.setmImageSrc(forecastData.getString("icon_url"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return model;
    }

    public List<FourDayForecastModel> getWeatherForecast() {
        ArrayList<FourDayForecastModel> list = new ArrayList<FourDayForecastModel>();

        try {
            JSONArray forecastData = new JSONObject(mWeatherDataAsJSON)
                    .getJSONObject("forecast")
                    .getJSONObject("simpleforecast")
                    .getJSONArray("forecastday");

            for (int i = 1; i < 5; i++) {
                JSONObject forecastDay = forecastData.getJSONObject(i);
                FourDayForecastModel model = new FourDayForecastModel();

                model.setmDayOfWeek(forecastDay.getJSONObject("date")
                .getString("weekday"));
                model.setmConditions(forecastDay.getString("conditions"));
                model.setmImageSrc(forecastDay.getString("icon_url"));

                model.setmMinHumidity(forecastDay.getString("minhumidity"));
                model.setmMaxHumidity(forecastDay.getString("maxhumidity"));
                model.setmMinTemperature(forecastDay.getJSONObject("low")
                        .getString("celsius"));
                model.setmMaxTemperature(forecastDay.getJSONObject("high")
                        .getString("celsius"));
                model.setmMinWindSpeed(forecastDay.getJSONObject("avewind")
                        .getString("kph"));
                model.setmMaxWindSpeed(forecastDay.getJSONObject("maxwind")
                        .getString("kph"));

                list.add(model);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }
}
