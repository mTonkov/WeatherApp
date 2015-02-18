package com.example.mtonkov.weather;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener {

    public static final String TAG = MapActivity.class.getSimpleName();
    public static final float SMALLEST_DISPLACEMENT = 10 * 1000; //10km
    public static final long LOCATION_UPDATE_INTERVAL = 1 * 60 * 60 * 1000; //1 minute
    public static final long SHORTEST_LOCATION_UPDATE_INTERVAL = 1 * 1000; //1 second
    public static final String REQUEST_URL = "http://api.wunderground.com/api/fedfc56ec42b0d70/conditions/forecast10day/q/";

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocationOnMap;
    private String mCurrentLocationName;
    private String mForecastResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();


        initGoogleApiClient();
        initLocationUpdateRequest();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        applyLocation(latLng);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        applyLocation(latLng);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (mCurrentLocationOnMap != null && Math.abs(mCurrentLocationOnMap.getLatitude() - latLng.latitude) < 0.005 &&
                Math.abs(mCurrentLocationOnMap.getLongitude() - latLng.longitude) < 0.005) {

            mMap.clear();
            mCurrentLocationOnMap = null;
        }
    }

    private void applyLocation(LatLng latLng) {
        String locationName = getLocationName(latLng);

        if (mCurrentLocationOnMap == null || mCurrentLocationName.contentEquals(locationName)) {
            if (mCurrentLocationOnMap == null) {
                HttpGetRequest httpRequester = new HttpGetRequest();
                httpRequester.execute(latLng.latitude, latLng.longitude);
            }

            mCurrentLocationOnMap = determineNewLocation(latLng);
            mCurrentLocationName = locationName;

            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        }
    }

    private void initGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void initLocationUpdateRequest() {
        mLocationRequest = LocationRequest.create()
                .setSmallestDisplacement(SMALLEST_DISPLACEMENT)
                .setPriority(LocationRequest.PRIORITY_LOW_POWER)
                .setInterval(LOCATION_UPDATE_INTERVAL)
                .setFastestInterval(SHORTEST_LOCATION_UPDATE_INTERVAL);//1 second
    }

    private BriefWeatherInfoModel getBriefWeatherInfo(String forecast) {
        BriefWeatherInfoModel result= new BriefWeatherInfoModel();

        try {
            JSONObject briefForecast = new JSONObject(forecast).getJSONObject("current_observation");
            String location = briefForecast.getJSONObject("display_location")
                    .getString("full");
            String degrees = briefForecast.getString("temp_c");

            result.setLocation(location);
            result.setTemperature(degrees);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private String getLocationName(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e1) {
            Log.e(TAG, "IO Exception in getFromLocation()");
            e1.printStackTrace();
            return "IO Exception trying to get address";
        } catch (IllegalArgumentException e2) {

            String errorString = "Illegal arguments " + Double.toString(latLng.latitude) + " , "
                    + Double.toString(latLng.longitude) + " passed to address service";

            Log.e(TAG, errorString);
            e2.printStackTrace();
            return errorString;
        }

        if (addresses != null && addresses.size() > 0) {
            String address = addresses.get(0).getLocality();

            return address;
        } else {
            return "No address found";
        }
    }

    private Location determineNewLocation(LatLng latLng) {
        Location newLocation = new Location("Location");
        newLocation.setLatitude(latLng.latitude);
        newLocation.setLongitude(latLng.longitude);
        newLocation.setTime(new Date().getTime());
        return newLocation;
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setInfoWindowAdapter(
                new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker arg0) {

                        View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);

                        BriefWeatherInfoModel briefWeatherData = getBriefWeatherInfo(mForecastResult);

                        TextView tvLocation = (TextView) v.findViewById(R.id.tv_location);
                        TextView tvDegrees = (TextView) v.findViewById(R.id.tv_degrees);

                        tvLocation.setText(briefWeatherData.getLocation());
                        tvDegrees.setText(briefWeatherData.getTemperature() + " C");

                        return v;
                    }
                });
        mMap.setOnMarkerClickListener(
                new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        marker.showInfoWindow();
                        return true;
                    }
                });
        mMap.setOnInfoWindowClickListener(
                new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent in = new Intent(MapActivity.this, DetailedForecastActivity.class);
                        in.putExtra("forecast", mForecastResult);
                        startActivity(in);
                    }
                }
        );
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private class HttpGetRequest extends AsyncTask<Double, Void, String> {

        @Override
        protected String doInBackground(Double... params) {
            HttpClient client = new DefaultHttpClient();
            String latitude = String.valueOf(params[0]);
            String longitude = String.valueOf(params[1]);
            HttpGet request = new HttpGet(REQUEST_URL + latitude + "," + longitude + ".json");
            StringBuilder body = new StringBuilder();

            try {
                HttpResponse response = client.execute(request); // execute httpGet
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                if (statusCode == HttpStatus.SC_OK) {
                    HttpEntity responseEntity = response.getEntity();
                    String entity = EntityUtils.toString(responseEntity);
                    body.append(entity);
                } else {
                    body.append(statusLine + "\n");
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return body.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            mForecastResult = result;

        }
    }
}
