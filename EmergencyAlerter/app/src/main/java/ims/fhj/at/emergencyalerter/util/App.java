package ims.fhj.at.emergencyalerter.util;

/**
 * Created by michael.stifter on 11.11.2016.
 */

public class App {

    // All app constants can go here
    public static final String DUMMY = "dummy_constant";

    // Emergency number to call
    public static final String EMERGENCY_PHONE_NUMBER = "tel:989898989898";

    // Google constants
    public static final String GOOGLE_API_KEY = "AIzaSyDyGHM8lcNSX3RUrmSvYoTNX52vSlFKb3g";
    public static final String GOOGLE_PLACES_QUERY_TYPE = "police";

    // Permission requests
    public static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    public static final int PERMISSION_REQUEST_CALL_PHONE = 2;

    // Location service broadcast
    public static final String BROADCAST_LOCATION_UPDATE = "ims.fhj.at.emergencyalerter.LOCATION_UPDATE";

    public static final String EXTRA_LATITUDE = "lat";
    public static final String EXTRA_LONGITUDE = "lng";

}
