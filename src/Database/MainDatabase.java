package Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 * MainDataBase provides the outline and the column information 
 * for MainDataSource.
 */
public class MainDatabase extends SQLiteOpenHelper
{
	private static final String LOGTAG = "Main Db creation";

	private static final String DATABASE_NAME = "main.db";
	private static final int DATABASE_VERSION = 11;
	
	// Column names
	public static final String TABLE_Droppins = "droppins";
	public static final String COLUMN_DroppinID = "placeitID";
	public static final String COLUMN_Pintitle = "pintitle";
	public static final String COLUMN_Pindescription = "pindescription";
	public static final String COLUMN_Pinstatus = "pindstatus";
	public static final String COLUMN_Longitude = "longitude";
	public static final String COLUMN_Latitude = "latitude";
	public static final String COLUMN_onSchedule = "onSchedule";
	public static final String COLUMN_schedule = "schedule";
	public static final String COLUMN_inactiveDate = "inactiveDate";
	public static final String COLUMN_userName = "username";
	public static final String COLUMN_isCategory = "isCategory";
	public static final String COLUMN_category1 = "Category1";
	public static final String COLUMN_category2 = "Category2";
	public static final String COLUMN_category3 = "Category3";
	
	//Column types
	private static final String TABLE_CREATE_pins = 
			"CREATE TABLE " + TABLE_Droppins + "(" +
			COLUMN_DroppinID  + " LONG PRIMARY KEY, " +
			COLUMN_Pintitle + " TEXT, " +
			COLUMN_Pindescription + " TEXT, " +
			COLUMN_Pinstatus + " CHAR50, " +
			COLUMN_Longitude + " CHAR100, " + 
			COLUMN_Latitude + " CHAR100, " +
			COLUMN_onSchedule + " TINYINT, " +
			COLUMN_schedule + " CHAR50, " +
			COLUMN_inactiveDate +" TEXT, "+
			COLUMN_userName + " TEXT, " +
			COLUMN_isCategory + " INT, " +
			COLUMN_category1 + " TEXT, " +
			COLUMN_category2 + " TEXT, " +
			COLUMN_category3 + " TEXT"+
			")";
	
	public MainDatabase(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(TABLE_CREATE_pins);
		Log.i(LOGTAG,"Main DB created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int i, int k)
	{
		db.execSQL("DROP TABLE IF EXISTS "+ TABLE_Droppins);
		onCreate(db);
	}
	
}
