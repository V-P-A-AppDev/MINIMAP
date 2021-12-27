package com.v_p_a_appdev.minimap.Utils;

import android.location.Location;

import com.google.android.gms.location.LocationRequest;

public class UserLocation {
    public Location lastLocation;
    public LocationRequest locationRequest;

    public void CreateLocationRequest() {
        locationRequest = new LocationRequest();
        //*Set an interval for 1 second.
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
}
