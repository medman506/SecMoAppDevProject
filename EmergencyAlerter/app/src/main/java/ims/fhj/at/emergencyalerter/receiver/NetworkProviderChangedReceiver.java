package ims.fhj.at.emergencyalerter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import ims.fhj.at.emergencyalerter.util.App;

/**
 * Created by michael.stifter on 08.01.2017.
 */

public class NetworkProviderChangedReceiver extends BroadcastReceiver {

    public static final String TAG = NetworkProviderChangedReceiver.class.getSimpleName();

    private NetworkProviderChangedListener listener;

    public void setListener(NetworkProviderChangedListener listener) {
        this.listener = listener;
    }

    public interface NetworkProviderChangedListener {
        void onProviderChanged(String name);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(App.BROADCAST_NETWORK_PROVIDER_CHANGED)) {
            String name = intent.getStringExtra("provider.name");

            if (name != null && listener != null) {
                listener.onProviderChanged(name);
            }
        }
    }
}