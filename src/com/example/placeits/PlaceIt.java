package com.example.placeits;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/*
 * PlaceIt is the intermediary class between the database and the user interface 
 * that passes and stores information about a specific PlaceIt.
 */
public class PlaceIt implements Parcelable{
	
	//PlaceIt fields
	private String title;
	private String description;
	private double longitude;
	private double latitude;
	private int onSchedule;
	private int schedule=0;
	private int placeItId = -1;
	private boolean isCategory = false;
	private String category1 = "";
	private String category2 = "";
	private String category3 = "";
	private Calendar inactiveDateTime = Calendar.getInstance();
	private String status;
	private String userName = "";
	
	/* These two fields are not passed to the database and are only for the purpose
	 * of displaying information about a nearby locale for a given category.
	 */ 
	private String locale = ""; 
	private String address = "";
	
	public PlaceIt(Parcel in)
	{
		readFromParcel(in);
	}
	
	public PlaceIt(LatLng point)
	{
		latitude = point.latitude;
		longitude = point.longitude;
	}
	
	public PlaceIt()
	{
		super();
	}
	
	public static PlaceIt convertPlaceIt(com.example.placeits.placeitendpoint.model.PlaceIt input)
	{
		PlaceIt place = new PlaceIt();
		if(input.getIscategory())
		{
			place.setCategory(true);
			place.setCategory1(input.getCat0());
			place.setCategory2(input.getCat1());
			place.setCategory3(input.getCat2());
		}
		else
		  place.setCategory(false);
		long id = input.getId();
		place.setPlaceItId((int)id);
		place.setTitle(input.getTitle());
		place.setDescription(input.getDescription());
		place.setStatus(input.getStatus());
		place.setSchedule(input.getOnSchedule());
		place.setUserName(input.getUserName());
		place.setLatitude(input.getLatit());
		place.setLongitude(input.getLongit());
		
		return place;
	}
	
	public String getUserName()
	{
		return userName;
	}
	public void setUserName(String word)
	{
		userName = word;
	}
	
	public void setIsCategory(boolean bool)
	{
		isCategory = bool;
	}
	
	public boolean isCategory() {
		return isCategory;
	}

	public void setCategory(boolean isCategory) {
		this.isCategory = isCategory;
	}

	public String getCategory1() {
		return category1;
	}

	public void setCategory1(String category1) {
		this.category1 = category1;
	}

	public String getCategory2() {
		return category2;
	}

	public void setCategory2(String category2) {
		this.category2 = category2;
	}

	public String getCategory3() {
		return category3;
	}

	public void setCategory3(String category3) {
		this.category3 = category3;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public int getOnSchedule() {
		return onSchedule;
	}
	public void setOnSchedule(int onSchedule) {
		this.onSchedule = onSchedule;
	}
	public int getSchedule() {
		return schedule;
	}
	public void setSchedule(int schedule) {
		this.schedule = schedule;
	}
	public int getPlaceItId() {
		return placeItId;
	}
	public void setPlaceItId(int placeItId) {
		this.placeItId = placeItId;
	}
	public Calendar getInactiveDateTime() {
		return inactiveDateTime;
	}
	public void setInactiveDateTime(Calendar inactiveDateTime) {
		this.inactiveDateTime = inactiveDateTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}
	
	// Stores necessary fields as a parcel to allow for passing of placeIts as intent extras
	@Override
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeDouble(longitude);
		out.writeDouble(latitude);	
		out.writeString(title);
		out.writeByte((byte)(isCategory ? 1: 0));
		out.writeInt(placeItId);
		out.writeString(locale);
		out.writeString(address);
	}
	
	// reads the parcel and sets the PlaceIt fields accordingly
	private void readFromParcel(Parcel in)
	{
		longitude = in.readDouble();
		latitude = in.readDouble();
		title = in.readString();
		isCategory = (in.readByte() != 0);
		placeItId = in.readInt();
		locale = in.readString();
		address = in.readString();
	}
	
	// Creates a placeIt from a parcel
	public static final Parcelable.Creator<PlaceIt> CREATOR = new Parcelable.Creator<PlaceIt>()
	{
		public PlaceIt createFromParcel (Parcel in)
		{
			return new PlaceIt(in);
		}

		public PlaceIt[] newArray(int size)
		{
			return new PlaceIt[size];
		}
	};	
	
	// creates a PlaceIt from a json object
	 static PlaceIt jsonToPlaceIt(PlaceIt refP, JSONObject json_object) {
	        try {
	            PlaceIt result = refP;
	            JSONObject geometry = (JSONObject) json_object.get("geometry");
	            JSONObject location = (JSONObject) geometry.get("location");
	            result.setLatitude((Double) location.get("lat"));
	            result.setLongitude((Double) location.get("lng"));
	            System.out.println("The store name is: " + json_object.getString("name"));
	            result.setLocale(json_object.getString("name"));
	            result.setAddress(json_object.getString("vicinity"));
	            System.out.println("The placeits locale is: " + result.getLocale());
	            return result;
	        } catch (JSONException ex) {
	            Logger.getLogger(PlaceIt.class.getName()).log(Level.SEVERE, null, ex);
	        }
	        return null;
	  }
}

