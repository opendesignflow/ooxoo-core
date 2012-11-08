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
public class DateTimeBuffer extends DateBuffer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 715677378345186210L;
	// This field says wether string form must be expressed in UTC or
	// relative to UTC
	private boolean isUTC = true;

	/**
	 * 
	 */
	public DateTimeBuffer() {
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
	public DateTimeBuffer(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
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
	public DateTimeBuffer(int arg0, int arg1, int arg2, int arg3, int arg4) {
		super(arg0, arg1, arg2, arg3, arg4);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public DateTimeBuffer(int arg0, int arg1, int arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public DateTimeBuffer(Locale arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public DateTimeBuffer(TimeZone arg0, Locale arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public DateTimeBuffer(TimeZone arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public DateTimeBuffer(DateBuffer dt, TimeBuffer tm) {
		super();
		this.set(Calendar.YEAR, dt.get(Calendar.YEAR));
		this.set(Calendar.MONTH, dt.get(Calendar.MONTH));
		this.set(Calendar.DAY_OF_MONTH, dt.get(Calendar.DAY_OF_MONTH));

		this.set(Calendar.HOUR_OF_DAY, tm.get(Calendar.HOUR_OF_DAY));
		this.set(Calendar.MINUTE, tm.get(Calendar.MINUTE));
		this.set(Calendar.SECOND, tm.get(Calendar.SECOND));

	}

	public DateTimeBuffer(String dateTime) throws SyntaxException {
		this._setValueFromString(dateTime);
	}

	
	
	/* (non-Javadoc)
	 * @see com.idyria.tools.xml.oox.datatypes.XSDType#_setValueFromString(java.lang.String)
	 */
	@Override
	public void _setValueFromString(String dateTime) {
		if (dateTime == null)
			throw new SyntaxException("date value is null!");

		// Split at :
		String[] splited = dateTime.split("T");

		// array MUST be of length 3
		if (splited.length != 2)
			throw new SyntaxException("date value is invalid!");

		// Get date part
		DateBuffer dt = new DateBuffer(splited[0]);

		// Get time part
		TimeBuffer tm = TimeBuffer.fromString(splited[1]);

		if (dt != null) {
			this.set(Calendar.YEAR, dt.get(Calendar.YEAR));
			this.set(Calendar.MONTH, dt.get(Calendar.MONTH));
			this.set(Calendar.DAY_OF_MONTH, dt.get(Calendar.DAY_OF_MONTH));
		}

		if (tm != null) {
			this.set(Calendar.HOUR_OF_DAY, tm.get(Calendar.HOUR_OF_DAY));
			this.set(Calendar.MINUTE, tm.get(Calendar.MINUTE));
			this.set(Calendar.SECOND, tm.get(Calendar.SECOND));
		}
		// Set time zone from time object
		this.setTimeZone(tm.getTimeZone());
		
	}

	protected String getTimeZoneRepresentation() {

		String tz = "";

		int zonedec = this.getTimeZone().getRawOffset() / 3600000;
		if (zonedec > 0)
			tz += "+" + zonedec;
		else if (zonedec == 0)
			tz += "Z";
		else
			tz += "-" + Math.abs(zonedec);

		return tz;
	}

	/**
	 * This method generates the representation of the date using the local
	 * recoverable time zone relative to UTC
	 */
	public String toString() {

		TimeBuffer tm = new TimeBuffer(this.get(Calendar.HOUR_OF_DAY), this
				.get(Calendar.MINUTE), this.get(Calendar.SECOND));
		DateBuffer dt = new DateBuffer(this.get(Calendar.YEAR), this.get(Calendar.MONTH),
				this.get(Calendar.DAY_OF_MONTH));

		// Get string values
		String date = dt.toStringNoZone();
		String time = tm.toStringNoZone();

		// append correct time zone representation
		time += this.getTimeZoneRepresentation();

		// Concatenate the strings
		String dateTime = date + "T" + time;

		return dateTime;

	}

	public static DateTimeBuffer IncreaseDateTime(DateTimeBuffer dt, DurationBuffer duration) {

		DateTimeBuffer res = new DateTimeBuffer();
		res.set(Calendar.YEAR, dt.get(Calendar.YEAR) + duration.getYear());
		res.set(Calendar.MONTH, dt.get(Calendar.MONTH) + duration.getMonth());
		res.set(Calendar.DAY_OF_MONTH, dt.get(Calendar.DAY_OF_MONTH)
				+ duration.getDay());

		res.set(Calendar.HOUR, dt.get(Calendar.HOUR)
				+ duration.getHour());
		res
				.set(Calendar.MINUTE, dt.get(Calendar.MINUTE)
						+ duration.getMinute());
		res
				.set(Calendar.SECOND, dt.get(Calendar.SECOND)
						+ duration.getSecond());

		return res;

	}

	public static long compareDates(DateTimeBuffer dt1, DateTimeBuffer dt2) {

		if (dt1 == null && dt2 == null)
			return 0;
		else if (dt1 == null && dt2 != null)
			return -99999;
		else if (dt1 == null && dt2 != null)
			return 99999;
		else {

			long t1 = dt1.getTimeInMillis();
			long t2 = dt2.getTimeInMillis();

			return t1 - t2;

		}
	}

	public boolean validate() throws SyntaxException {
		// TODO Auto-generated method stub
		return false;
	}

}
