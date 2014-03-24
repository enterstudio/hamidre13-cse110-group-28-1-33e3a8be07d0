package com.example.placeits;

import java.util.Calendar;
import java.util.TimeZone;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import Database.MainDataSource;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/*
 * MainActivity is the home activity, this directs to user to the appropriate
 * activities based on their actions and displays the map and their active PlaceIts.
 */
public class MainActivity extends Activity implements OnMapClickListener, OnMarkerClickListener
{
	//Instance variables
    private GoogleMap googleMap; 
    private Button active, inactive, createCategoryPlaceItButton;
    protected MainDataSource dataSource = MainDataSource.getInstance(this);
    private MarkerManager markerManager;
    private static CameraUpdate newCam;
    private static boolean panManually = false;
    
    // Sets up the buttons and the map (along with active PlaceIts)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
    	dataSource.open();
        
    	Intent intent = new Intent(this, LocationUpdateService.class);
		startService(intent);
		setRecurringAlarm(this.getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        active = (Button) findViewById(R.id.active_list);
        active.setOnClickListener(onClickListener);
        inactive = (Button) findViewById(R.id.inactive_list);
        inactive.setOnClickListener(onClickListener);
        createCategoryPlaceItButton = (Button) findViewById(R.id.create_category_place_it);
        createCategoryPlaceItButton.setOnClickListener(onClickListener);
 
        try
        {
            initializeMap();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        markerManager = new MarkerManager(googleMap, dataSource);
        dataSource.addObserver(markerManager);
    }
 
    public void newMapMake() {
    	googleMap = null;
    	initializeMap();
    }
    private void initializeMap()
    {
        if (googleMap == null)
        {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            markerManager.populateMap(googleMap);
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        if (panManually) {
        	googleMap.animateCamera(newCam);
        	panManually = false;
        }
    }

 
    @Override
    protected void onResume()
    {
        super.onResume();
        initializeMap();
        dataSource.open();    	
    }

    // Listener for mapClicks, sets the location for a prospective PlaceIt
	@Override
	public void onMapClick(LatLng point)
	{
		PlaceIt p = new PlaceIt(point);
		Intent intent = new Intent(this, CreatePlaceItActivity.class);
		intent.putExtra("placeIt", (Parcelable) p);
		startActivity(intent);
	}

	// Listener for user clicking on an active PlaceIt's marker
	@Override
	public boolean onMarkerClick(Marker m)
	{ 
		PlaceIt p = new PlaceIt(m.getPosition());
		Intent intent = new Intent(this, ViewPlaceItActivity.class);
		intent.putExtra("placeIt", (Parcelable) p);
		startActivity(intent);
		return false; // marker will be at the center of the screen
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
	}
	
	// Listener for the list buttons
	private View.OnClickListener onClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch(v.getId())
			{
				case R.id.active_list:
					if(dataSource.findByStatus("ACTIVE").size() > 0)
						showActiveList();
					else
						Toast.makeText(getApplicationContext(), "Nothing To show", Toast.LENGTH_LONG).show();
				break;
			
				case R.id.inactive_list:
					if(dataSource.findByStatus("INACTIVE").size() > 0)
						showInactiveList();
					else
						Toast.makeText(getApplicationContext(), "Nothing To show", Toast.LENGTH_LONG).show();
				break;
				
				case R.id.create_category_place_it:
				goToCreateCategoryPlaceItActivity();
			}
		}	
	};
	
	/*
	 * The following create intents for the user to go to the correct Activity
	 */
	private void goToCreateCategoryPlaceItActivity()
	{
		Intent intent = new Intent(this, CreateCategoryPlaceItActivity.class);
		startActivity(intent);
	}

	private void showActiveList()
	{
		Intent intent = new Intent(this, ActiveListActivity.class);
		startActivity(intent);
	}
	
	private void showInactiveList()
	{
		Intent intent = new Intent(this, InactiveListActivity.class);
		startActivity(intent);
	}
	
	/*
	 *  Sets a recurring alarm in order to query the time and check when scheduled
	 *  PlaceIts should change status.
	 */
	
	private void setRecurringAlarm(Context context)
	{
	    Calendar update_time = Calendar.getInstance();
	    update_time.setTimeZone(TimeZone.getTimeZone("GMT"));
	    update_time.set(Calendar.HOUR_OF_DAY, 12);
	    Intent schedule_intent = new Intent(context, AlarmReceiver.class);
	    PendingIntent recurring_schedule_intent = PendingIntent.getBroadcast(context,
	            0, schedule_intent , PendingIntent.FLAG_CANCEL_CURRENT);

	    AlarmManager alarms = (AlarmManager) this.getSystemService(
	            Context.ALARM_SERVICE);
	    alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
	            update_time.getTimeInMillis(),
	            AlarmManager.INTERVAL_DAY, recurring_schedule_intent);
	}
	
	// sets up a camera location and zoom to pan to when returning to this activity
	public static void setReturnView(PlaceIt p) {
		LatLng zoomLoc = new LatLng(p.getLatitude(), p.getLongitude());
		newCam = CameraUpdateFactory.newLatLngZoom(zoomLoc, 15);
		panManually = true;
	}
}
