/**
 *
 */
package com.idyria.osi.ooxoo3.core.buffers.structural

import scala.beans.BeanProperty
import com.idyria.osi.ooxoo3.core.buffers.structural.io.IOBuffer

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
abstract class BaseBuffer extends Buffer {

  @BeanProperty
  protected var nextBuffer: Buffer = null

  @BeanProperty
  protected var previousBuffer: Buffer = null

  def appendBuffer(buffer: Buffer): Buffer = {

    // Find Tail
    //-------------
    var currentBuffer: Buffer = this
    while ((currentBuffer getNextBuffer) != null)
      currentBuffer = currentBuffer getNextBuffer

    // Insert
    //-----------
    currentBuffer insertNextBuffer (buffer)

  }

  def insertNextBuffer(buffer: Buffer): Buffer = {

    require(this.nextBuffer != buffer)

    // save next
    //------------
    var oldNext = this.nextBuffer

    // Set new next
    //-------------
    this.nextBuffer = buffer

    // Old next gets new next as previous
    //----------
    if (oldNext != null)
      oldNext insertPreviousBuffer (buffer)
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

    require(this.previousBuffer != buffer)

    // save previous
    //------------
    var oldPrevious = this.previousBuffer

    // Set new previous
    //-------------
    this.previousBuffer = buffer

    // Old prev gets new prev as next
    //----------
    if (oldPrevious != null)
      oldPrevious insertNextBuffer (buffer)
    buffer

  }

  /**
   * This implementation simply passes to neighbor
   */
  def pushOut(du: DataUnit) = {

    // Pass
    if (this.nextBuffer != null)
      this.nextBuffer -> du

  }  
  def pushOut = pushOut(createDataUnit)
  def pushOut( cl : DataUnit => DataUnit) = pushOut(cl(createDataUnit))

  

  /**
   * Default implementation
   */
  def fetchIn = {

  }

  def fetchIn(du: DataUnit) = {

    // Pass
    
  }

}