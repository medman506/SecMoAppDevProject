package ims.fhj.at.emergencyalerter.util;

import android.location.Location;

/**
 * Created by michael.stifter on 07.12.2016.
 */
public class LocationTracker {
    private static LocationTracker ourInstance = new LocationTracker();

    public static LocationTracker getInstance() {
        return ourInstance;
    }

    private Location lastKnownLocation = null;

    private LocationTracker() {

    }

    public void setLocation(Location location) {
        lastKnownLocation = location;
    }

    public Location getLocation() {
        return lastKnownLocation;
    }
}
