package com.example.placeits;

import java.util.Calendar;
import java.util.GregorianCalendar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import Database.MainDataSource;

/* ViewPlaceIt displays the information of a regular PlaceIt and allows
 * the user to toggle its status or delete it.
 */
public class ViewPlaceItActivity extends Activity
{	
	private Button setActiveButton;
	private Button setInactive;
	private Button deleteButton;
	private PlaceIt placeIt;
	private MainDataSource dataSource = MainDataSource.getInstance(this);
	
	// displays the visual elements of this activity
	protected void onCreate(Bundle savedInstanceState)
	{	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_place_it);
        
		Intent i = getIntent();
        PlaceIt place_it = (PlaceIt) i.getParcelableExtra("placeIt");
        placeIt = dataSource.findByLatLong(place_it.getLatitude(), place_it.getLongitude());
        
        setUpUIElements();
	}
	
	//listener for the setActive, setInactive, and Delete buttons
	private View.OnClickListener onClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch(v.getId())
			{
				case R.id.setActiveBtn:
					placeIt.setStatus("ACTIVE");
					dataSource.editById(placeIt);
					finish();
				break;
			
				case R.id.setInactiveBtn:
					placeIt.setStatus("INACTIVE");
					Calendar gc  = new GregorianCalendar();
					gc.set(Calendar.HOUR_OF_DAY, 0);
					gc.set(Calendar.SECOND, 0);
					gc.set(Calendar.MINUTE, 0);
					gc.set(Calendar.DATE,0);
					placeIt.setInactiveDateTime(gc);
					dataSource.editById(placeIt);
					finish();	
				break;
				
				case R.id.deleteBtn:
					System.out.println("ViewPlaceItActivity::Delete this id: " + placeIt.getPlaceItId());
					dataSource.deleteById(placeIt.getPlaceItId());
					finish();
				break;
			}
			MainActivity.setReturnView(placeIt);
		}
	};

	// sets up the buttons and the text views
	private void setUpUIElements()
	{
		TextView title_content;
		TextView description_content;
		TextView show_schedule;
		String schedule;
		CheckBox check_box;
		
		setActiveButton = (Button)findViewById(R.id.setActiveBtn);
	    setActiveButton.setOnClickListener(onClickListener);
	    setInactive = (Button)findViewById(R.id.setInactiveBtn);
	    setInactive.setOnClickListener(onClickListener);
	    deleteButton = (Button)findViewById(R.id.deleteBtn);
	    deleteButton.setOnClickListener(onClickListener);
	    title_content = (TextView)findViewById(R.id.titleContent);
	    description_content = (TextView)findViewById(R.id.descriptionContent);
	    show_schedule = (TextView)findViewById(R.id.showSchedule);
	    check_box = (CheckBox)findViewById(R.id.checkBox1);
	    
	    title_content.setText(placeIt.getTitle());
	    description_content.setText(placeIt.getDescription());
	    schedule = "";
	    if (placeIt.getSchedule() != 0) 
	    	schedule += placeIt.getSchedule();
	    show_schedule.setText(schedule);
	    if (placeIt.getOnSchedule() == 1) 
	    	check_box.setChecked(true);
	    else
	    	check_box.setChecked(false);
	}
}