package com.example.placeits;

import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import Database.MainDataSource;
import android.annotation.SuppressLint;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/*
 * Marker manager is tasked with creating, deleting, and changing the 
 * visibility of markers on the map.
 */
public class MarkerManager implements Observer
{ 
	GoogleMap gMap;
	MainDataSource dataSource;
	
	@SuppressLint("UseSparseArrays")
	HashMap<Integer, Marker> idToMarker= new HashMap<Integer, Marker>();
	
	public MarkerManager(final GoogleMap map, final MainDataSource ds)
	{
		gMap = map;
		dataSource = ds;
		populateMap(map);
	}
	

	// Displays all active markers on the map
	public void populateMap(GoogleMap map)
	{
		gMap = map;
		Marker marker;
		List<PlaceIt> all_markers = dataSource.findAll();
     	if(all_markers.size() >0)
     	{
     		for (PlaceIt place_it : all_markers)
     		{
     			if (!place_it.isCategory()) {
     				if (place_it.getStatus().equals("ACTIVE"))
     					marker = createMarker(place_it,true);
     				else {
     					marker = createMarker(place_it,false);
     					idToMarker.put(place_it.getPlaceItId(), marker);
     				}
     			}
     		}	
     	}
	}
	
	// creates a new marker at the specified location with the specified visibility
	private Marker createMarker(PlaceIt place_it, boolean visible)
	{
		Marker marker = gMap.addMarker(new MarkerOptions()
			.position(new LatLng(place_it.getLatitude(),place_it.getLongitude()))
			.icon(BitmapDescriptorFactory.fromAsset("note_icon.png"))
			.visible(visible));
		
		return marker;
	}
	
	// creates a new marker for the specified place_it or toggles its visibility
	public void updateMap(PlaceIt place_it)
	{
		int key = place_it.getPlaceItId();
		
		//marker was previously inactive now active, or vice versa
		if (idToMarker.containsKey(key))
				idToMarker.get(key).setVisible(place_it.getStatus().equals("ACTIVE"));

		// new marker was added
		else
		{
			Marker marker = createMarker(place_it, true);
			idToMarker.put(key, marker);
		}
	}
	
	// removes the markers from the map and the hashmap
	public void deleteFromMap(int key)
	{

		if (idToMarker.get(key) != null) {
			idToMarker.get(key).remove(); // remove the marker from Google Map
			idToMarker.remove(key); // remove the marker from hash map
		}
		
	}
		
	// called when subject(database) has changed, determines what to do the placeIt that was modified in the DB
	@Override
	public void update(Observable observed, Object id)
	{
		
		int key = (Integer)id;
		List<PlaceIt> list = dataSource.findByPinId(key);
		
		if (list.size() == 0) // not found in database
			deleteFromMap(key);
		else // found
		{
			PlaceIt place_it = list.get(0);
			if(!place_it.isCategory())
				updateMap(place_it);
		}
	}
}