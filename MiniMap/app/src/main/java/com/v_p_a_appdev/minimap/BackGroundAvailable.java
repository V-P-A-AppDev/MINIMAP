package com.v_p_a_appdev.minimap;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BackGroundAvailable extends Service {
    public BackGroundAvailable() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}