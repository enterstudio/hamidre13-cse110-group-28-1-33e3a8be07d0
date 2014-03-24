package com.example.placeits.test;

import java.util.List;

import Database.MainDataSource;
import android.test.ActivityInstrumentationTestCase2;

import com.example.placeits.MainActivity;
import com.example.placeits.R;
import com.example.placeits.PlaceIt;

public class MainViewTest extends ActivityInstrumentationTestCase2<MainActivity> {

private MainActivity activity;
	
	public MainViewTest() {
		super(MainActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
	}

	protected void tearDown() throws Exception {
		activity.finish();
		super.tearDown();
	}
	
	/* Scenario 1: User drags map
	Given map is being displayed
	When user drags finger across screen 
	Then the map pans in the direction of the finger drag

	Scenario 2: User zooms in/out of map
	Given map is being displayed
	When user pinches the screen
	Then the map zooms in/out accordingly

	Scenario 3: User is at the center of map
	Given the map is being displayed
	When the apps is opened or
	When user touches button
	Then the user location is centered on the map
	
	Since the googleMap is offering all the features above
	we only tests whether map is displayed when start app.
	
	BDD Test: Given user opens placeits app
	When application starts
	Then map is properly displayed with drag, zoom, centered features
	 */
	public void testMapShowing() {
		assertNotNull(activity);
		assertNull(activity.findViewById(0));
		assertNotNull(activity.findViewById(R.id.map));
	}
	
	/*
	Scenario: User clicks on existing Place-It and clicks Inactive button
	Given the map is displayed and there is a Place-It at the clicked location
	When the user taps on the marker with finger,
	Then a new window appears with the Place-It details displayed
	When the user is finished viewing the Place-It and clicks Inactive button
	Then the marker on the map for the Place-It is hidden
	And the Place-It is placed into the Inactive list
	And the app returns to the window displaying the map and lists.
	
	BDD Test: Given user is viewing placeit details
	When user clicks inactive button
	Then the placeit is now inactived and is added to inactive list.
	*/	
	public void testChangeStatus() {
		
		MainDataSource db = MainDataSource.getInstance(activity);
		PlaceIt testp1 = new PlaceIt();
		
		testp1.setLatitude(32.8810);
		testp1.setLongitude(117.2380);
		
		testp1.setTitle("test1");
		testp1.setDescription("hello ucsd");	
		
		
		List<PlaceIt> list = db.findByStatus("INACTIVE");
		int oldsize = list.size();
		testp1.setStatus("INACTIVE");
		
		db.create(testp1);
		
		list = db.findByStatus("INACTIVE");
		assertEquals(list.size(),oldsize+1);
		
	}
	
	/*
	Scenario: User opens the Place-Its app
	Given the list is shown on the bottom-left side on the map
	When the user selects Active Place-Its lists
	Then the Active Place-Its from the map are listed
	When the user clicked the title from the Active Place-Its lists,
	Then the Place-It details are displayed as if the Place-It was clicked on the map
	
	BDD Test: Given user createes two placeits
	When the user make placeit1 to inactive and placeit2 to active
	Then placeit1 is added to inactive placeit lists
	And placeit2 is added to active placeit lists.
	*/
	public void testLists() {
		
		MainDataSource db = MainDataSource.getInstance(activity);
		
		PlaceIt testp1 = new PlaceIt();
		PlaceIt testp2 = new PlaceIt();
		
		testp1.setLatitude(32.8810);
		testp1.setLongitude(117.2380);
		testp2.setLatitude(37.8700);
		testp2.setLongitude(122.2590);
		
		testp1.setTitle("test1");
		testp2.setTitle("test2");
		
		List<PlaceIt> inlist = db.findByStatus("INACTIVE");
		List<PlaceIt> alist = db.findByStatus("ACTIVE");
		int oldsizein = inlist.size();
		int oldsizea = alist.size();
		
		testp1.setStatus("INACTIVE");
		db.create(testp1);
		inlist = db.findByStatus("INACTIVE");
		assertEquals(inlist.size(),oldsizein+1);
		
		testp2.setStatus("ACTIVE");
		db.create(testp2);
		alist = db.findByStatus("ACTIVE");
		assertEquals(alist.size(),oldsizea+1);	
	}
	
	/*
	Scenario: User clicks on the check box for ¡°On Schedule¡± from the task details screen
	Given the frequency drop-down menu is active
	When the user selects a time interval and clicks OK
	Then the task is updated with new schedule
	
	BDD Test: Given user view exist placeit on certain location
	When user clicks check box for "On Schedule"
	And user select "1 Week" from drop-down menu
	Then the placeit is updated with new schedule, "1 Week."
	 */
	public void testChangeSchedule() {
		PlaceIt testp1 = new PlaceIt();
		
		testp1.setLatitude(32.8810);
		testp1.setLongitude(117.2380);
		
		testp1.setTitle("test1");
		
		testp1.setOnSchedule(1);
		testp1.setSchedule(1);
		assertEquals(testp1.getOnSchedule(),1);
		assertEquals(testp1.getSchedule(),1);
	}
}