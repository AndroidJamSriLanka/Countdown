package com.example.rukshani.countdown;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Main3Activity extends ActionBarActivity implements AsyncResponse{

    final static private long ONE_SECOND = 1000;
    final static private long FIVE_MINUTES=6*60*ONE_SECOND;

    PendingIntent pi;//A description of an Intent and target action to perform with it
    BroadcastReceiver br;//Base class for code that will receive intents sent by sendBroadcast()
    AlarmManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        setup();

        WebService webService = new WebService(Main3Activity.this, "get", "Loading");
        webService.asyncResponse = this;

        Bundle b=this.getIntent().getExtras();
        String city=b.getString("city");
        String des=b.getString("des");
        String mode=b.getString("mode");

        System.out.println("Current city: "+city);
        System.out.println("Destination city: "+des);
        System.out.println("mode of travelling: "+mode);

        //https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&avoid=highways&mode=bicycling&key=API_KEY

        webService.execute("https://maps.googleapis.com/maps/api/directions/json?origin="+city+"&destination="+des+"&avoid=highways&mode="+mode+"");

        Button button= (Button) findViewById(R.id.notificationButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Reminder", "Reminder button was clicked");

                SharedPreferences sharedPreferences = getSharedPreferences("Countdown", MODE_PRIVATE);
                Long milliDurationValue=sharedPreferences.getLong("milliDurationValue",0);
                System.out.println("milliDurationValue:" +milliDurationValue);
                Long reminderTime=milliDurationValue-FIVE_MINUTES;
                System.out.println("Reminder time: "+reminderTime);
                am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + reminderTime, pi);//Returns milliseconds since boot, including time spent in sleep.
                Toast.makeText(getApplicationContext(),"Notification is set",Toast.LENGTH_SHORT).show();
            }
        });

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

            String durationSeconds = durationObject.getString("value");
            System.out.println("durationSeconds:"+durationSeconds);

            Long durationValue=Long.parseLong(durationSeconds);
            Long milliDurationValue=durationValue*ONE_SECOND;
            System.out.println("durationInMilliseconds:"+milliDurationValue);

            SharedPreferences sharedPreferences = getSharedPreferences("Countdown", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("milliDurationValue", milliDurationValue);
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setup() {
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {

                NotificationManager nm=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                android.app.Notification notify=new android.app.Notification(R.drawable.two,"",System.currentTimeMillis());
                Context context=Main3Activity.this;//the context that pi should start is Main3Activity
                CharSequence title="Closer to destination";
                CharSequence details="Continue with your work";//the context in expanded entry
                Intent intent=new Intent(context,Notification.class);//from Main3 to Notification
                //A description of an Intent and target action to perform with it
                //granting other app to perform the operation specified
                PendingIntent pending=PendingIntent.getActivity(context, 0, intent, 0);//context,request code for user,array of intent activities to do,flags
                notify.setLatestEventInfo(context,title, details, pending);
                notify.sound= Uri.parse(""+R.raw.sound);
                nm.notify(0, notify);//to post a notification to be shown in the status bar
            }

        };

        registerReceiver(br, new IntentFilter("") );
        pi = PendingIntent.getBroadcast( this, 0, new Intent(""),0 );
        am = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
    }

    protected void onDestroy() {
        am.cancel(pi);
        unregisterReceiver(br);
        super.onDestroy();
    }
}
