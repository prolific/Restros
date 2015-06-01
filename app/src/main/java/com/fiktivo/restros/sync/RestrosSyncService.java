package com.fiktivo.restros.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RestrosSyncService extends Service {

    private static final Object syncAdapterLock = new Object();
    private static RestrosSyncAdapter restrosSyncAdapter = null;
    public static final String SYNC_FIINISHED = "com.fiktivo.restros.SYNC_FINISHED";

    @Override
    public void onCreate() {
        synchronized (syncAdapterLock) {
            if (restrosSyncAdapter == null)
                restrosSyncAdapter = new RestrosSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return restrosSyncAdapter.getSyncAdapterBinder();
    }
}
