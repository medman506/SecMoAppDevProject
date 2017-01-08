package ims.fhj.at.emergencyalerter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(App.BROADCAST_NETWORK_CHANGED)) {
            Bundle bundle = intent.getExtras();

            SmsMessage[] msgs = null;

            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        String from = msgs[i].getOriginatingAddress();
                        long timestamp = msgs[i].getTimestampMillis();
                        String body = msgs[i].getMessageBody();

                        if (listener != null) {
                            listener.onNetworkChanged(from, timestamp, body);
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        }
    }
}
