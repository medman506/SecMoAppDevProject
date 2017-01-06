package ims.fhj.at.emergencyalerter.util;

/**
 * Created by michael.stifter on 11.11.2016.
 */

public class App {

    // All app constants can go here
    public static final String DUMMY = "dummy_constant";
    public static final String SETTING_EMGERGENCY_NUMBER = "emergencyNumber";

    // Emergency number to call
    public static final String EMERGENCY_PHONE_NUMBER_DEFAULT = "0900666666";

    // Google constants
    public static final String GOOGLE_API_KEY = "AIzaSyDyGHM8lcNSX3RUrmSvYoTNX52vSlFKb3g";
    public static final String GOOGLE_PLACES_QUERY_TYPE = "police";

    // Permission requests
    public static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    public static final int PERMISSION_REQUEST_CALL_PHONE = 2;
    public static final int PERMISSION_REQUEST_SEND_SMS = 3;

    // Location service broadcast
    public static final String BROADCAST_LOCATION_UPDATE = "ims.fhj.at.emergencyalerter.LOCATION_UPDATE";

    public static final String EXTRA_LATITUDE = "lat";
    public static final String EXTRA_LONGITUDE = "lng";

    public final static double DEMO_LAT_GRAZ = 47.090637;
    public final static double DEMO_LONG_GRAZ = 15.4169279;

}
