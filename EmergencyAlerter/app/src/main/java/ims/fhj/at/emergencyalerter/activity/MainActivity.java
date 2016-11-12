package ims.fhj.at.emergencyalerter.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import ims.fhj.at.emergencyalerter.R;

public class MainActivity extends AppCompatActivity {

    private Button btnToMapActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // button to navigate to map activity
        btnToMapActivity = (Button) findViewById(R.id.to_map_activity);
        btnToMapActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapActivityIntent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(mapActivityIntent);
            }
        });
    }
}
