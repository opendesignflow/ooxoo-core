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
/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.structural

import scala.beans.BeanProperty
import com.idyria.osi.ooxoo.core.buffers.structural.io.IOBuffer
import javax.persistence.Transient

/**
 * The Base Buffer class is a default implementation providing Buffer infrastructure.
 * It is used by the main ooxoo buffer classe, and is intended to be usually derived by the user.
 *
 * The Buffer trait can be used for specific application, but usually, simply deriving this class will
 * be sufficient
 *
 * To be implented by subclasses:
 *
 * 	<ul>
 * 		<li>createDataUnit : DataUnit</li>
 *  </ul>
 *
 * @author rleys
 *
 */
trait BaseBufferTrait extends Buffer {

  @Transient
  protected var nextBuffer: Buffer = null

  @Transient
  protected var previousBuffer: Buffer = null

  def appendBuffer(buffer: Buffer): Buffer = {

    // Find Tail
    //-------------
    var currentBuffer: Buffer = this
    while ((currentBuffer.getNextBuffer) != null)
      currentBuffer = currentBuffer.getNextBuffer

    // Insert
    //-----------
    currentBuffer.insertNextBuffer(buffer)

  }

  def getNextBuffer: Buffer = this.nextBuffer

  /**
   * Sets this nextbuffer to provided buffer
   */
  def setNextBuffer(buffer: Buffer): Buffer = {

    if (buffer == this.nextBuffer)
      return buffer
    this.nextBuffer = buffer

   /* if (buffer != null)
      buffer.setPreviousBuffer(this)*/

    buffer

  }

  def getPreviousBuffer: Buffer = this.previousBuffer

  /**
   * Only sets previous buffer to this one
   */
  def setPreviousBuffer(buffer: Buffer): Buffer = {

    if (buffer == this.previousBuffer)
      return buffer
    this.previousBuffer = buffer

    if (buffer != null)
      buffer.setNextBuffer(this)

    buffer

  }

  def insertNextBuffer(buffer: Buffer): Buffer = {

    if (this.nextBuffer == buffer || this == buffer)
      return buffer

    // remove provided buffer from where it is now
    //-----------------
    /* if (buffer!=null)
        buffer.remove*/

    // save next
    //------------
    var oldNext = this.nextBuffer

    // Set new next
    //-------------
    this.nextBuffer = buffer

    // Next gets this as previous
    //-----------
    if (this.nextBuffer != null) {
      this.nextBuffer setPreviousBuffer (this)
    }

    // New next gets old next as next
    //------------
    this.nextBuffer setNextBuffer (oldNext)

    // Old next gets new next as previous
    //----------
    if (oldNext != null)
      oldNext setPreviousBuffer (buffer)
    buffer
  }

  /**
   * Return a set with all the next buffers
   */
  def allNextBuffers: Set[Buffer] = {

    var currentBuffer = this.nextBuffer
    var allNexts = Set[Buffer]()
    while (currentBuffer != null) {
      allNexts += currentBuffer
      currentBuffer = currentBuffer.getNextBuffer
    }
    allNexts
  }

  def prependBuffer(buffer: Buffer): Buffer = {

    // Find Head
    //-------------
    var currentBuffer: Buffer = this
    while ((currentBuffer.getPreviousBuffer) != null)
      currentBuffer = currentBuffer.getPreviousBuffer

    // Insert
    //-----------
    currentBuffer insertPreviousBuffer (buffer)

  }

  def insertPreviousBuffer(buffer: Buffer): Buffer = {

    if (this.previousBuffer == buffer)
      return buffer

    // remove provided buffer from where it is now
    //-----------------
    /* if (buffer!=null)
        buffer.remove*/

    // save previous
    //------------
    var oldPrevious = this.previousBuffer

    // Set new previous
    //-------------
    this.previousBuffer = buffer

    // Previous gets this as next
    //------------
    if (this.previousBuffer != null)
      this.previousBuffer setNextBuffer (this)

    // Previous gets oldprevious as previous
    //------------
    this.previousBuffer setPreviousBuffer (oldPrevious)

    // Old prev gets new prev as next
    //----------
    if (oldPrevious != null) {
      oldPrevious setNextBuffer (buffer)

    }
    buffer

  }

  /**
   * This implementation simply passes to neighbor
   */
  /*override def streamOut(du: DataUnit) : Unit = {

    // Pass
    if (this.nextBuffer != null)
      this.nextBuffer.streamOut(du)

  }*/
  /*def streamOut() = streamOut(createDataUnit)
  def streamOut( cl : DataUnit => DataUnit) = streamOut(cl(createDataUnit))
*/

  override def streamIn(du: DataUnit) = {

    // Pass
    if (this.previousBuffer != null)
      this.previousBuffer <= du
      
   
  }
  
  /**
   * Post Streamin call is made by the implementations, because streamin end depends on the type of element currently being read
   */
  def postStreamIn = {
    
  }

}
