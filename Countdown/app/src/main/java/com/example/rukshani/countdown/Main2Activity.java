package com.example.rukshani.countdown;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Main2Activity extends ActionBarActivity implements LocationListener{
    public TextView latitudeField;
    public TextView longitudeField;
    public TextView cityField;
    public TextView countryField;

    EditText destination;
    //RadioButton car;
    //RadioButton bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        latitudeField = (TextView) findViewById(R.id.latitude);
        longitudeField = (TextView) findViewById(R.id.longitude);
        cityField= (TextView) findViewById(R.id.city);
        countryField= (TextView) findViewById(R.id.country);

        destination= (EditText) findViewById(R.id.editTextDestination);
        //car= (RadioButton) findViewById(R.id.radioButtonCar);
        //bus= (RadioButton) findViewById(R.id.radioButtonBus);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            onLocationChanged(location);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Double lat = location.getLatitude();
        Double lng = location.getLongitude();
        latitudeField.setText("Latitude :" + lat);
        longitudeField.setText("Longitude:" + lng);

        List<Address> addresses;
        Geocoder geocoder=new Geocoder(getBaseContext(),Locale.getDefault());
        try{
            addresses=geocoder.getFromLocation(lat,lng,5);
            if(addresses.size()>0)
            {
                String cityName = addresses.get(0).getSubAdminArea();
                String countryName = addresses.get(0).getCountryName();

                cityField.setText(cityName);
                countryField.setText(countryName);

            }
        }catch(IOException e){
            e.printStackTrace();
        }

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void searchTime(View view){

        Intent intent=new Intent(this,Main3Activity.class);
        final String des=destination.getText().toString();
        intent.putExtra("city", ((TextView) findViewById(R.id.city)).getText().toString());
        intent.putExtra("des", des);
        startActivity(intent);

        Log.d("Search", "Search button was clicked");

    }
}
