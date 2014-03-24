package com.example.placeits;

import java.util.List;

import Database.MainDataSource;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/*
 * InactiveListActivity displays all PlaceIts (regular or category) that
 * are inactive.
 * 
 */
public class InactiveListActivity extends ListActivity
{
	private List<PlaceIt> placeItsList;
	MainDataSource dataSource = MainDataSource.getInstance(this);
	
	//sets up the visual elements of this activity
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{    	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_lists);
		TextView listTitle = (TextView)findViewById(R.id.listTitle);
		listTitle.setText("The following Place It(s) are inactive:");
		
		placeItsList = dataSource.findByStatus("INACTIVE");

		if(placeItsList.size() > 0)
		{
			PlaceItAdapter adaptedPlaceIts = new PlaceItAdapter (this,android.R.layout.simple_list_item_1, placeItsList);
			setListAdapter(adaptedPlaceIts);
		}
	 }
	
	/*
	 * Listener for the list in this activity, it sets up the next
	 * intent based on which placeIt the user clicked.
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		Intent intent;
		PlaceIt placeIt = placeItsList.get(position);
		if (placeIt.isCategory())
			intent = new Intent(this, ViewCategoryPlaceItActivity.class);
		else
			intent = new Intent(this, ViewPlaceItActivity.class);
		intent.putExtra("placeIt", (Parcelable) placeIt);
		startActivity(intent);
		finish();
	}
}