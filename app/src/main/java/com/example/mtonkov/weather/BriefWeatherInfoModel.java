package com.example.mtonkov.weather;

/**
 * Created by M.Tonkov on 13.2.2015 г..
 */
public class BriefWeatherInfoModel {

    private String location;
    private String temperature;


    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        if (location != null && !location.isEmpty()) {
            this.location = location;
        }
    }

    public String getTemperature() {
        return this.temperature;
    }

    public void setTemperature(String temperature) {
        if (temperature != null && !temperature.isEmpty()) {
            this.temperature = temperature;
        }
    }
}

