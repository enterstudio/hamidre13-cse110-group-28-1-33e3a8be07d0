package com.example.placeits;

import java.util.Calendar;
import java.util.GregorianCalendar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import Database.MainDataSource;

/*
 * ViewCategoryPlaceItActivity displays the category PlaceIt information and 
 * allows the user to toggle its status or delete it
 */
public class ViewCategoryPlaceItActivity extends Activity
{	
	//Instance variables
	private Button setActiveButton;
	private Button setInactive;
	private Button deleteButton;
	private PlaceIt placeIt;
	private MainDataSource dataSource = MainDataSource.getInstance(this);
	
	//displays the visual elements of this activity
	protected void onCreate(Bundle savedInstanceState)
	{	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_category_place_it);
        
		Intent i = getIntent();
        PlaceIt place_it = (PlaceIt) i.getParcelableExtra("placeIt");
        placeIt = dataSource.findByPinId(place_it.getPlaceItId()).get(0);
        
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
					dataSource.deleteById(placeIt.getPlaceItId());
					finish();
				break;
			}
			if (!placeIt.isCategory())
				MainActivity.setReturnView(placeIt);
		}
	};

	//sets up the buttons and the text views
	private void setUpUIElements()
	{
		TextView title_content;
		TextView description_content;
	    TextView cat1;
		TextView cat2;
		TextView cat3;

		
		setActiveButton = (Button)findViewById(R.id.setActiveBtn);
	    setActiveButton.setOnClickListener(onClickListener);
	    setInactive = (Button)findViewById(R.id.setInactiveBtn);
	    setInactive.setOnClickListener(onClickListener);
	    deleteButton = (Button)findViewById(R.id.deleteBtn);
	    deleteButton.setOnClickListener(onClickListener);
	    title_content = (TextView)findViewById(R.id.titleContent);
	    description_content = (TextView)findViewById(R.id.descriptionContent);
	    title_content.setText(placeIt.getTitle());
	    description_content.setText(placeIt.getDescription());
	    
	
		cat1 = (TextView)findViewById(R.id.category1);
		cat1.setText("Category 1: " + placeIt.getCategory1());
		cat2 = (TextView)findViewById(R.id.category2);
		cat2.setText("Category 2: " + placeIt.getCategory2());
		cat3 = (TextView)findViewById(R.id.category3);
		cat3.setText("Category 3: " + placeIt.getCategory3());
	    
	}
}