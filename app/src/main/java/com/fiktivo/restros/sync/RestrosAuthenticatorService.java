package com.fiktivo.restros.sync;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RestrosAuthenticatorService extends Service {

    private RestrosAuthenticator authenticator;

    @Override
    public void onCreate() {
        authenticator = new RestrosAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
