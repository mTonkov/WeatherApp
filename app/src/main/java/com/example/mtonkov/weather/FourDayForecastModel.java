package com.example.mtonkov.weather;

/**
 * Created by M.Tonkov on 17.2.2015 г..
 */
public class FourDayForecastModel {

    private String mDayOfWeek;
    private String mImageSrc;
    private String mMinTemperature;
    private String mMaxTemperature;
    private String mMinWindSpeed;
    private String mMaxWindSpeed;
    private String mMinHumidity;
    private String mMaxHumidity;
    private String mConditions;

    public String getmConditions() {
        return mConditions;
    }

    public void setmConditions(String mConditions) {
        this.mConditions = mConditions;
    }

    public String getmDayOfWeek() {
        return mDayOfWeek;
    }

    public void setmDayOfWeek(String mDayOfWeek) {
        this.mDayOfWeek = mDayOfWeek;
    }

    public String getmImageSrc() {
        return mImageSrc;
    }

    public void setmImageSrc(String mImageSrc) {
        this.mImageSrc = mImageSrc;
    }

    public String getmMinTemperature() {
        return mMinTemperature;
    }

    public void setmMinTemperature(String mMinTemperature) {
        this.mMinTemperature = mMinTemperature;
    }

    public String getmMaxTemperature() {
        return mMaxTemperature;
    }

    public void setmMaxTemperature(String mMaxTemperature) {
        this.mMaxTemperature = mMaxTemperature;
    }

    public String getmMinWindSpeed() {
        return mMinWindSpeed;
    }

    public void setmMinWindSpeed(String mMinWindSpeed) {
        this.mMinWindSpeed = mMinWindSpeed;
    }

    public String getmMaxWindSpeed() {
        return mMaxWindSpeed;
    }

    public void setmMaxWindSpeed(String mMaxWindSpeed) {
        this.mMaxWindSpeed = mMaxWindSpeed;
    }

    public String getmMinHumidity() {
        return mMinHumidity;
    }

    public void setmMinHumidity(String mMinHumidity) {
        this.mMinHumidity = mMinHumidity;
    }

    public String getmMaxHumidity() {
        return mMaxHumidity;
    }

    public void setmMaxHumidity(String mMaxHumidity) {
        this.mMaxHumidity = mMaxHumidity;
    }

}
