package com.idyria.osi.ooxoo.core.buffers.datatypes;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.idyria.osi.ooxoo.core.tu.TransferUnit;



public class DateBuffer extends  AbstractDataTypesBuffer<GregorianCalendar> implements XSDType,Comparable<DateBuffer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -962944643681820588L;

	
	/**
	 * 
	 */
	public DateBuffer() {
		super();
		this.value = new GregorianCalendar();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 * @param arg5
	 */
	public DateBuffer(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		this();
		this.value = new GregorianCalendar(arg0, arg1, arg2, arg3, arg4, arg5);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 */
	public DateBuffer(int arg0, int arg1, int arg2, int arg3, int arg4) {
		this();
		this.value = new GregorianCalendar(arg0, arg1, arg2, arg3, arg4);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public DateBuffer(Locale arg0) {
		this();
		this.value = new GregorianCalendar(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public DateBuffer(TimeZone arg0, Locale arg1) {
		this();
		this.value = new GregorianCalendar(arg0,arg1);
	}

	/**
	 * @param arg0
	 */
	public DateBuffer(TimeZone arg0) {
		this();
		this.value = new GregorianCalendar(arg0);
	}

	/**
	 * A personal Constructor
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	public DateBuffer(int year, int month, int day) {
		this();
		this.set(Calendar.YEAR, year);
		this.set(Calendar.MONTH, month);
		this.set(Calendar.DAY_OF_MONTH, day);
	}
	
	/**
	 * @param dateTime
	 * @throws SyntaxException
	 */
	public DateBuffer(String date) {
		this();
		this._setValueFromString(date);
	}
	
	/**
	 * Delegate
	 * @param field
	 * @param value
	 */
	public void set(int field,int value) {
		super.value.set(field, value);
	}
	
	/**
	 * Delegate
	 * @param field
	 * @return
	 */
	public int get(int field) {
		return super.value.get(field);
	}
	

	/**
	 * Delegate
	 * @return
	 */
	public TimeZone getTimeZone() {
		return super.value.getTimeZone();
	}
	
	/**
	 * Delegate
	 * @param timeZone
	 */
	public void setTimeZone(TimeZone timeZone) {
		super.value.setTimeZone(timeZone);
	}
	
	public long getTimeInMillis() {
		return super.value.getTimeInMillis();
	}

	/**
	 * Returns the string representation of this time type
	 */
	public String toString() {

		return this.toStringNoZone();
	}

	public String toStringWithZone() {

		return this.toStringNoZone() + this.getTimeZoneRepresentation();
	}
	

	public String toStringNoZone() {

		// Set Year
		int temp = this.get(Calendar.YEAR);
		String date = temp > 9 ? "" + temp : "0" + temp;

		date += "-";
		temp = this.get(Calendar.MONTH) + 1; // January is 0
		date += temp > 9 ? "" + temp : "0" + temp;

		date += "-";
		temp = this.get(Calendar.DAY_OF_MONTH);
		date += temp > 9 ? "" + temp : "0" + temp;

		return date;
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
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Calendar#compareTo(java.util.Calendar)
	 */
	@Override
	public int compareTo(DateBuffer anotherCalendar) {

		int res = 0;

		

			// Cast to get a date :)
			DateBuffer dt = (DateBuffer) anotherCalendar;

			
			if (this.get(Calendar.YEAR) < dt.get(Calendar.YEAR))
				res = -1;
			else if (this.get(Calendar.YEAR) > dt.get(Calendar.YEAR))
				res = 1;
			else {

				// Years are equal -> go to month
				if (this.get(Calendar.MONTH) < dt.get(Calendar.MONTH))
					res = -1;
				else if (this.get(Calendar.MONTH) > dt.get(Calendar.MONTH))
					res = 1;
				else {
					// Months are equal -> go to day
					if (this.get(Calendar.DAY_OF_MONTH) < dt.get(Calendar.DAY_OF_MONTH))
						res = -1;
					else if (this.get(Calendar.DAY_OF_MONTH) > dt.get(Calendar.DAY_OF_MONTH))
						res = 1;
					else {
						res = 0;
					}
				}

			}

		
		System.out.println("Res : "+res);
		return res;
	}

	@Override
	public void _setValueFromString(String date) {
		
		this.setValue(this.convertFromString(date));
		
	}
	
	
	/* (non-Javadoc)
	 * @see uni.hd.cag.ooxoo.core.Buffer#convertFromString(java.lang.String)
	 */
	@Override
	protected GregorianCalendar convertFromString(String date) {
		
		if (date == null)
			throw new RuntimeException("date value is null!");

		// Split at -
		String[] splited = date.split("-");

		// array MUST be of length 3
		if (splited.length < 3)
			throw new RuntimeException("date value is invalid!");

		// Get year
		int year = java.lang.Integer.parseInt(splited[0]);
		// Get Month
		int month = java.lang.Integer.parseInt(splited[1]);
		// Get Day of month
		int day = java.lang.Integer.parseInt(splited[2].replaceAll(
				"(\\+|-|Z).*$", ""));

//		System.out.println("Read Date before in Object creation : " + year
//				+ "-" + month + "-" + day);

		// Get gmt

		String gmt;

		// If splited.length = 4 => we have a negative gmt
		if (splited.length == 4)
			gmt = "-" + splited[3];

		else if (splited[2].split("\\+").length > 1)
			gmt = "+" + splited[2].split("\\+")[1];
		else if (splited[2].split("Z").length > 1)
			gmt = "+0";
		else
			gmt = "+0";

//		System.out.println("Requested date timezone : Etc/GMT" + gmt);
		// build time
//		System.err.println("Date found year: "+year);
		this.set(Calendar.YEAR, year);
		this.set(Calendar.MONTH, month - 1);
		this.set(Calendar.DAY_OF_MONTH, day);
		this.setTimeZone(TimeZone.getTimeZone("Etc/GMT" + gmt));
		
		// Maintain value as it was (don't replace)
		return this.value;
	}
	

	@Override
	public boolean validate() throws SyntaxException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected TransferUnit createTransferUnit() {
		// TODO Auto-generated method stub
		return new TransferUnit() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return DateBuffer.this.toString();
			}
			
		};
	}

}
