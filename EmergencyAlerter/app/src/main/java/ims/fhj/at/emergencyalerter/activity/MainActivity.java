package ims.fhj.at.emergencyalerter.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import ims.fhj.at.emergencyalerter.EmergencyApplication;
import ims.fhj.at.emergencyalerter.R;
import ims.fhj.at.emergencyalerter.receiver.LocationUpdateReceiver;
import ims.fhj.at.emergencyalerter.service.LocationService;
import ims.fhj.at.emergencyalerter.util.App;
import ims.fhj.at.emergencyalerter.util.LocationTracker;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button btnToMapActivity;
    private Button btnToSettingsActivity;
    private Button btnStartEmergencyPhoneCall;

    // location service intent
    private Intent serviceIntent;

    // location tracker
    private LocationTracker locationTracker;

    // broadcast receiver
    private LocationUpdateReceiver locationUpdateReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // keep reference of activity object
        EmergencyApplication emergencyApplication = (EmergencyApplication) this.getApplicationContext();
        emergencyApplication.mainActivity = this;

        locationUpdateReceiver = new LocationUpdateReceiver();
        intentFilter = new IntentFilter(App.BROADCAST_LOCATION_UPDATE);

        // set up location tracker
        locationTracker = LocationTracker.getInstance();

        // button to navigate to map activity
        btnToMapActivity = (Button) findViewById(R.id.to_map_activity);
        btnToMapActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapActivityIntent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(mapActivityIntent);
            }
        });

        // button to settings
        btnToSettingsActivity = (Button) findViewById(R.id.to_settings_activity);
        btnToSettingsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsActivityIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsActivityIntent);
            }
        });

        // button to start emergency phone call
        btnStartEmergencyPhoneCall = (Button) findViewById(R.id.panic_call);
        btnStartEmergencyPhoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStartCallPermission();
            }
        });

        // set up location service
        serviceIntent = new Intent(this, LocationService.class);
        startService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");
        stopService(serviceIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case App.PERMISSION_REQUEST_CALL_PHONE:
                // if request was cancelled, all the arrays are empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, we can continue on
                    startEmergencyPhoneCallPermissionGranted();
                } else {
                    // permission was denied, we may not be able to continue as planned
                    startEmergencyPhoneCallPermissionDenied();
                }
                break;
        }
    }

    private void checkStartCallPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                // TODO show an explanation?
            } else {
                // no explanation needed, we can request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, App.PERMISSION_REQUEST_CALL_PHONE);
            }
        } else {
            // we have the permission, start the phone call
            startEmergencyPhoneCallPermissionGranted();
        }
    }

    private void startEmergencyPhoneCallPermissionGranted() {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(App.EMERGENCY_PHONE_NUMBER));
            startActivity(callIntent);
        } catch (SecurityException e) {
            startEmergencyPhoneCallPermissionDenied();
            e.printStackTrace();
        }
    }

    private void startEmergencyPhoneCallPermissionDenied() {
        Toast.makeText(this, "You didn't let us start a phone call. Good luck", Toast.LENGTH_LONG).show();
    }

    public void setCurrentLocation(Location location) {
        if (locationTracker != null) {
            locationTracker.setLocation(location);
        } else {
            Log.e(TAG, "could not set current location: no location tracker available");
        }
    }
}