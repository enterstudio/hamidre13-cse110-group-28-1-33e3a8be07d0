package com.example.placeits;

import Database.MainDataSource;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/*
 * CreateCategoryPlaceIt is the activity where the information for a 
 * category PlaceIt is submitted.
 */
public class CreateCategoryPlaceItActivity extends Activity implements OnItemSelectedListener
{
	//Instance vars
	protected MainDataSource dataSource = MainDataSource.getInstance(this);
	private Button placeItButton;
	private Button cancelButton;
	private EditText titleEdit;
	private EditText descriptionEdit;
	private Spinner categoryOneSpinner;
	private Spinner categoryTwoSpinner;
	private Spinner categoryThreeSpinner;
	private PlaceIt placeIt;
	private String categoryOne;
	private String categoryTwo;
	private String categoryThree;
	
	//sets up the visual elements of the activity
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_category_place_it);
		setUpUIElements();
	}
	// Listener for buttons PlaceIt and Cancel buttons
	private View.OnClickListener onClickListener = new OnClickListener()
   	{
   		@Override
   		public void onClick(View v)
   		{
   			switch(v.getId())
   			{
   				case R.id.placeItBtn:
   					if (titleEdit.getEditableText().toString().length() == 0)
   						Toast.makeText(getApplicationContext(), "ERROR: Place It needs a title to be created", Toast.LENGTH_LONG).show();
   					else if(categoryOne.equals("None") && categoryTwo.equals("None") && categoryThree.equals("None"))
   						Toast.makeText(getApplicationContext(), "ERROR: Place It needs at least one category to be created.", Toast.LENGTH_LONG).show();
   					else
   					{
   						placeIt = new PlaceIt();
	   					placeIt.setTitle(titleEdit.getEditableText().toString());
	   					placeIt.setDescription(descriptionEdit.getEditableText().toString());
	   					placeIt.setIsCategory(true);
	   					if (categoryOne.equals("None"))
	   						categoryOne = "";
	   					placeIt.setCategory1(categoryOne);
	   					if (categoryTwo.equals("None"))
	   						categoryTwo = "";
	   					placeIt.setCategory2(categoryTwo);
	   					if (categoryThree.equals("None"))
	   						categoryThree = "";
	   					placeIt.setCategory1(categoryOne);
	   					placeIt.setCategory2(categoryTwo);
	   					placeIt.setCategory3(categoryThree);
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
   	
   	
	// sets the default state of the text boxes and spinners
	private void setUpUIElements()
	{
		placeItButton = (Button)findViewById(R.id.placeItBtn); //links variable to the button
    	placeItButton.setOnClickListener(onClickListener);
    	
    	cancelButton = (Button)findViewById(R.id.cancelBtn);
		cancelButton.setOnClickListener(onClickListener);
		
		titleEdit = (EditText)findViewById(R.id.editTitle);
    	
    	descriptionEdit = (EditText)findViewById(R.id.editDescriptionView);
    	
    	categoryOneSpinner = (Spinner) findViewById(R.id.category1_spinner);
    	ArrayAdapter<CharSequence> adapter_one = ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item);
    	adapter_one.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	categoryOneSpinner.setEnabled(true);
    	categoryOneSpinner.setAdapter(adapter_one);
    	categoryOneSpinner.setOnItemSelectedListener(this);
    	
    	categoryTwoSpinner = (Spinner) findViewById(R.id.category2_spinner);
    	ArrayAdapter<CharSequence> adapter_two = ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item);
    	adapter_two.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	categoryTwoSpinner.setEnabled(true); //disabled until scheduled checkbox is checked
    	categoryTwoSpinner.setAdapter(adapter_two);
    	categoryTwoSpinner.setOnItemSelectedListener(this);
    	
    	categoryThreeSpinner = (Spinner) findViewById(R.id.category3_spinner);
    	ArrayAdapter<CharSequence> adapter_three = ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item);
    	adapter_three.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	categoryThreeSpinner.setEnabled(true); //disabled until scheduled checkbox is checked
    	categoryThreeSpinner.setAdapter(adapter_three);
    	categoryThreeSpinner.setOnItemSelectedListener(this);
	}


	//listener for the 3 spinners that determine categories of the placeIt
	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3)
	{
		switch(parent.getId())
		{
			case R.id.category1_spinner:
				categoryOne = parent.getItemAtPosition(pos).toString();
				break;
			case R.id.category2_spinner:
				categoryTwo = parent.getItemAtPosition(pos).toString();
				break;
			case R.id.category3_spinner:
				categoryThree = parent.getItemAtPosition(pos).toString();
				break;
		}
	}

	/*
	 * Listener for the list in this activity, it sets up the next
	 * intent based on which placeIt the user clicked.
	 */
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}
}
