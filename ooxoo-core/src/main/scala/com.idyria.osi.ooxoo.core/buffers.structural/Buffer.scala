/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.structural

import com.idyria.osi.ooxoo.core.buffers.structural.io.IOBuffer
import java.util.concurrent.locks.ReentrantLock

/**
 *
 * Base common Buffer trait
 *
 * This trait defines the base methods for the buffer system.
 *
 * @author rleys
 *
 */
trait Buffer {

  // Data Unit Interface
  //---------------------

  /**
   * Creates a data unit from this buffer.
   * This is used when the buffer is the starting point of a streamOut or streamIn
   */
  def createDataUnit: DataUnit = new DataUnit

  /**
   * Import a Data Unit in the current buffer
   */
  def importDataUnit(du: DataUnit): Unit = {

  }
  // Streamout/in Interface
  //---------------------------------

  var cleanLock = new ReentrantLock

  def lockIO = cleanLock.lock()
  def unlockIO = cleanLock.unlock()

  /**
   * ONly front Buffer can clean the IO chain, and if I/Os are not locked
   */
  def cleanIOChain = {

    if (this.getPreviousBuffer == null && cleanLock.getHoldCount() == 0) {
      
      //println("Clean IO from "+getClass)
      this.foreachNextBuffer {
        case io: IOBuffer ⇒

          //println("Removing IO Buffer")
          io.remove
        case _ ⇒
      }
    }

  }

  /**
   * Return all the buffers that ar of IO type and connected to the end of this buffer
   */
  def getIOChain: Option[IOBuffer] = {

    this.foreachNextBuffer {
      case io: IOBuffer ⇒

        return Some(io)
      case _ ⇒
    }

    None

  }

  /**
   * Pushs a data unit coming from previous buffer in chain
   *
   * After Streamout, this method removes the possible remaining I/O Chain
   * So this method should always be called after implementation streamOut
   *
   */
  def streamOut(du: DataUnit): Unit = {

    // Pass
    if (this.getNextBuffer != null)
      this.getNextBuffer.streamOut(du)

    //println("Clean from top")
    cleanIOChain

  }

  /**
   * Pushs a data unit created locally out to the next buffer chain (right direction)
   */
  //def streamOut() : Unit

  def streamOut(): Unit = {

    streamOut(createDataUnit)
  }

  /**
   * Pushs a data unit created locally out to the next buffer chain
   * The provided closure is called on the locally created dataunit for injection purpose
   */
  def streamOut(cl: DataUnit ⇒ DataUnit): Unit = streamOut(cl(createDataUnit))

  /**
   * Streamin without dataUnit is used if element is supposed to produce input DataUnits.
   * Per default this method does nothing
   */
  def streamIn = {}

  /**
   * streamIn a data unit from the next buffer
   */
  def streamIn(du: DataUnit)

  /**
   *  Alias for streamIn
   */
  def <=(du: DataUnit) = streamIn(du)

  //-----------------------------
  // Push / Pull Interface
  //-----------------------------

  /**
   * Push Left and Right, and creates the data unit if none has been given
   *
   */
  def push(du: DataUnit): Unit = {

    // DU Preparation
    //--------
    var dataUnit = du
    if (dataUnit == null) {
      dataUnit = this.createDataUnit
    } else if (dataUnit.value == null) {
      dataUnit += this.createDataUnit
    }

    // Push
    //----------
    this.pushRight(dataUnit)
    this.pushLeft(dataUnit)
  }
  def push: Unit = push(DataUnit())

  /**
   * Push a Data Unit to the right of the buffer chain
   * The user should override this method to be able to react on Push
   */
  def pushRight(du: DataUnit): Unit = {

    if (getNextBuffer != null) {
      getNextBuffer.pushRight(du)
    }

  }

  /**
   * Push a Data Unit to the left of the buffer chain
   * The user should override this method to be able to react on Push
   */
  def pushLeft(du: DataUnit): Unit = {

    if (getPreviousBuffer != null) {
      getPreviousBuffer.pushLeft(du)
    }

  }

  /**
   * Request value pull from right buffer
   * If we have someone on the left, respond using pull(dataUnit)
   *
   * @return The Data Unit to be pulled in
   */
  def pull(indu: DataUnit): DataUnit = {

    var du: DataUnit = null

    // Pull Right
    if (getNextBuffer != null)
      du = getNextBuffer.pull(indu)

    // Create Data Unit if necessary
    if (du == null)
      du = this.createDataUnit

    // Pull in if no buffer on the left
    if (getPreviousBuffer == null)
      this.importDataUnit(du)

    du

  }
  def pull(): DataUnit = this.pull(DataUnit())

  // Buffer Chain Management
  //-------------------------------

  def printForwardChain: String = {

    var buffers = Set[Buffer](this)
    this.foreachNextBuffer {
      b ⇒
        if (b != null)
          buffers += b
    }
    buffers.mkString(" -> ")

  }

  def printBackwardsChain: String = {

    var buffers = Set[Buffer](this.firstBuffer)
    this.foreachNextBuffer {
      b ⇒
        if (b != null)
          buffers += b
    }
    buffers.mkString(" <- ")

  }

  /**
   * Remove ourselves, and chain previous and next together
   */
  def remove: Buffer = {

    // Get previous and next
    var previous = this.getPreviousBuffer
    var next = this.getNextBuffer

    // Next becomes next of previous
    //----------
    if (previous != null)
      previous.setNextBuffer(next)

    // Previous becomes previous of next
    //------------
    if (next != null)
      next.setPreviousBuffer(previous)

    // Return this
    this.setPreviousBuffer(null)
    this.setNextBuffer(null)
    this
  }

  /**
   * Gets the next buffer
   */
  def getNextBuffer: Buffer

  /**
   * Set the next buffer
   */
  def setNextBuffer(buffer: Buffer): Buffer

  /**
   * Appends a buffer at the end of the chain
   *
   * @return The inserted buffer
   */
  def appendBuffer(buffer: Buffer): Buffer

  /**
   * Appends a Buffer at the end of the chain
   *
   * @return The inserted buffer
   */
  def -(buffer: Buffer): Buffer = appendBuffer(buffer)

  /**
   * Inserts a buffer after this buffer
   * @return The inserted buffer
   */
  def insertNextBuffer(buffer: Buffer): Buffer

  /**
   * Apply a function to all the next buffers, including this one
   */
  def foreachNextBuffer(closure: Buffer ⇒ Unit) = {

    var currentBuffer = this
    while (currentBuffer != null) {
      if (currentBuffer != null)
        closure(currentBuffer)
      currentBuffer = currentBuffer.getNextBuffer
    }

  }

  /**
   * Apply a function to all the previous buffers, including this one
   */
  def foreachPreviousBuffer(closure: Buffer ⇒ Unit) = {

    var currentBuffer = this
    while (currentBuffer != null) {
      if (currentBuffer != null)
        closure(currentBuffer)
      currentBuffer = currentBuffer.getPreviousBuffer
    }

  }

  /**
   * Get the last buffer in the chain
   */
  def lastBuffer: Buffer = {

    var res = this
    this.foreachNextBuffer {
      b ⇒ res = b
    }
    res

  }

  /**
   * Get the first buffer in the chain
   */
  def firstBuffer: Buffer = {

    var res = this
    this.foreachPreviousBuffer {
      b ⇒ res = b
    }
    res

  }

  /**
   * Get the previous buffer
   */
  def getPreviousBuffer: Buffer

  def setPreviousBuffer(buffer: Buffer): Buffer

  /**
   * Sets the provided buffer at the head of the buffer chain
   * @return The inserted buffer
   */
  def prependBuffer(buffer: Buffer): Buffer

  /**
   * Inserts the provided buffer before this buffer
   * @return The inserted buffer
   */
  def insertPreviousBuffer(buffer: Buffer): Buffer

}
