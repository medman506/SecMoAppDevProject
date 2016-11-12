package ims.fhj.at.emergencyalerter.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import ims.fhj.at.emergencyalerter.R;
import ims.fhj.at.emergencyalerter.api.HttpGetAsyncTask;
import ims.fhj.at.emergencyalerter.api.PlacesJsonParser;
import ims.fhj.at.emergencyalerter.util.App;

public class MapActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    public static String TAG = "MapActivity";

    // proximity radius for Google Places API
    private static double PROXIMITY_RADIUS = 5000;

    private static double DEMO_LAT = 47.090637;
    private static double DEMO_LONG = 15.4169279;

    private GoogleMap googleMap = null;

    private GoogleApiClient mGoogleApiClient;

    // hash map to store API response
    private List<HashMap<String, String>> googlePlacesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set action bar title
        getSupportActionBar().setTitle(getResources().getString(R.string.nearby_police_stations));

        // map fragment
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // set up Google API client
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connection to Google Places API established");

        searchPoliceStationsNearby();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection to Google Places API suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection to Google Places API failed");
        Toast.makeText(this, "Unfortunately, we were not able to establish a connection with Google Places", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case App.PERMISSION_REQUEST_FINE_LOCATION:
                // if request was cancelled, all the arrays are empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, we can continue on
                    searchPoliceStationsNearbyPermissionGranted();
                } else {
                    // permission was denied, we may not be able to continue as planned
                    searchPoliceStationsNearbyPermissionDenied();
                }
                break;
        }
    }

    private void searchPoliceStationsNearby() {
        Log.d(TAG, "Starting to search for police stations nearby");

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                 // TODO show an explanation?
            } else {
                // no explanation needed, we can request the permission
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, App.PERMISSION_REQUEST_FINE_LOCATION);
            }
        } else {
            searchPoliceStationsNearbyPermissionGranted();
        }
    }

    private void searchPoliceStationsNearbyPermissionGranted() {
        Log.d(TAG, "now we really search for police stations nearby");

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + DEMO_LAT + "," + DEMO_LONG);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=" + App.GOOGLE_PLACES_QUERY_TYPE);
        googlePlacesUrl.append("&sensors=true");
        googlePlacesUrl.append("&key=" + App.GOOGLE_API_KEY);

        HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask(this, googlePlacesUrl.toString(), new HttpGetAsyncTask.OnTaskDoneListener() {
            @Override
            public void onTaskDone(String response) {
                // parse JSON
                PlacesJsonParser placesJsonParser = new PlacesJsonParser();
                List<HashMap<String, String>> placesData = null;

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    placesData = placesJsonParser.parse(jsonResponse);

                    // display markers on map
                    displayMarkersOnMap(placesData);

                    // zoom camera in
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(DEMO_LAT, DEMO_LONG), 12.0f));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                Log.e(TAG, "retrieve error");
            }
        });

        httpGetAsyncTask.execute("GO!");
    }

    private void searchPoliceStationsNearbyPermissionDenied() {
        Toast.makeText(this, "Unfortunately, we are not able to show you nearby police stations if you do not permit us to access your location", Toast.LENGTH_LONG).show();
    }

    private void displayMarkersOnMap(List<HashMap<String, String>> mapData) {
        for (HashMap<String, String> item : mapData) {
            MarkerOptions markerOptions = new MarkerOptions();

            try {
                double lat = Double.parseDouble(item.get("lat"));
                double lng = Double.parseDouble(item.get("lng"));
                String placeName = item.get("place_name");
                String vicinity = item.get("vicinity");

                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName);

                if (googleMap != null) {
                    googleMap.addMarker(markerOptions);
                }
            } catch (Exception e) {
                Log.d(TAG, "failed to parse: " + item.get("place_name"));
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
    }
}
