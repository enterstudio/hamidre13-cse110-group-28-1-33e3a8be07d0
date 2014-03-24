package com.example.placeits;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/*
 * PlacesSerive queries google Places based on location, type(Category), and radius 
 * and returns all valid locations
 */
public class PlacesService {

	private String API_KEY;

	public PlacesService(String apikey) {
		this.API_KEY = apikey;
	}

	public void setApiKey(String apikey) {
		this.API_KEY = apikey;
	}

	/* Queries google Places for JSON objects that match the criteria and
	 * makes an array of PlaceIts from them.
	 */
	public ArrayList<PlaceIt> findPlaces(double latitude, double longitude,
			String placeType, PlaceIt refP) {

		String urlString = makeUrl(latitude, longitude, placeType);

		try {
			String json = getJSON(urlString);

			JSONObject object = new JSONObject(json);
			JSONArray array = object.getJSONArray("results");

			ArrayList<PlaceIt> arrayList = new ArrayList<PlaceIt>();
			for (int i = 0; i < array.length(); i++) {
				try {
					PlaceIt place = PlaceIt
							.jsonToPlaceIt(refP, (JSONObject) array.get(i));
					Log.v("Places Services ", "" + place);
					System.out.println("findPlaces: " + place.getLocale());
					arrayList.add(place);
				} catch (Exception e) {
				}
			}
			return arrayList;
		} catch (JSONException ex) {
			Logger.getLogger(PlacesService.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return null;
	}

	// Formats the query to send to google places
	private String makeUrl(double latitude, double longitude, String place) {
		StringBuilder urlString = new StringBuilder(
				"https://maps.googleapis.com/maps/api/place/search/json?");
		
		if (!place.equals("")) {
			urlString.append("&location=");
			urlString.append(Double.toString(latitude));
			urlString.append(",");
			urlString.append(Double.toString(longitude));
			urlString.append("&radius=1000");
			urlString.append("&types=" + place);
			urlString.append("&sensor=false&key=" + API_KEY);
		}
		return urlString.toString();
	}
	
	// returns a string with the results of the url query
	protected String getJSON(String url) {
		return getUrlContents(url);
	}
	
	// private helper to getJson method
	private String getUrlContents(String theUrl) {
		StringBuilder content = new StringBuilder();

		try {
			URL url = new URL(theUrl);
			URLConnection urlConnection = url.openConnection();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(urlConnection.getInputStream()), 8);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				content.append(line + "\n");
			}
			bufferedReader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return content.toString();
	}
}