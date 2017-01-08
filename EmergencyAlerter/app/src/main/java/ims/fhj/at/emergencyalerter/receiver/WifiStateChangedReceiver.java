package ims.fhj.at.emergencyalerter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import ims.fhj.at.emergencyalerter.util.App;

/**
 * Created by michael.stifter on 08.01.2017.
 */

public class WifiStateChangedReceiver extends BroadcastReceiver {

    public static final String TAG = WifiStateChangedReceiver.class.getSimpleName();

    private WifiStateChangedListener listener;

    public void setListener(WifiStateChangedListener listener) {
        this.listener = listener;
    }

    public interface WifiStateChangedListener {
        void onWifiChanged(String ssid);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(App.BROADCAST_WIFI_CHANGED)) {
            WifiManager wifiManager;
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState()) == NetworkInfo.DetailedState.CONNECTED) {
                if (listener != null) {
                    listener.onWifiChanged(wifiInfo.getSSID());
                }
            }
        }
    }
}