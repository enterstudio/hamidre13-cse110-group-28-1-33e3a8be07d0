package Database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.placeits.CloudEndpointUtils;
import com.example.placeits.PlaceIt;
import com.example.placeits.userendpoint.Userendpoint;
import com.example.placeits.userendpoint.model.User;
import com.example.placeits.placeitendpoint.Placeitendpoint;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.example.placeits.PlaceIt;

/*
 * Singleton database class that manages all the PlaceIt information 
 */
public class MainDataSource extends Observable
{
	//Instance vars
	private static final String LOGTAG = "Main Db creation";
	private static MainDataSource instance = null;
	SQLiteOpenHelper dataBaseHelper;
	SQLiteDatabase dataBase;
	private String userName = "";
	private long ID;
	
	//used for unique ID creation
	private static final long LIMIT = 100000000L;
	private static long last = 0;
	
	private void setUserName(String word)
	{
		userName = word;
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public static final String[] allColumns = 
	{
		MainDatabase.COLUMN_DroppinID,
		MainDatabase.COLUMN_Latitude,
		MainDatabase.COLUMN_Longitude,
		MainDatabase.COLUMN_Pintitle,
		MainDatabase.COLUMN_onSchedule,
		MainDatabase.COLUMN_Pindescription,
		MainDatabase.COLUMN_Pinstatus,
		MainDatabase.COLUMN_schedule,
		MainDatabase.COLUMN_inactiveDate,
		MainDatabase.COLUMN_userName,
		MainDatabase.COLUMN_isCategory,
		MainDatabase.COLUMN_category1,
		MainDatabase.COLUMN_category2,
		MainDatabase.COLUMN_category3
	};
	
	private MainDataSource(Context context)
	{
		dataBaseHelper = new MainDatabase(context);	
	}
	
	//allows classes to get the same instance of this class
	public static MainDataSource getInstance(Context context)
	{
		if (instance == null)
			instance = new MainDataSource(context);
		return instance;
	}
	
	public void open()
	{
		Log.i(LOGTAG,"Database opened");
		dataBase = dataBaseHelper.getReadableDatabase();
	}
	
	public void close()
	{
		Log.i(LOGTAG,"Database closed");
		dataBaseHelper.close();
	}
	

	
	// adds the input PlaceIt to the database
	public int create(PlaceIt place_it)
	{
		long id = getID();
		ContentValues values = new ContentValues();
		values.put(MainDatabase.COLUMN_Latitude	, place_it.getLatitude());
		values.put(MainDatabase.COLUMN_Longitude, place_it.getLongitude());
		values.put(MainDatabase.COLUMN_Pintitle	, place_it.getTitle());
		values.put(MainDatabase.COLUMN_Pindescription, place_it.getDescription());
		values.put(MainDatabase.COLUMN_Pinstatus, place_it.getStatus());
		values.put(MainDatabase.COLUMN_onSchedule	, place_it.getOnSchedule());
		values.put(MainDatabase.COLUMN_schedule	, place_it.getSchedule());
		values.put(MainDatabase.COLUMN_DroppinID, id);
		values.put(MainDatabase.COLUMN_userName, place_it.getUserName());
		
		if(place_it.isCategory())
		{
			values.put(MainDatabase.COLUMN_isCategory, 1);
			values.put(MainDatabase.COLUMN_category1, place_it.getCategory1());
			values.put(MainDatabase.COLUMN_category2, place_it.getCategory2());
			values.put(MainDatabase.COLUMN_category3, place_it.getCategory3());
		}
		else
		{
			values.put(MainDatabase.COLUMN_isCategory, 0);
		}
		
		dataBase.insert(MainDatabase.TABLE_Droppins, null, values);
		System.out.println("First inserted Title: " + place_it.getTitle() + " ID: " + id);

		
		Cursor row_id = dataBase.rawQuery("select "+ MainDatabase.COLUMN_DroppinID +" from "+MainDatabase.TABLE_Droppins +" order by "+MainDatabase.COLUMN_DroppinID +" DESC limit 1", null);
		place_it.setPlaceItId((int)id);
		setChanged();
		row_id.moveToNext();
		notifyObservers(row_id.getInt(0));
		new EndpointsTask().execute(place_it);
		return row_id.getInt(0);
	}
	
	//returns all PlaceIts (active and inactive)
	public List<PlaceIt> findAll()
	{
		Cursor cursor = dataBase.query(MainDatabase.TABLE_Droppins, allColumns, null, null, null, null, null);
		Log.i(LOGTAG,"Returned"+cursor.getCount()+" rows");
		List<PlaceIt> place_its = cursorToList(cursor);
	
		return place_its;
	}
	// returns a list of PlaceIts that are on a schedule
	public List<PlaceIt> findOnSchedule()
	{
		Cursor cursor = dataBase.query(MainDatabase.TABLE_Droppins,allColumns, "'" +MainDatabase.COLUMN_onSchedule +"' = 1 AND '"+MainDatabase.COLUMN_Pinstatus +"' = INACTIVE" , null, null, null, null, null);
		Log.i(LOGTAG,"Returned"+cursor.getCount()+" rows");
		List<PlaceIt> place_its = cursorToList(cursor);
	
		return place_its;
	}
	
	// returns a single PlaceIt that matches the lat and lng coordinates
	public PlaceIt findByLatLong(double latitude, double longitude)
	{ 
		Cursor cursor = dataBase.rawQuery("select * from "+MainDatabase.TABLE_Droppins  + " where "+MainDatabase.COLUMN_Latitude +" = "+ latitude +" AND "+ MainDatabase.COLUMN_Longitude +" = "+ longitude, null);
		Log.i(LOGTAG,"Returned"+cursor.getCount()+" rows");
		List<PlaceIt> place_its = cursorToList(cursor);
		return place_its.get(0);
	}
	
	// returns a list of PlaceIts whose Id matches the input ID
	public List<PlaceIt> findByPinId(int pinId)
	{
		String query = MainDatabase.COLUMN_DroppinID+" = "+"'"+pinId+"'";
		Cursor cursor = dataBase.query(MainDatabase.TABLE_Droppins,allColumns,query, null, null, null, null);
		Log.i(LOGTAG,"Returned"+cursor.getCount()+" rows");
		List<PlaceIt> place_its = cursorToList(cursor);
	
		return place_its;
	}
	
	// returns a list of PlaceIts whose status matches the input String
	public List<PlaceIt> findByStatus(String status)
	{
		String query = MainDatabase.COLUMN_Pinstatus +"= '" + status +"'" ;
		Cursor cursor = dataBase.query(MainDatabase.TABLE_Droppins, allColumns, query, null, null, null, null);
		Log.i(LOGTAG,"Returned"+cursor.getCount()+" rows");
		List<PlaceIt> place_its = cursorToList(cursor);
	
		return place_its;
	}
	
	// edits the information of the passed in placeIt in the database
	public void editById(PlaceIt place_it)
	{
		dataBase.rawQuery(" ", null);
		ContentValues values = new ContentValues();
		values.put(MainDatabase.COLUMN_Latitude	, place_it.getLatitude());
		values.put(MainDatabase.COLUMN_Longitude, place_it.getLongitude());
		values.put(MainDatabase.COLUMN_Pintitle	, place_it.getTitle());
		values.put(MainDatabase.COLUMN_Pindescription, place_it.getDescription());
		values.put(MainDatabase.COLUMN_Pinstatus, place_it.getStatus());
		values.put(MainDatabase.COLUMN_onSchedule	, place_it.getOnSchedule());
		values.put(MainDatabase.COLUMN_schedule	, place_it.getSchedule());
		values.put(MainDatabase.COLUMN_inactiveDate	, place_it.getInactiveDateTime().toString());
		
		values.put(MainDatabase.COLUMN_userName, place_it.getUserName());
		
		if(place_it.isCategory())
		{
			values.put(MainDatabase.COLUMN_isCategory, 1);
			values.put(MainDatabase.COLUMN_category1, place_it.getCategory1());
			values.put(MainDatabase.COLUMN_category2, place_it.getCategory2());
			values.put(MainDatabase.COLUMN_category3, place_it.getCategory3());
		}
		else
		{
			values.put(MainDatabase.COLUMN_isCategory, 0);
		}
		
		
		dataBase.update(MainDatabase.TABLE_Droppins, values, MainDatabase.COLUMN_DroppinID +" = " + "'"+place_it.getPlaceItId() +"'", null);
		setChanged();
		notifyObservers(place_it.getPlaceItId());
		new EndpointsUpdateTask().execute(place_it);
	}
	
	//removes placeIt that corresponds to the place_it_id from the database
	public void deleteById(int place_it_id)
	{
		dataBase.delete(MainDatabase.TABLE_Droppins,MainDatabase.COLUMN_DroppinID +"=" + place_it_id,null);
		setChanged();
		notifyObservers(place_it_id);
		new EndpointsDeleteTask().execute(place_it_id);
	}
	
	// converts a Cursor into a list of PlaceIts
	private List<PlaceIt> cursorToList(Cursor cursor)
	{
		List<PlaceIt> place_its = new ArrayList<PlaceIt>();
		if(cursor.getCount()>0)
		{
			while (cursor.moveToNext())
			{
				PlaceIt place_it = new PlaceIt();
				place_it.setTitle(cursor.getString(cursor.getColumnIndex(MainDatabase.COLUMN_Pintitle)));
				place_it.setDescription(cursor.getString(cursor.getColumnIndex(MainDatabase.COLUMN_Pindescription)));
				place_it.setLongitude(cursor.getDouble(cursor.getColumnIndex(MainDatabase.COLUMN_Longitude)));
				place_it.setLatitude(cursor.getDouble(cursor.getColumnIndex(MainDatabase.COLUMN_Latitude)));
				place_it.setStatus(cursor.getString(cursor.getColumnIndex(MainDatabase.COLUMN_Pinstatus)));
				place_it.setOnSchedule((int)cursor.getLong(cursor.getColumnIndex(MainDatabase.COLUMN_onSchedule)));
				place_it.setSchedule(cursor.getInt(cursor.getColumnIndex(MainDatabase.COLUMN_schedule)));
				place_it.setPlaceItId((int)cursor.getLong(cursor.getColumnIndex(MainDatabase.COLUMN_DroppinID)));
				
				place_it.setUserName(cursor.getString(cursor.getColumnIndex(MainDatabase.COLUMN_userName)));
				
				if(cursor.getInt(cursor.getColumnIndex(MainDatabase.COLUMN_isCategory)) == 0)
				{
					place_it.setIsCategory(false);
				}
				else place_it.setIsCategory(true);
				
				if(place_it.isCategory())
				{
					place_it.setCategory1(cursor.getString(cursor.getColumnIndex(MainDatabase.COLUMN_category1)));
					place_it.setCategory2(cursor.getString(cursor.getColumnIndex(MainDatabase.COLUMN_category2)));
					place_it.setCategory3(cursor.getString(cursor.getColumnIndex(MainDatabase.COLUMN_category3)));
				}
				System.out.println("Returning Title: " + place_it.getTitle() + " ID: " + place_it.getPlaceItId());
				place_its.add(place_it);
				
			}
		}
		return place_its;
	}
	
//start
public boolean createUser(String string, String password)
{
	try {
		return new CreateUserTask().execute(string, password).get();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ExecutionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return false;
	
}
public boolean authUser(String username, String password)
{
	try {
		return new AuthUserTask().execute(username, password).get();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ExecutionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return true;
}

public class CreateUserTask extends AsyncTask<String, Integer, Boolean>
{
	protected Boolean doInBackground(String...params)
	{
		String username = params[0];
		String password = params[1];
		Userendpoint.Builder endpointBuilder = new Userendpoint.Builder(
				 AndroidHttp.newCompatibleTransport(),
	              new JacksonFactory(),
	              new HttpRequestInitializer() {
	              public void initialize(HttpRequest httpRequest) { }
	              });
		Userendpoint endpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
		com.example.placeits.userendpoint.model.User user = new com.example.placeits.userendpoint.model.User();
		
		try {
			user = endpoint.getUser(username).execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			
				user.setUsername(username);
				user.setPassword(password);
				try {
					endpoint.insertUser(user).execute();
					
				} catch (IOException ex) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			
		}
		return false;
		
		
		
	}
}

public class AuthUserTask extends AsyncTask<String, Integer, Boolean>
{

	protected Boolean doInBackground(String... params)
	{
		
		String username = params[0];
		String password = params[1];
		Userendpoint.Builder endpointBuilder = new Userendpoint.Builder(
				 AndroidHttp.newCompatibleTransport(),
	              new JacksonFactory(),
	              new HttpRequestInitializer() {
	              public void initialize(HttpRequest httpRequest) { }
	              });
				Userendpoint endpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
		
					User temp = new User();
					temp.setUsername(username);
				com.example.placeits.userendpoint.model.User tempuser;
				try {
					
					tempuser = endpoint.getUser(username).execute();
					if(tempuser.getUsername() == null) return false;
					System.out.println("In the try part");
					System.out.println("Username: " + tempuser.getUsername());
					System.out.println("Password: " + password);
					if(tempuser.getPassword().equals(password))
					{
						
						setUserName(username);
						//delete();
						return true;
					}
					
					
				} catch (Exception e) {
					System.out.println("in the exception");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
		        
	}
  
}

public class EndpointsTask extends AsyncTask<PlaceIt, Integer, Long> {
        protected Long doInBackground(PlaceIt... params) {
        	 PlaceIt input = params[0];
        	 Placeitendpoint.Builder endpointBuilder = new Placeitendpoint.Builder(
    				 AndroidHttp.newCompatibleTransport(),
    	              new JacksonFactory(),
    	              new HttpRequestInitializer() {
    	              public void initialize(HttpRequest httpRequest) { }
    	              });
    				Placeitendpoint endpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
    				try
    				{
    					com.example.placeits.placeitendpoint.model.PlaceIt place = new com.example.placeits.placeitendpoint.model.PlaceIt();
    					if(input.isCategory())
    					{
    						place.setIscategory(true);
    						place.setCat0(input.getCategory1());
    						place.setCat1(input.getCategory2());
    						place.setCat2(input.getCategory3());
    					}
    					else
    					  place.setIscategory(false);
    					
    					place.setId((long)input.getPlaceItId());
    					place.setTitle(input.getTitle());
    					place.setDescription(input.getDescription());
    					place.setStatus(input.getStatus());
    					place.setSchedule(input.getOnSchedule());
    					place.setUserName(input.getUserName());
    					place.setLatit(input.getLatitude());
    					place.setLongit(input.getLongitude());
    					Log.i("EndPointsAsyncTask", place.getUserName());
    					com.example.placeits.placeitendpoint.model.PlaceIt result =  endpoint.updatePlaceIt(place).execute();
    				}catch(Exception e) {
    			        e.printStackTrace();
    			      } 
        	return (long) 0;
        }
        
        

		
       }

public class EndpointsUpdateTask extends AsyncTask<PlaceIt, Integer, Long> {
    protected Long doInBackground(PlaceIt... params) {
    	 PlaceIt input = params[0];
    	 Placeitendpoint.Builder endpointBuilder = new Placeitendpoint.Builder(
				 AndroidHttp.newCompatibleTransport(),
	              new JacksonFactory(),
	              new HttpRequestInitializer() {
	              public void initialize(HttpRequest httpRequest) { }
	              });
				Placeitendpoint endpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
				try
				{
					com.example.placeits.placeitendpoint.model.PlaceIt place = new com.example.placeits.placeitendpoint.model.PlaceIt();
					if(input.isCategory())
					{
						place.setIscategory(true);
						place.setCat0(input.getCategory1());
						place.setCat1(input.getCategory2());
						place.setCat2(input.getCategory3());
					}
					else
					  place.setIscategory(false);
					
					place.setId((long)input.getPlaceItId());
					place.setTitle(input.getTitle());
					place.setDescription(input.getDescription());
					place.setStatus(input.getStatus());
					place.setSchedule(input.getOnSchedule());
					place.setUserName(input.getUserName());
					place.setLatit(input.getLatitude());
					place.setLongit(input.getLongitude());
					
					com.example.placeits.placeitendpoint.model.PlaceIt result =  endpoint.updatePlaceIt(place).execute();
				    Log.i("MainDatasource", "updating server with place it");
				}catch(Exception e) {
			        e.printStackTrace();
			      } 
    	return (long) 0;
    }
   }

public class EndpointsDeleteTask extends AsyncTask<Integer, Integer, Long> {
    protected Long doInBackground(Integer... params) {
    	Placeitendpoint.Builder endpointBuilder = new Placeitendpoint.Builder(
				 AndroidHttp.newCompatibleTransport(),
	              new JacksonFactory(),
	              new HttpRequestInitializer() {
	              public void initialize(HttpRequest httpRequest) { }
	              });
				Placeitendpoint endpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
			try {
				endpoint.removePlaceIt((long)params[0]).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         return (long)params[0];
    }
}





//creates a unique 10 digit placeItId
	public static long getID() {
		  // 10 digits.
		  long id = System.currentTimeMillis() % LIMIT;
		  if ( id <= last ) {
		    id = (last + 1) % LIMIT;
		  }
		  return last = id;
	}
}
