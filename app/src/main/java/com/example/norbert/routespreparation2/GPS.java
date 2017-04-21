package com.example.norbert.routespreparation2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class GPS implements LocationListener {

    Context context;

    Location previousLoc = null;
    float distance = 0;
    boolean firstTime = true;
    public double dis;

    public GPS(Context c) {
        context = c;
    }

    public Location getlocation() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Uprawnienia nie przyznane", Toast.LENGTH_SHORT).show();
        }

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSenabled;
        isGPSenabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isGPSenabled) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return location;
        } else {
            Toast.makeText(context, "Proszę włączyć GPS", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public float getDistance() {
        return distance;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(firstTime){
            previousLoc = location;
            firstTime = false;
        }
        distance += previousLoc.distanceTo(location);
        previousLoc = location;
        Toast.makeText(context, String.valueOf(distance), Toast.LENGTH_SHORT).show();
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

    public double getDis() {
        return dis;
    }

    public void setDis(double dis) {
        this.dis = dis;
    }
}