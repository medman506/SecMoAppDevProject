package ims.fhj.at.emergencyalerter.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
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

import java.util.ArrayList;

import ims.fhj.at.emergencyalerter.EmergencyApplication;
import ims.fhj.at.emergencyalerter.R;
import ims.fhj.at.emergencyalerter.api.PoliceStationRetriever;
import ims.fhj.at.emergencyalerter.model.Contact;
import ims.fhj.at.emergencyalerter.receiver.LocationUpdateReceiver;
import ims.fhj.at.emergencyalerter.receiver.NetworkChangedReceiver;
import ims.fhj.at.emergencyalerter.service.LocationService;
import ims.fhj.at.emergencyalerter.util.App;
import ims.fhj.at.emergencyalerter.util.DatabaseUtil;
import ims.fhj.at.emergencyalerter.util.LocationTracker;
import ims.fhj.at.emergencyalerter.util.MessageUtil;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button btnToMapActivity;
    private Button btnToSettingsActivity;
    private Button btnStartEmergencyPhoneCall;
    private Button btnSendEmergencyMessage;

    // location service intent
    private Intent serviceIntent;

    // location tracker
    private LocationTracker locationTracker;

    // police station retriever
    private PoliceStationRetriever policeStationRetriever;

    // broadcast receiver
    private LocationUpdateReceiver locationUpdateReceiver;
    private IntentFilter intentFilter;

    private NetworkChangedReceiver networkChangedReceiver;
    private NetworkChangedReceiver.NetworkChangedListener networkChangedListener;

    private MessageUtil messageUtil;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_main);

        // keep reference of activity object
        EmergencyApplication emergencyApplication = (EmergencyApplication) this.getApplicationContext();
        emergencyApplication.mainActivity = this;
        prefs =  PreferenceManager.getDefaultSharedPreferences(this);
        messageUtil = MessageUtil.getInstance(getApplicationContext());

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

        // button to send emergency messages
        btnSendEmergencyMessage = (Button) findViewById(R.id.panic_message);
        btnSendEmergencyMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSendMessagePermission();
            }
        });

        // network changed listener
        networkChangedListener = new NetworkChangedReceiver.NetworkChangedListener() {
            @Override
            public void onNetworkChanged(String name, long connectTime, String info) {
                handleNetworkChangedEvent(name, connectTime, info);
            }

            @Override
            public void onNetworkDown() {
                // TODO handle network down event?
            }

            @Override
            public void onNetworkUp() {
                // TODO handle network up event?
            }
        };

        // check for location permission on app start
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, App.PERMISSION_REQUEST_FINE_LOCATION);
        }

        // police station retriever
        policeStationRetriever = new PoliceStationRetriever(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");
        
        if (serviceIntent != null) {
            stopService(serviceIntent);
        }
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
                    showEmergencyPermissionDenied();
                }
                break;
            case App.PERMISSION_REQUEST_SEND_SMS:
                // if request was cancelled, all the arrays are empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, we can continue on
                    startEmergencyMessagePermissionGranted();
                } else {
                    // permission was denied, we may not be able to continue as planned
                    showEmergencyPermissionDenied();
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        initConstants();
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // set up location service
            serviceIntent = new Intent(this, LocationService.class);
            startService(serviceIntent);
        }

        addListeners();
    }

    private void addListeners() {
        // set up network changed listener
        if (networkChangedReceiver == null) {
            networkChangedReceiver = new NetworkChangedReceiver();
            networkChangedReceiver.setListener(networkChangedListener);
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

    /**
     * Initializes constants for app-wide shared constant strings
     */
    private void initConstants() {
        App.BROADCAST_LOCATION_UPDATE = "ims.fhj.at.emergencyalerter.LOCATION_UPDATE";
        App.BROADCAST_NETWORK_CHANGED = "android.provider.Telephony.SMS_RECEIVED";
        App.BROADCAST_WIFI_CHANGED = "android.net.wifi.WIFI_STATE_CHANGED";
        App.BROADCAST_NETWORK_PROVIDER_CHANGED = "android.intent.action.PROVIDER_CHANGED";
    }

    private void startEmergencyPhoneCallPermissionGranted() {
        try {
            String number = "tel:"+prefs.getString(App.SETTING_EMGERGENCY_NUMBER,App.EMERGENCY_PHONE_NUMBER_DEFAULT);

            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
            startActivity(callIntent);
        } catch (SecurityException e) {
            showEmergencyPermissionDenied();
            e.printStackTrace();
        }
    }

    private void handleNetworkChangedEvent(String lat, long radius, String lng) {
        // if police station retriever has been initialized, retrieve police stations
        if (policeStationRetriever != null) {
            Log.d(TAG, "##2a## police station retriever is not null");
            policeStationRetriever.retrieveNearbyPoliceStations(lat, lng, radius);
        } else {
            Log.d(TAG, "##2b## police station retriever is null");
        }
    }

    private void checkSendMessagePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                // TODO show an explanation?
            } else {
                // no explanation needed, we can request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, App.PERMISSION_REQUEST_SEND_SMS);
            }
        } else {
            // we have the permission, start the phone call
            startEmergencyMessagePermissionGranted();
        }
    }

    private void startEmergencyMessagePermissionGranted() {
        try {
            ArrayList<Contact> contacts = DatabaseUtil.getInstance(getApplicationContext()).getContacts();
           messageUtil.sendEmergencyMessage(contacts, locationTracker.getLocation());

        } catch (SecurityException e) {
            showEmergencyPermissionDenied();
            e.printStackTrace();
        }
    }

    private void showEmergencyPermissionDenied() {
        Toast.makeText(this, "You did not grant the necessary Permissions. Good luck anyways!", Toast.LENGTH_LONG).show();
    }

    public void setCurrentLocation(Location location) {
        if (locationTracker != null) {
            locationTracker.setLocation(location);
        } else {
            Log.e(TAG, "could not set current location: no location tracker available");
        }
    }
}