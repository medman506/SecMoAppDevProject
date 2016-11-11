package ims.fhj.at.emergencyalerter;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set title bar to a red color
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.flamingo)));
    }
}
