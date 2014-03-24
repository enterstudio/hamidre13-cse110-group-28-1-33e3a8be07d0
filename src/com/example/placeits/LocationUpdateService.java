package com.example.placeits;


import java.util.ArrayList;
import java.util.List;


import com.google.android.gms.maps.model.LatLng;

import Database.MainDataSource;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;


/*
 * LocationUpdateService runs in the background and queries the Database
 * every time the user location changes and creates a Notification if
 * the user is within 1 KM of a placeIt.
 */
public class LocationUpdateService extends Service implements LocationListener
{	
	//Instance vars
	private Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	protected MainDataSource dataSource = MainDataSource.getInstance(this);
	private int notificationId = 0;
	private final int TIME_SENSITIVITY = 30000; // milliseconds
	private final int DISTANCE_SENSITIVITY = -1;  // meters. if negative, then distance is not used, only the time.
	private final int ALERT_DISTANCE = 1000; // meters
	private ArrayList<PlaceIt> triggeredPlaceIts;
	
	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}
	
	/*
	 * Gets called when user location changes and searches all active and category
	 * placeIts
	 */
	@Override
	public void onLocationChanged(Location currentLoc)
	{	
		triggeredPlaceIts = new ArrayList<PlaceIt>();
		float[] results = new float[3];
		List<PlaceIt> marker_list  = dataSource.findByStatus("ACTIVE");
		if(marker_list.size() > 0 )
     	{
			for (PlaceIt placeIt : marker_list)
			{	
				if (placeIt.isCategory()) {
					System.out.println("Cat1: " + placeIt.getCategory1());
					System.out.println("Cat2: " + placeIt.getCategory2());
					System.out.println("Cat3: " + placeIt.getCategory3());
					if (placeIt.getCategory1() != "")
						new CheckPlaces(placeIt.getCategory1().toLowerCase().replace(" ", "_"), currentLoc, placeIt).execute();
					if (placeIt.getCategory2()!= "")
						new CheckPlaces(placeIt.getCategory2().toLowerCase().replace(" ", "_"), currentLoc, placeIt).execute();
					if (placeIt.getCategory3()!= "")
						new CheckPlaces(placeIt.getCategory3().toLowerCase().replace(" ", "_"), currentLoc, placeIt).execute();
				}
				else {
					Location.distanceBetween(currentLoc.getLatitude(), currentLoc.getLongitude(), placeIt.getLatitude(), placeIt.getLongitude(), results);
					if (results[0] < ALERT_DISTANCE) { // results[0] holds the computed distance between the Place It and yourself
						triggeredPlaceIts.add(placeIt);
						makeNotification();
					}
				}
			}	
		}
	}
	
	// Creates a notification and displays it on System tray 
	void makeNotification() {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.note_icon)
		.setContentTitle("ALERT!")
		.setContentText("PlaceIt(s) Nearby")
		.setAutoCancel(true)
		.setSound(alarmSound);
	Intent baseIntent = new Intent(this, MainActivity.class);
	Intent topIntent = new Intent(this, TriggeredPlaceItsActivity.class);
	Bundle bun = new Bundle();
	System.out.println("Location Update: " + triggeredPlaceIts.get(0).getLocale());
	bun.putParcelableArrayList("triggeredPlaceIts", triggeredPlaceIts);
	topIntent.putExtras(bun);
	TaskStackBuilder stack_builder = TaskStackBuilder.create(this);
	stack_builder.addNextIntent(baseIntent);
	stack_builder.addNextIntent(topIntent);
	PendingIntent pending_intent = stack_builder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
	builder.setContentIntent(pending_intent);
	NotificationManager notification_manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	notification_manager.notify(notificationId,builder.build());	
	}
	
	// Checks if there are locales of a certain category nearby (1 KM)
	private class CheckPlaces extends AsyncTask<Void, Void, PlaceIt> {

		private ProgressDialog dialog;
		private String places;
		private Location loc;
		private PlaceIt refPlaceIt;

		public CheckPlaces(String places, Location currLoc, PlaceIt ref) {
			this.places = places;
			loc = currLoc;
			refPlaceIt = ref;
		}

		@Override
		protected void onPostExecute(PlaceIt p) {
			super.onPostExecute(p);
			if (p.getTitle() != "") {
				System.out.println("onPostExecute: " + p.getLocale());
				triggeredPlaceIts.add(p);
				makeNotification();
			}
				
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected PlaceIt doInBackground(Void... arg0) {
			PlaceIt p = new PlaceIt();
			PlacesService service = new PlacesService(
					"AIzaSyDBqywkcO851C208X05foqaMpQ1bi_xoOc");
			ArrayList<PlaceIt> findPlaces = service.findPlaces(loc.getLatitude(), loc.getLongitude(), places, refPlaceIt);
			if (findPlaces.size() != 0) {
				System.out.println(" doInBG: " + findPlaces.get(0).getLocale());
				p = findPlaces.get(0);
			}
			else 
				p.setTitle("");
			return p;
		}

	}
	
	@Override
	// Sets up the providers from which to get location information
    public void onCreate()
	{
		LocationManager location_manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_SENSITIVITY, DISTANCE_SENSITIVITY, (android.location.LocationListener) this);
		location_manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_SENSITIVITY, DISTANCE_SENSITIVITY, (android.location.LocationListener) this);
    }
	
	@Override
	public void onProviderDisabled(String provider) {}
	
	@Override
	public void onProviderEnabled(String provider) {}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

}