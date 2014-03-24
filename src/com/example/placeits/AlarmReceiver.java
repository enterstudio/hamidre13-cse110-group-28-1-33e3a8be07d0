package com.example.placeits;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import Database.MainDataSource;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*
 * Alarm Receiver class determines if any PlaceIts should
 * be set to active based on their set schedule.
 */
public class AlarmReceiver extends BroadcastReceiver
{
	private MainDataSource dataSource;
	private final int DELAY = 5; // seconds
	
	@Override
	public void onReceive(Context arg0, Intent arg1)
	{
		List<PlaceIt> scheduledPlaceIts = dataSource.findOnSchedule();
		for (PlaceIt placeIt : scheduledPlaceIts)
		{
			if (placeIt.getInactiveDateTime() != null)
			{
				Calendar gc  = new GregorianCalendar();
				if (gc.get(Calendar.SECOND) > placeIt.getInactiveDateTime().get(Calendar.SECOND) + DELAY * placeIt.getSchedule())
					placeIt.setStatus("ACTIVE");
			}
		}
	}
}
