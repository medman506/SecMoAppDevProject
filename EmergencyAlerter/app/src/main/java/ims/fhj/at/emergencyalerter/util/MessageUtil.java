package ims.fhj.at.emergencyalerter.util;


import android.content.Context;
import android.location.Location;
import android.telephony.SmsManager;

import java.util.ArrayList;

import ims.fhj.at.emergencyalerter.R;
import ims.fhj.at.emergencyalerter.model.Contact;

/**
 * Created by mayerhfl13 on 06.01.2017.
 */

public class MessageUtil {

    private static MessageUtil mInstance = null;
    private SmsManager smsManager;
    private Context ctx;

    public static MessageUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MessageUtil(context);
        }
        return mInstance;
    }

    private MessageUtil(Context ctx) {
        smsManager = SmsManager.getDefault();
        this.ctx = ctx;
    }

    private String buildEmergencyMessage(Location location) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(ctx.getApplicationContext().getString(R.string.emergency_message));
        messageBuilder.append(ctx.getApplicationContext().getString(R.string.emergency_message_location));
        messageBuilder.append("Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
        messageBuilder.append("\nhttp://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude());
        return messageBuilder.toString();
    }

    public void sendEmergencyMessage(ArrayList<Contact> emergencyContacts, Location location) {
        for (Contact contact : emergencyContacts) {
            smsManager.sendTextMessage(contact.getTelephoneNumber(), null, buildEmergencyMessage(location), null, null);
        }
    }
}
