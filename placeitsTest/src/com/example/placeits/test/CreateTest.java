package com.example.placeits.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.placeits.CreatePlaceItActivity;
import com.example.placeits.R;
import com.example.placeits.PlaceIt;



public class CreateTest extends ActivityInstrumentationTestCase2<CreatePlaceItActivity> {

	private CreatePlaceItActivity create;
	private static final String TITLE = "title";
	private static final String TEXT = "hello world";
	
	public CreateTest() {
		super(CreatePlaceItActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		create = getActivity();
	}

	protected void tearDown() throws Exception {
		create.finish();
		super.tearDown();
	}

	/*
	Scenario: User clicks on empty location
	Given the map is displayed and there is no Place-It at the clicked location
	When the user taps on screen with finger
	Then the user is taken to a new screen with data fields that must be filled in by the user
	When the user clicks on the editable text box for Title
	Then a virtual keyboard is brought up, and the user can enter text
	When the user has entered at least one character in the title text box
	Then the ¡°Place It¡± button becomes clickable
	When the user clicks on the editable/scrollable text box for Description
	Then a virtual keyboard is brought up, and the user can enter text
	When the user clicks on the check box for ¡°On Schedule¡±,
	Then the frequency drop-down menu becomes active
	When the frequency drop-down menu is active and the user clicks it
	Then the appropriate rescheduling options appear and can be selected.
	
	Check for whether all the UIs are correctly displayed on screen.
	Also check for the buttons are clickable.
	
	BDD Test: Given user clicks the map
	When create placeit screen is displayed
	Then all the text boxes, buttons, check box, drop-down menu is properly displayed
	And user is able to type title and description on text boxes
	And user can click the placeit/cancel button.
	*/
	
	public void testCreateDisplays() {
		assertNotNull(create);
		assertNull(create.findViewById(0));
		final EditText title = (EditText) create.findViewById(R.id.editTitle);
		final EditText description = (EditText) create.findViewById(R.id.editDescriptionView);
		ViewAsserts.assertOnScreen(create.getWindow().getDecorView(),title);
		ViewAsserts.assertOnScreen(create.getWindow().getDecorView(), description);
		final Button placeit = (Button) create.findViewById(R.id.placeItBtn);
		final Button cancel = (Button) create.findViewById(R.id.cancelBtn);
		ViewAsserts.assertOnScreen(create.getWindow().getDecorView(),placeit);
		ViewAsserts.assertOnScreen(create.getWindow().getDecorView(),cancel);
		Spinner spinner = (Spinner) create.findViewById(R.id.schedule_spinner);
		ViewAsserts.assertOnScreen(create.getWindow().getDecorView(),spinner);
		CheckBox checkbox = (CheckBox) create.findViewById(R.id.checkBox1);
		ViewAsserts.assertOnScreen(create.getWindow().getDecorView(),checkbox);

		create.runOnUiThread(new Runnable() {
			public void run() {
				title.setText(TITLE, TextView.BufferType.EDITABLE);
				description.setText(TEXT, TextView.BufferType.EDITABLE);
				assertEquals(TITLE, title.getText().toString());
				assertEquals(TEXT, description.getText().toString());
				assertEquals(placeit.isClickable(),true);
				assertEquals(cancel.isClickable(),true);
			}
		});
	}
	
	/*
	Scenario: User creates a Place-It
	Given the map is displayed and there is no Place-It
	When the user fills out text fields and clicks Place It button
	Then a marker is placed on the selected location on map
	
	BDD Test: Given the map is displayed and there is no placeit
	When the user clicks certain location on the map
	Then user need to either type title or type both title and description
	And then user can create placeit with typed information.
	 */
	
	public void testCreatePlaceIts() {
		PlaceIt testp1 = new PlaceIt();
		PlaceIt testp2 = new PlaceIt();
		PlaceIt testp3 = new PlaceIt();
		
		testp1.setLatitude(32.8810);
		testp1.setLongitude(117.2380);
		testp2.setLatitude(37.8700);
		testp2.setLongitude(122.2590);
		testp3.setLatitude(34.0722);
		testp3.setLongitude(118.4441);
		
		testp1.setTitle("test1");
		testp1.setDescription("hello ucsd");
		testp2.setTitle("test2");
		
		assertNotNull(testp1.getPlaceItId());
		assertNotNull(testp2.getPlaceItId());
		assertEquals(testp3.getPlaceItId(),-1);
		assertEquals(testp1.getTitle(),"test1");
		assertEquals(testp1.getDescription(),"hello ucsd");
		assertEquals(testp2.getTitle(),"test2");
		assertNull(testp3.getTitle());
		assertEquals(testp1.getLatitude(),32.8810);
		assertEquals(testp2.getLongitude(),122.2590);
	}

}