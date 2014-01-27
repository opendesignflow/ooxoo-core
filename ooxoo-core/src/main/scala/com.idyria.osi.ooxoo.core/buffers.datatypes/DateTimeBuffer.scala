package com.idyria.osi.ooxoo.core.buffers.datatypes

import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer

import scala.language.implicitConversions

import java.util._
import java.text._

/**
 * DateTimeBuffer bears per default the time at object creation
 *
 */
class DateTimeBuffer extends AbstractDataBuffer[java.util.GregorianCalendar] with Comparable[java.util.GregorianCalendar] {

  // Default Constructor
  //--------------------
  this.data = new GregorianCalendar

  def dataFromString(str: String): java.util.GregorianCalendar = {

    //this.data = java.lang.Boolean.parseBoolean(str)
    //this.data

    // Parse
    //-------------
    var dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
    var date = dateFormat.parse(str, new ParsePosition(0))
    if (date == null) {
      throw new RuntimeException(s"""Could not parse date: $str , does not match correct format: yyyy-MM-dd'T'HH:mm:ssX""")
    }

    // Create Gregoran Calendar from this date
    //----------------
    this.data = new GregorianCalendar

    /* println(s"parsed with offset: ${date.getTimezoneOffset()}")
        // The Timezone offset is relative to the current one in seconds
        this.data.getTimeZone.setRawOffset((date.getTimezoneOffset()))*/

    this.data.setTime(date)
    this.data

  }

  def dataToString: String = if (data != null) String.format("%1$tY-%1$tm-%1$tdT%1$tH:%1$tM:%1$tS%tz", this.data); else null

  override def toString: String = this.dataToString

  def equals(comp: DateTimeBuffer): java.lang.Boolean = this.data == comp.data

  def compareTo(comp: java.util.GregorianCalendar): Int = this.data.compareTo(comp)

}

object DateTimeBuffer {

  def apply() = new DateTimeBuffer
  
  implicit def convertDateTimeBufferToCalendar(b: DateTimeBuffer): java.util.GregorianCalendar = b.data
  implicit def convertCalendarToDateTimeBuffer(c: java.util.GregorianCalendar): DateTimeBuffer = {

    var dtb = new DateTimeBuffer
    dtb.data = c
    dtb

  }
}
