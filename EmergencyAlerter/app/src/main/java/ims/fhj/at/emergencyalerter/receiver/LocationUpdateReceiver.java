package ims.fhj.at.emergencyalerter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import ims.fhj.at.emergencyalerter.EmergencyApplication;
import ims.fhj.at.emergencyalerter.activity.MainActivity;
import ims.fhj.at.emergencyalerter.util.App;

public class LocationUpdateReceiver extends BroadcastReceiver {

    private static final String TAG = LocationUpdateReceiver.class.getSimpleName();

    public LocationUpdateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "received location update");

        Double lat = intent.getDoubleExtra(App.EXTRA_LATITUDE, 0.00d);
        Double lng = intent.getDoubleExtra(App.EXTRA_LONGITUDE, 0.00d);
        Location currentLocation = new Location("");
        currentLocation.setLatitude(lat);
        currentLocation.setLongitude(lng);

        MainActivity mainActivity = ((EmergencyApplication) context.getApplicationContext()).mainActivity;
        mainActivity.setCurrentLocation(currentLocation);
    }
}
