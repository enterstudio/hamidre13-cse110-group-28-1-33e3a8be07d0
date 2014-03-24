package com.example.placeits;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/*
 * PlaceItAdaper creates the UI element to display a list of placeIts
 */
public class PlaceItAdapter extends ArrayAdapter<PlaceIt>
{
	private Context context;
	private List<PlaceIt> objects;
	
	public PlaceItAdapter(Context context, int resource, List<PlaceIt> objects)
	{
		super(context, resource, objects);
		this.context = context;
		this.objects = objects;
	}
	
	@Override
	public View getView(int position, View convert_view, ViewGroup parent)
	{
		PlaceIt place_it = objects.get(position);
		LayoutInflater inflater  = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.item_marker, null);
		TextView place_it_title  = (TextView) view.findViewById(R.id.textView1);
		place_it_title.setText(place_it.getTitle());
		return view;
	}

}
