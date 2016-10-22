package com.master.uberclone;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class MyLoction implements LocationListener {

    LocationManager locationManager;

    @SuppressWarnings({"MissingPermission"})
    public MyLoction(LocationManager locationManager) {
        this.locationManager = locationManager;
        if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
            onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        } else if (locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
            onLocationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        }
        startListening();
    }

    @SuppressWarnings({"MissingPermission"})
    public void startListening() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //   Toast.makeText(getApplicationContext(), "Location Provided By GPS", Toast.LENGTH_SHORT).show();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //  Toast.makeText(getApplicationContext(), "Location Provided By Network", Toast.LENGTH_SHORT).show();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        CustomerMap.ChangeMapLocation(location);
        DriverMap.ChangeMapLocation(location);
        ReqList.location = location;
    }

    @SuppressWarnings({"MissingPermission"})
    public void removeUpdates() {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        startListening();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
