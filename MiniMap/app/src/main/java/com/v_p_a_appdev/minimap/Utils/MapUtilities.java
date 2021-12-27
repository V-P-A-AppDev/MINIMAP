package com.v_p_a_appdev.minimap.Utils;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class MapUtilities {
    public static final int LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    GoogleApiClient currentGoogleApiClient;
    SupportMapFragment mapFragment;

    public MapUtilities() {
    }

    public GoogleApiClient getCurrentGoogleApiClient() {
        return currentGoogleApiClient;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public SupportMapFragment getMapFragment() {
        return mapFragment;
    }

    public void setCurrentGoogleApiClient(GoogleApiClient currentGoogleApiClient) {
        this.currentGoogleApiClient = currentGoogleApiClient;
    }

    public void setMapFragment(SupportMapFragment mapFragment) {
        this.mapFragment = mapFragment;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }


}
