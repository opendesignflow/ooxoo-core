/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Rtek
 * 
 */
public class TimeBuffer extends DateTimeBuffer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1452300102675339146L;

	/**
	 * 
	 */
	public TimeBuffer() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 * @param arg5
	 */
	public TimeBuffer(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		super(arg0, arg1, arg2, arg3, arg4, arg5);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 */
	public TimeBuffer(int arg0, int arg1, int arg2, int arg3, int arg4) {
		super(arg0, arg1, arg2, arg3, arg4);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public TimeBuffer(Locale arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public TimeBuffer(TimeZone arg0, Locale arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public TimeBuffer(TimeZone arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public TimeBuffer(int hourOfDay, int minute, int second) {
		
		//if (hourOfDay>=12)
		//	this.set(Calendar.AM_PM, Calendar.PM);
		//else
			//this.set(Calendar.AM_PM, Calendar.AM);
		//this.set(Calendar.AM_PM, Calendar.PM);
	
		this.set(Calendar.HOUR_OF_DAY, hourOfDay);
		this.set(Calendar.MINUTE, minute);
		this.set(Calendar.SECOND, second);
		
	}

	/**
	 * Returns the string representation of this time type
	 */
	public String toString() {

		return this.toStringNoZone() + this.getTimeZoneRepresentation();
	}

	public String toStringNoZone() {
		//this.set(Calendar.AM_PM, Calendar.AM);
		// Set Hour of Day
		int temp = this.get(Calendar.HOUR_OF_DAY);
		String time = temp > 9 ? "" + temp : "0" + temp;

		time += ":";
		temp = this.get(Calendar.MINUTE);
		time += temp > 9 ? "" + temp : "0" + temp;

		time += ":";
		temp = this.get(Calendar.SECOND);
		time += temp > 9 ? "" + temp : "0" + temp;

		return time;
	}

	public static TimeBuffer fromString(String time) {

		TimeBuffer tm = null;

		try {

			if (time == null)
				throw new Exception("time value is null!");

			// Split at :
			String[] splited = time.split(":");

			// array MUST be of length 3
			if (splited.length != 3)
				throw new Exception("time value is invalid!");

			// Get hour
			int hour = Integer.parseInt(splited[0]);
			// Get minutes
			int minutes = Integer.parseInt(splited[1]);
			// Get seconds
			int seconds = Integer.parseInt(splited[2].replaceAll(
					"(\\+|-|Z).*$", ""));

			// Get gmt
			String gmt;
			if (splited[2].split("\\+").length > 1)
				gmt = "-";
			else if (splited[2].split("-").length > 1)
				gmt = "+";
			else
				gmt = "+0";
			gmt += splited[2].replaceAll("^.*(\\+|-|Z)", "");

			// build time
//			System.out.println("Read Time before object creation: "+hour+":"+minutes+":"+seconds);
			tm = new TimeBuffer(hour, minutes, seconds);
		
			tm.setTimeZone(TimeZone.getTimeZone("Etc/GMT" + gmt));

		} catch (Exception ex) {
			tm = null;
		}

		return tm;
	}
	
	public static TimeBuffer fromStringNoValidation(String time) {

		return TimeBuffer.fromString(time);
	}

}
