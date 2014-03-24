package com.example.placeits;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/*
 * TriggeredPlaceItsActivity displays all the PlaceIts that are within 1 KM of the
 * user.
 */
public class TriggeredPlaceItsActivity extends ListActivity {
	private List<PlaceIt> placeItsList;
	
	// displays the visual elements of this activity
	protected void onCreate(Bundle savedInstanceState)
	{    	
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
        placeItsList = i.getParcelableArrayListExtra("triggeredPlaceIts");
		setContentView(R.layout.show_lists);
		TextView listTitle = (TextView)findViewById(R.id.listTitle);
		listTitle.setText("The following Place It(s) are nearby:");
		
		if(placeItsList.size() > 0)
		{
			
			PlaceItAdapter adaptedPlaceIts = new PlaceItAdapter (this,android.R.layout.simple_list_item_1, placeItsList);
			setListAdapter(adaptedPlaceIts);
		}
		
	}
	
	/*
	 * listener for the PlaceIts in the list, directs user to the next Activity depending
	 * on which type of placeIt was clicked
	 */
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		PlaceIt placeIt = placeItsList.get(position);
		Intent intent;
		if (placeIt.isCategory())
			intent = new Intent(this, ViewTriggeredCategoryPlaceItActivity.class);
		else
			intent = new Intent(this, ViewPlaceItActivity.class);
		intent.putExtra("placeIt", (Parcelable) placeIt);
		startActivity(intent);
		finish();
	}

}
