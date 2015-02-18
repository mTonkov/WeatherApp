package com.example.mtonkov.weather;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class DetailedForecastActivity extends ActionBarActivity {

    private String mForecast;
    private TextView mLocation;
    private TextView mConditions;
    private TextView mTemperature;
    private TextView mFeelsLike;
    private TextView mWindSpeed;
    private TextView mPressure;
    private TextView mHumidity;
    private ImageView mConditionsImage;
    private GridView mForecastGrid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_forecast);

        mForecast = getIntent().getStringExtra("forecast");

        initViewComponents();
        displayForecastData();
    }

    private void displayForecastData() {
        WeatherDataJsonParser parser = new WeatherDataJsonParser(mForecast);
        WeatherDetailsModel weatherDetails = parser.getWeatherDetails();

        ImageLoadTask imgLoader = new ImageLoadTask(weatherDetails.getmImageSrc(), mConditionsImage);
        imgLoader.execute();

        mLocation.setText(weatherDetails.getmLocation());
        mConditions.setText(weatherDetails.getmConditions());
        mTemperature.setText(weatherDetails.getmTemperature() + "° C");
        mFeelsLike.setText("Feels like: " + weatherDetails.getmFeelsLike() + "° C");
        mWindSpeed.setText("Wind speed: " + weatherDetails.getmWind() + " km/h");
        mPressure.setText("Pressure: " + weatherDetails.getmPressure() + " mb");
        mHumidity.setText("Humidity: " + weatherDetails.getmHumidity());

        ArrayList<FourDayForecastModel> fourDaysForecast = (ArrayList<FourDayForecastModel>) parser.getWeatherForecast();

        mForecastGrid.setAdapter(new FourDayForecastAdapter(getApplicationContext(), fourDaysForecast));
    }

    private void initViewComponents() {
        mLocation = (TextView) findViewById(R.id.tv_details_location);
        mConditions = (TextView) findViewById(R.id.tv_main_conditions);
        mTemperature = (TextView) findViewById(R.id.tv_main_degrees);
        mFeelsLike = (TextView) findViewById(R.id.tv_main_feelsLike);
        mWindSpeed = (TextView) findViewById(R.id.tv_main_Wind);
        mPressure = (TextView) findViewById(R.id.tv_main_pressure);
        mHumidity = (TextView) findViewById(R.id.tv_main_humidity);
        mConditionsImage = (ImageView) findViewById(R.id.img_main);
        mForecastGrid = (GridView) findViewById(R.id.forecast_gridView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_detailed_forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
