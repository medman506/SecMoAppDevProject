package ims.fhj.at.emergencyalerter.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by michael.stifter on 11.11.2016.
 */

public class HttpGetAsyncTask extends AsyncTask<String, Integer, String> {

    private static String TAG = "HttpGetAsyncTask";

    private Context context;
    private OnTaskDoneListener onTaskDoneListener;
    private String urlString = "";

    public HttpGetAsyncTask(Context context, String url, OnTaskDoneListener onTaskDoneListener) {
        this.context = context;
        this.urlString = url;
        this.onTaskDoneListener = onTaskDoneListener;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-length", "0");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setAllowUserInteraction(false);
            httpURLConnection.setConnectTimeout(100000);
            httpURLConnection.setReadTimeout(100000);

            httpURLConnection.connect();

            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }

                reader.close();

                return builder.toString();
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }

                reader.close();

                Log.e(TAG, "Error response: " + builder.toString());

                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (onTaskDoneListener != null && s != null) {
            onTaskDoneListener.onTaskDone(s);
        } else {
            onTaskDoneListener.onError();
        }
    }

    public interface OnTaskDoneListener {
        void onTaskDone(String response);
        void onError();
    }
}
