package com.example.placeits;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import Database.MainDataSource;

/*
 * CreatePlaceIt is the activity where the information for a 
 * regular PlaceIt is submitted.
 */
public class CreatePlaceItActivity extends Activity implements OnItemSelectedListener
{	
	protected MainDataSource dataSource = MainDataSource.getInstance(this);
	
	private Button placeItButton;
	private CheckBox scheduleCheckBox;
	private EditText titleEdit;
	private Button cancelButton;
	private EditText descriptionEdit;
	private int schedule = 1;
	private Spinner scheduleSpinner;
	private PlaceIt placeIt;
	
	protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        placeIt = (PlaceIt) i.getParcelableExtra("placeIt");
        setContentView(R.layout.create_view);
        
        setUpUIElements();
    	
	}

	//sets up the visual elements of the activity
	private void setUpUIElements() {
		//initializes spinner (Drop down menu) the options are stored in the strings.xml as an array
    	scheduleSpinner = (Spinner) findViewById(R.id.schedule_spinner);
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.schedule_array, android.R.layout.simple_spinner_item);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	scheduleSpinner.setEnabled(false); //disabled until scheduled checkbox is checked
    	scheduleSpinner.setAdapter(adapter);
    	scheduleSpinner.setOnItemSelectedListener(this);
    	
    	placeItButton = (Button)findViewById(R.id.placeItBtn); //links variable to the button
    	placeItButton.setOnClickListener(onClickListener);
 
    	cancelButton = (Button)findViewById(R.id.cancelBtn);
		cancelButton.setOnClickListener(onClickListener);
    	
    	scheduleCheckBox = (CheckBox)findViewById(R.id.checkBox1); // enables spinner if checked
    	scheduleCheckBox.setOnClickListener(onClickListener);
    	
    	
    	titleEdit = (EditText)findViewById(R.id.editTitle);
    	
    	descriptionEdit = (EditText)findViewById(R.id.editDescriptionView);
	}
			
    // Listener for the PlaceIt and Cancel buttons
   	private View.OnClickListener onClickListener = new OnClickListener()
   	{
   		@Override
   		public void onClick(View v)
   		{
   			switch(v.getId())
   			{
   				case R.id.checkBox1:
   					if (scheduleCheckBox.isChecked())
   						scheduleSpinner.setEnabled(true);
   					else
   						scheduleSpinner.setEnabled(false);
   				break;
   				
   				case R.id.placeItBtn:
   					if (titleEdit.getEditableText().toString().length() == 0)
   						Toast.makeText(getApplicationContext(), "ERROR: PlaceIt needs a title to be created", Toast.LENGTH_LONG).show();
   					else
   					{
	   					placeIt.setTitle(titleEdit.getEditableText().toString());
	   					placeIt.setDescription(descriptionEdit.getEditableText().toString());
	   					if (scheduleCheckBox.isChecked())
	   					{ 
	   						placeIt.setOnSchedule(1);
	   						placeIt.setSchedule(schedule);
	   					}
	   					else
	   					{
	   						placeIt.setOnSchedule(0);
	   						placeIt.setSchedule(0);
	   					}
	   					placeIt.setStatus("ACTIVE");
	   					dataSource.open();
	   					dataSource.create(placeIt);
	   					finish();
	   				}
   				break;
   					
   				case R.id.cancelBtn:
   					finish();
   				break;
   			}
   		}
   	};
    
   	// Listener for the spinner that determines schedule frequency
	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3)
	{
		schedule = Integer.parseInt(parent.getItemAtPosition(pos).toString());	
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}	
}

 