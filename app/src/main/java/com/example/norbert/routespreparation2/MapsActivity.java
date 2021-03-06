package com.example.norbert.routespreparation2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.norbert.routespreparation2.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    @BindView(R.id.butStop)
    Button butStop;
    @BindView(R.id.butWynik)
    Button butKoniec;
    private GoogleMap mMap;
    private String dlugosc;
    private String szerokosc;
    private LatLng StartP, StopP, StartPP, StopPP;
    private Date czasS;
    private DatabaseHelper mDatabaseHelper;
    private String adres;
    private boolean oneStop = false;
    private GPStracker gpStracker;
    private Location location;
    public double dis;
    public ArrayList<LatLng> droga;
    public String[] trasa;
    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);

        mapFragment.getMapAsync(this);
        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        Intent intent = getIntent();
        gpStracker = new GPStracker(getApplicationContext());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Uprawnienia nie przyznane", Toast.LENGTH_SHORT).show();
        }
        LocationManager lm = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 3, this);
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        dlugosc = intent.getStringExtra("dlugosc");
        szerokosc = intent.getStringExtra("szerokosc");
        StartPP = new LatLng(location.getLatitude(), location.getLongitude());
        StopPP = new LatLng(location.getLatitude(), location.getLongitude());
        StartP = new LatLng(Double.parseDouble(szerokosc), Double.parseDouble(dlugosc));
        czasS = new Date();
        mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper.setStartP(getAdres(StartP));//wyciągnięcie adresu rozpoczęcia podróży
        droga = new ArrayList<LatLng>();
        trasa = new String[100000];
        trasa[0] = getAdres(StartP);
        droga.add(0, StartP);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addMarker(StartP);
    }

    private void addMarker(LatLng pozycja) {
        mMap.addMarker(new MarkerOptions().position(pozycja));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(pozycja)
                .zoom(10).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @OnClick(R.id.butStop)
    public void butStop() {
        if (oneStop == false) {
            //rysowanie przebytej trasy
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions
                    .addAll(droga)
                    .width(5)
                    .color(Color.RED);
            mMap.addPolyline(polylineOptions);

            oneStop = true;
            Location location = gpStracker.getlocation();
            if (location != null) {
                StopP = new LatLng(location.getLatitude(), location.getLongitude());
            }
            addMarker(StopP);
            String odleglosc = String.format("%.2f", dis);
            mDatabaseHelper.setKoniecP(getAdres(StopP)); //wyciągnięcie adresu zakonczenia podróży
            //wyciągnięcie odległości i czasu podróży
            mDatabaseHelper.setCzasP(odleglosc + " KM " + "AVS: " + WyliczAvs() + " KM/H" + " Time: " + WyliczCzas());
            mDatabaseHelper.setTrasaP(stringTrasy(trasa));
            mDatabaseHelper.setTravelLatLngString(travelLatLng());

            boolean insertData = mDatabaseHelper.addData();
        } else {
            toastMessage("Twoja podróż została już zakończona");
        }
    }

    @OnClick(R.id.butWynik)
    public void butKoniec() {
        if (oneStop == false) {
            toastMessage("Musisz zakończyć podróż wciskając STOP");
        } else {

            Intent intent = new Intent(MapsActivity.this, ListDataActivity.class);
            startActivity(intent);
        }
    }

    private int WyliczAvs() {
        long czasF = (new Date().getTime() - czasS.getTime());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(czasF);
        long Czas = (czasF / 1000);
        double diss = dis * 1000;//przeliczenie odległości na metry
        int avTimeS = (int) ((int) (diss / Czas) * 3.6);//obliczenie średniej prędkości i zamiana z m/s na km/h

        return avTimeS;
    }

    public String WyliczCzas() {
        long czasF = (new Date().getTime() - czasS.getTime());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(czasF);

        TimeZone UTC = TimeZone.getTimeZone("UTC");

        Date date = new Date(czasF);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(UTC);
        String dateFormatted = formatter.format(date);

        return dateFormatted;
    }

    public double ObliczanieOdl(LatLng StartP, LatLng StopP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = StopP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = StopP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));

        return km;
    }

    public void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String getAdres(LatLng pozycja) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(pozycja.latitude, pozycja.longitude, 1);
            adres = addresses.get(0).getAddressLine(0);
            //Toast.makeText(this, "Adres: " + adres, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return adres;
    }

    @Override
    public void onLocationChanged(Location location) {

        StartPP = new LatLng(location.getLatitude(), location.getLongitude());
        if (StartPP.latitude != StopPP.latitude || StartPP.longitude != StopPP.longitude) {
            dis += ObliczanieOdl(StartPP, StopPP);
            StopPP = new LatLng(StartPP.latitude, StartPP.longitude);
            droga.add(i, StartPP);
            trasa[i] = getAdres(StartPP);
            i++;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public String stringTrasy(String[] trasy) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i <= trasy.length && trasy[i] != null; i++) {
            stringBuilder.append(trasa[i].toString() + "|");
        }
        String stringTrasy = stringBuilder.toString();

        return stringTrasy;
    }

    public String travelLatLng() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int j = 0; j < droga.size(); j++) {
            stringBuilder.append(droga.get(j).toString() + "|");
        }
        String travelLatLngString = stringBuilder.toString();

        return travelLatLngString;
    }
}
