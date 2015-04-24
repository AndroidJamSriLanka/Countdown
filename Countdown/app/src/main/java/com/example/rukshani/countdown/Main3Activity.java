package com.example.rukshani.countdown;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Main3Activity extends ActionBarActivity implements AsyncResponse{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        WebService webService = new WebService(Main3Activity.this, "get", "Loading");
        webService.asyncResponse = this;
        webService.execute("https://maps.googleapis.com/maps/api/directions/json?origin=Kurunegala&destination=Colombo&avoid=highways&mode=car");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main3, menu);
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void processFinish(String output) {
        System.out.println(output);
        TextView textView1= (TextView) findViewById(R.id.output);
        TextView textView2= (TextView) findViewById(R.id.dis);
        //textView.setText(output);

        try {

            JSONObject jsonObject = new JSONObject(output);
            JSONArray routesArray = jsonObject.getJSONArray("routes");
            JSONObject route = routesArray.getJSONObject(0);
            JSONArray legs = route.getJSONArray("legs");
            JSONObject leg = legs.getJSONObject(0);

            JSONObject distanceObject = leg.getJSONObject("distance");
            String distance = distanceObject.getString("text");
            textView2.setText(distance);

            JSONObject durationObject = leg.getJSONObject("duration");
            String duration = durationObject.getString("text");
            textView1.setText(duration);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
