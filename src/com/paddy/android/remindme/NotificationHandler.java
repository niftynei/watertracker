package com.paddy.android.remindme;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.content.Context;

public class NotificationHandler extends Service {
	public static final String TAG = "NH";
	private WakeLock mWakeLock;
	public NotificationCreator notificationCreator = new NotificationCreator();
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.i("notificationHandler", "onBind");
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public void handleIntent(Intent intent) {
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		mWakeLock.acquire();
		
		ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		if (!cm.getBackgroundDataSetting()) {
			stopSelf();
			return;
		}
		new NotificationTask().execute();
	}
	
	private class NotificationTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... params) {
			Log.i("doInBackground", "called");
      startService(new Intent(NotificationHandler.this, NotificationCreator.class));
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			stopSelf();
		}
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleIntent(intent);
		return START_NOT_STICKY;
	}
	
	public void onDestroy() {
		super.onDestroy();
		mWakeLock.release();
	}

}
