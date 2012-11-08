package com.idyria.osi.ooxoo.core.buffers.datatypes;

/**
 * FIXME
 * @author rtek
 *
 */
public class DurationBuffer extends  DateTimeBuffer {

	private int year = 0;
	private int month = 0;
	private int day = 0;
	private int hour = 0;
	private int minute = 0;
	private int second = 0;
	
	private boolean minus = false;
	
	public DurationBuffer() {
		
	}
	
	public DurationBuffer(int minute,int second) {
		this(0,0,0,0,minute,second);
	}
	
	public DurationBuffer(int year,int month,int day) {
		this(year,month,day,0,0,0);
	}
	
	public DurationBuffer(int year,int month,int day,int hour,int minute,int second) {
		
		// Set values
		this.year = Math.abs(year); this.month = Math.abs(month); this.day = Math.abs(day);
		this.hour = Math.abs(hour); this.minute = Math.abs(minute); this.second = Math.abs(second);
		
		// is minus?
		if (year<0 || month <0 || day<0 || hour <0 || minute<0 || second<0)
			this.minus = true;
		
	}

	
	public String toString() {
		
		String res ;
		
		// Minus?
		if (!this.minus) res = "P";
		else			res = "-P";
		
		// Append datas
		if (year>0)
			res+=year+"Y";
		if (month>0)
			res+=month+"M";
		if (day>0)
			res+=day+"D";
		
		if (hour>0 || minute > 0 || second >0)
			res+="T";
		
		if (hour>0)
			res+=hour+"H";
		if (minute>0)
			res+=minute+"M";
		if (second>0)
			res+=second+"S";
		
		return res;
		
	}
	
	public static DurationBuffer fromString(String duration) {
		
		DurationBuffer res = null;
		
		try {
			
			if (duration== null)
				throw new Exception("Duration value is null!!");
			
			boolean minus = duration.startsWith("-P")? true:false;
			
			String workon = duration.replaceAll("^.*P","");
			
			// Create res
			int year = 0; int month = 0; int day = 0;
			int hour = 0; int minute = 0; int second = 0;
			
			// Split workon for time
			String[] base = workon.split("T");
			
			// Start with date part
			workon = base[0];
			String[] arr ;
			
			// Year
			if (workon.contains("Y")) {
				arr = workon.split("Y");
				year = java.lang.Integer.valueOf(arr[0]);
				workon = workon.replaceAll("^.*Y","");	
			}
			
			
			if (workon.contains("M")) {	
				arr = workon.split("M");
				month = java.lang.Integer.valueOf(arr[0]);
				workon = workon.replaceAll("^.*M","");	
			}
			
			
			if (workon.contains("D")) {	
				arr = workon.split("D");
				day = java.lang.Integer.valueOf(arr[0]);
				workon = workon.replaceAll("^.*D","");	
			}
			
			// Go to time
			if (base.length>1) {
				
				workon = base[1];
				
				if (workon.contains("H")) {	
					arr = workon.split("H");
					hour = java.lang.Integer.valueOf(arr[0]);
					workon = workon.replaceAll("^.*H","");	
				}			
				if (workon.contains("M")) {	
					arr = workon.split("M");
					minute = java.lang.Integer.valueOf(arr[0]);
					workon = workon.replaceAll("^.*M","");	
				}			
				if (workon.contains("S")) {	
					arr = workon.split("S");
					second = java.lang.Integer.valueOf(arr[0]);
					workon = workon.replaceAll("^.*S","");	
				}
			}
			
			// Set values
			res = new DurationBuffer(year,month,day,hour,minute,second);
			
			
			
			
		} catch (Exception ex) {
			res = null;
		}
		
		return res;
		
	}
	
	/**
	 * @return Returns the day.
	 */
	public int getDay() {
		return day;
	}

	/**
	 * @param day The day to set.
	 */
	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * @return Returns the hour.
	 */
	public int getHour() {
		return hour;
	}

	/**
	 * @param hour The hour to set.
	 */
	public void setHour(int hour) {
		this.hour = hour;
	}

	/**
	 * @return Returns the minus.
	 */
	public boolean isMinus() {
		return minus;
	}

	/**
	 * @param minus The minus to set.
	 */
	public void setMinus(boolean minus) {
		this.minus = minus;
	}

	/**
	 * @return Returns the minute.
	 */
	public int getMinute() {
		return minute;
	}

	/**
	 * @param minute The minute to set.
	 */
	public void setMinute(int minute) {
		this.minute = minute;
	}

	/**
	 * @return Returns the month.
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * @param month The month to set.
	 */
	public void setMonth(int month) {
		this.month = month;
	}

	/**
	 * @return Returns the second.
	 */
	public int getSecond() {
		return second;
	}

	/**
	 * @param second The second to set.
	 */
	public void setSecond(int second) {
		this.second = second;
	}

	/**
	 * @return Returns the year.
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year The year to set.
	 */
	public void setYear(int year) {
		this.year = year;
	}
	
	
	
	
}
