 /*
 * #%L
 * Core runtime for OOXOO
 * %%
 * Copyright (C) 2006 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.odfi.ooxoo.core.buffers.datatypes

import org.odfi.ooxoo.core.buffers.structural.AbstractDataBuffer

import scala.language.implicitConversions
import java.text.SimpleDateFormat
import java.util.GregorianCalendar
import java.text.ParsePosition
import java.util.Formatter.DateTime
import java.util.Calendar

/**
 * DateTimeBuffer bears per default the time at object creation
 *
 */
class DateTimeBuffer extends AbstractDataBuffer[java.util.GregorianCalendar] with Comparable[java.util.GregorianCalendar] {

  // Default Constructor
  //--------------------
  this.data = new GregorianCalendar

  /**
   * Selected format detected when parsing
   */
  var selectedFormat: Option[String] = None

  
  // Comparisons
  //----------
  def isBeforeNow = {
    
    this.data.before(new GregorianCalendar)
    
    
  }
  
   def isAfterNow = {
    
    this.data.after(new GregorianCalendar)
    
    
  }
  
  // Update
  //-------------------
  def addMinutes(minutes:Int) = {
    this.data.add(Calendar.MINUTE,minutes)
  }
  
  // IO
  //-----------
  def dataFromString(str: String): java.util.GregorianCalendar = {

    //this.data = java.lang.Boolean.parseBoolean(str)
    //this.data

    // Find Format if necessary
    //---------------
    selectedFormat = DateTimeBuffer.availableFormats.find {
      format =>

        try {
          // Parse
          //-------------
          var dateFormat = new SimpleDateFormat(format)
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

          true
        } catch {

          //-- Format fail
          case e: Throwable => false
        }

    } match {
      case None => throw new IllegalArgumentException(s"DateTimeBuffer could not parse input $str agains any of the configured format: ${DateTimeBuffer.availableFormats}")
      case r => r
    }

    this.data

  }

  def dataToString: String = if (data != null) String.format("%1$tY-%1$tm-%1$tdT%1$tH:%1$tM:%1$tS%tz", this.data);
  else null

  override def toString: String = this.dataToString

  def equals(comp: DateTimeBuffer): java.lang.Boolean = this.data == comp.data

  def compareTo(comp: java.util.GregorianCalendar): Int = this.data.compareTo(comp)

}

object DateTimeBuffer {

  /**
   * List of possible date formats
   */
  var availableFormats = List("yyyy-MM-dd'T'HH:mm:ssX", "yyyy-MM-dd HH:mm:ss")

  def apply() = new DateTimeBuffer

  implicit def convertDateTimeBufferToCalendar(b: DateTimeBuffer): java.util.GregorianCalendar = b.data
  implicit def convertCalendarToDateTimeBuffer(c: java.util.GregorianCalendar): DateTimeBuffer = {

    var dtb = new DateTimeBuffer
    dtb.data = c
    dtb

  }
}
