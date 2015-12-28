package de.tobiaserthal.akgbensheim.backend.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class SyncService extends Service {
    private static SyncAdapter syncAdapter;
    private static final Object syncAdapterLock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (syncAdapterLock) {
            if (syncAdapter == null) {
                syncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    /**
     * Return an object that allows the system to invoke
     * the sync adapter.
     */
    @Override
    public IBinder onBind(Intent intent) {
        /*
         * Get the object that allows external processes
         * to call onPerformSync(). The object is created
         * in the base class code when the SyncAdapter
         * constructors call super()
         */
        return syncAdapter.getSyncAdapterBinder();
    }
}
