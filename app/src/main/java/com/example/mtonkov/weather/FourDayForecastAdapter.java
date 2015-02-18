package com.example.mtonkov.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by M.Tonkov on 18.2.2015 г..
 */
public class FourDayForecastAdapter extends BaseAdapter {

    private List<FourDayForecastModel> forecastDays;
    private LayoutInflater layoutInflater;

    public FourDayForecastAdapter(Context context, List<FourDayForecastModel> forecastDays) {
        this.forecastDays = forecastDays;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return forecastDays.size();
    }

    @Override
    public Object getItem(int position) {
        return forecastDays.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.forecast_day_layout, null);
            holder = new ViewHolder();
            holder.dayOfWeek = (TextView) convertView.findViewById(R.id.forecast_day);
            holder.conditions = (TextView) convertView.findViewById(R.id.forecast_conditions);
            holder.minMaxTemperature = (TextView) convertView.findViewById(R.id.forecast_temperatures);
            holder.minMaxWindSpeed = (TextView) convertView.findViewById(R.id.forecast_wind_speeds);
            holder.minMaxHumidity = (TextView) convertView.findViewById(R.id.forecast_humidity);
            holder.image = (ImageView) convertView.findViewById(R.id.forecast_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FourDayForecastModel model = forecastDays.get(position);
        holder.dayOfWeek.setText(model.getmDayOfWeek());
        holder.conditions.setText(model.getmConditions());
        holder.minMaxTemperature.setText(model.getmMinTemperature() + "/" + model.getmMaxTemperature() + "° C");
        holder.minMaxWindSpeed.setText(model.getmMinWindSpeed() + "/" + model.getmMaxWindSpeed() + " km/h");
        holder.minMaxHumidity.setText(model.getmMinHumidity() + "/" + model.getmMaxHumidity() + " %");

        ImageLoadTask imgLoader = new ImageLoadTask(model.getmImageSrc(), holder.image);
        imgLoader.execute();

        return convertView;
    }

    private static class ViewHolder {
        TextView dayOfWeek;
        TextView conditions;
        TextView minMaxTemperature;
        TextView minMaxWindSpeed;
        TextView minMaxHumidity;
        ImageView image;
    }
}
