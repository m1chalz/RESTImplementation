package com.example.rest.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncService extends Service {

	private static final Object syncAdapterLock = new Object();
	private static SyncAdapter syncAdapter = null;

	@Override
	public void onCreate() {
		super.onCreate();
		synchronized (syncAdapterLock) {
			if (syncAdapter == null) {
				syncAdapter = new SyncAdapter(getApplicationContext(), true);
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return syncAdapter.getSyncAdapterBinder();
	}
}
