package ims.fhj.at.emergencyalerter;

import android.app.Application;

import ims.fhj.at.emergencyalerter.activity.MainActivity;

/**
 * Created by michael.stifter on 07.12.2016.
 */

public class EmergencyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public MainActivity mainActivity;
}
