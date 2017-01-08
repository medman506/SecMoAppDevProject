package ims.fhj.at.emergencyalerter.api;

import android.content.Context;
import android.util.Log;

/**
 * Created by michael.stifter on 08.01.2017.
 */

public class PoliceStationRetriever {

    public static final String TAG = PoliceStationRetriever.class.getSimpleName();

    private Context context;

    public PoliceStationRetriever(Context context) {
        this.context = context;
    }

    public void retrieveNearbyPoliceStations(String lat, String lng, long radius) {
        StringBuilder apiUrl = new StringBuilder("http://service.micsti.at/getPoliceStations.php")
                .append("?radius=" + radius)
                .append("&lat=" + lat)
                .append("&lng=" + lng);

        HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask(context, apiUrl.toString(), new HttpGetAsyncTask.OnTaskDoneListener() {
            @Override
            public void onTaskDone(String response) {
                Log.d(TAG, "retrieved police stations");
            }

            @Override
            public void onError() {
                Log.e(TAG, "retrieve error");
            }
        });
    }
}
