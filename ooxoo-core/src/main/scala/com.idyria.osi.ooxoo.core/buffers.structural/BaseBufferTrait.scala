/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.structural

import scala.beans.BeanProperty
import com.idyria.osi.ooxoo.core.buffers.structural.io.IOBuffer

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


  protected var nextBuffer: Buffer = null


  protected var previousBuffer: Buffer = null

  def appendBuffer(buffer: Buffer): Buffer = {

    // Find Tail
    //-------------
    var currentBuffer: Buffer = this
    while ((currentBuffer getNextBuffer) != null)
      currentBuffer = currentBuffer getNextBuffer

    // Insert
    //-----------
    currentBuffer.insertNextBuffer(buffer)

  }

  def getNextBuffer : Buffer = this.nextBuffer

  /**
   * Sets this nextbuffer to provided buffer
   */
  def setNextBuffer(buffer: Buffer) : Buffer = {

    if (buffer == this.nextBuffer)
      return buffer


    this.nextBuffer = buffer
    buffer


  }

  def getPreviousBuffer : Buffer = this.previousBuffer

  /**
   * Only sets previous buffer to this one
   */
  def setPreviousBuffer(buffer:Buffer) : Buffer = {

    if (buffer == this.previousBuffer)
      return buffer


    this.previousBuffer = buffer
    buffer



  }


  def insertNextBuffer(buffer: Buffer): Buffer = {


    if(this.nextBuffer == buffer)
      return buffer


    // remove provided buffer from where it is now
    //-----------------
    /*  if (buffer!=null)
        buffer.remove*/

    // save next
    //------------
    var oldNext = this.nextBuffer

    // Set new next
    //-------------
    this.nextBuffer = buffer

    // Next gets this as previous
    //-----------
    if (this.nextBuffer!=null)
    	this.nextBuffer setPreviousBuffer(this)

    // New next gets old next as next
    //------------
    this.nextBuffer setNextBuffer(oldNext)

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
    while ((currentBuffer getPreviousBuffer) != null)
      currentBuffer = currentBuffer getPreviousBuffer

    // Insert
    //-----------
    currentBuffer insertPreviousBuffer (buffer)

  }

  def insertPreviousBuffer(buffer: Buffer): Buffer = {

    if(this.previousBuffer == buffer)
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
    if (this.previousBuffer!=null)
    	this.previousBuffer setNextBuffer(this)

    // Previous gets oldprevious as previous
    //------------
     this.previousBuffer setPreviousBuffer(oldPrevious)

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
  def streamOut(du: DataUnit) = {

    // Pass
    if (this.nextBuffer != null)
      this.nextBuffer -> du

  }
  def streamOut() = streamOut(createDataUnit)
  def streamOut( cl : DataUnit => DataUnit) = streamOut(cl(createDataUnit))



  def streamIn(du: DataUnit) = {

    // Pass
    if (this.previousBuffer!=null)
      this.previousBuffer <= du

  }

}
