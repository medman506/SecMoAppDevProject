package ims.fhj.at.emergencyalerter.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import ims.fhj.at.emergencyalerter.util.App;

public class LocationService extends Service {

    private static final String TAG = LocationService.class.getSimpleName();

    private LocationManager locationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    public LocationService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.d(TAG, "onCreate");

        initializeLocationManager();

        try {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    locationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    locationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (locationManager != null) {
            for (int i = 0; i < locationListeners.length; i++) {
                try {
                    // TODO handle permissions?
                    locationManager.removeUpdates(locationListeners[i]);
                } catch (SecurityException s) {
                    Log.i(TAG, "fail to remove location listener", s);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.d(TAG, "initializeLocationManager");
        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private class EmergencyLocationListener implements android.location.LocationListener {

        private Location lastKnownLocation;

        public EmergencyLocationListener(String provider) {
            Log.d(TAG, "Location Provider: " + provider);
            lastKnownLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "Location update: " + location);
            lastKnownLocation.set(location);

            // send location update broadcast
            Intent i = new Intent(App.BROADCAST_LOCATION_UPDATE);
            i.putExtra(App.EXTRA_LATITUDE, location.getLatitude());
            i.putExtra(App.EXTRA_LONGITUDE, location.getLongitude());
            sendBroadcast(i);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.d(TAG, "Status changed: " + s);
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.d(TAG, "Provider enabled: " + s);
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.e(TAG, "Provider disabled: " + s);
        }
    }

    LocationListener[] locationListeners = new LocationListener[] {
            new EmergencyLocationListener(LocationManager.GPS_PROVIDER),
            new EmergencyLocationListener(LocationManager.NETWORK_PROVIDER)
    };
}