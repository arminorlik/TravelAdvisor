package com.example.norbert.routespreparation2;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btnhistory)
    Button btnhistory;
    @BindView(R.id.btnStart)
    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);
    }

    @OnClick(R.id.btnhistory)
    public void btnHistory() {
        Intent intent = new Intent(MainActivity.this, ListDataActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnStart)
    public void btnStart() {
        GPStracker gpStracker = new GPStracker(getApplicationContext());
        Location location = gpStracker.getlocation();
        if (location != null) {
            Double lat = location.getLatitude();
            Double lon = location.getLongitude();
            //Toast.makeText(getApplicationContext(), "LAT: " + lat + " \n LON: " + lon, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra("dlugosc", lon.toString());
            intent.putExtra("szerokosc", lat.toString());
            startActivity(intent);
        }
    }
}
