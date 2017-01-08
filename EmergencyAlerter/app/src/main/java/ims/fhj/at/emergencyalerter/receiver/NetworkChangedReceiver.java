package ims.fhj.at.emergencyalerter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import ims.fhj.at.emergencyalerter.util.App;

/**
 * Created by michael.stifter on 08.01.2017.
 */

public class NetworkChangedReceiver extends BroadcastReceiver {

    public static final String TAG = NetworkChangedReceiver.class.getSimpleName();

    private NetworkChangedListener listener;

    public void setListener(NetworkChangedListener listener) {
        this.listener = listener;
    }

    public interface NetworkChangedListener {
        void onNetworkChanged(String name, long connectTime, String info);
        void onNetworkDown();
        void onNetworkUp();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(App.BROADCAST_NETWORK_CHANGED)) {
            Bundle bundle = intent.getExtras();

            boolean isNetworkDown = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if (isNetworkDown && listener != null) {
                listener.onNetworkDown();
            } else {
                listener.onNetworkUp();
            }

            if (bundle != null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");

                    if (pdus != null && pdus.length > 0) {

                        if (wifiInfo != null && mobileInfo != null) {
                            wifiInfo.getDetailedState();

                            if (wifiInfo.isAvailable() || mobileInfo.isAvailable()) {
                                // internet is available
                                Log.d(TAG, "Internet is available");
                            }
                        }

                        SmsMessage[] msgs = new SmsMessage[pdus.length];
                        for (int i = 0; i < msgs.length; i++) {
                            msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);

                            if (listener != null) {
                                listener.onNetworkChanged(msgs[i].getOriginatingAddress(), msgs[i].getTimestampMillis(), msgs[i].getMessageBody());
                            }
                        }

                        // check if device is connected to mobile or wifi network
                        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
                            Log.d(TAG, "Device is connected to internet");
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        }
    }
}
