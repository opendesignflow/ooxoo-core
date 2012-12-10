/**
 *
 */
package com.idyria.osi.ooxoo3.core.buffers.structural

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

  /**
   * Creates a data unit from this buffer.
   * This is used when the buffer is the starting point of a pushOut or fetchIn
   */
  def createDataUnit : DataUnit
  
  
  /**
   * Propagates a data unit coming from previous buffer in chain
   */
  def pushOut(du : DataUnit)
  
  /**
   * Propagates a data unit created locally out to the next buffer chain (right direction)
   */
  def pushOut
  
  /**
   * Propagates a data unit created locally out to the next buffer chain
   * The provided closure is called on the locally created dataunit for injection purpose
   */
  def pushOut( cl : DataUnit => DataUnit)
  
  /**
   * Alias for pushOut
   */
  def -> = pushOut
  
  /**
   * Alias for propagatin pushOut
   */
  def ->(du: DataUnit) = pushOut(du)
  
  /**
   * Fetchin a data unit from the next buffer
   */
  def fetchIn(du: DataUnit)
  
  /**
   *  Alias for fetchIn
   */
  def <= (du: DataUnit) = fetchIn(du)
  
  /**
   * Gets the next buffer
   */
  def getNextBuffer : Buffer
  
  /**
   * Appends a buffer at the end of the chain
   * 
   * @return The inserted buffer
   */
  def appendBuffer(buffer : Buffer) : Buffer
  
  /**
   * Inserts a buffer after this buffer
   * @return The inserted buffer
   */
  def insertNextBuffer(buffer: Buffer) : Buffer
  
  /**
   * Apply a function to all the next buffers, including this one
   */
  def foreachNextBuffer(closure: Buffer => Unit) = {

    var currentBuffer = this
    while (currentBuffer != null) {
      closure(currentBuffer)
      currentBuffer = currentBuffer.getNextBuffer
    }

  }
  
  /**
   * Get the previous buffer
   */
  def getPreviousBuffer : Buffer
  
  /**
   * Sets the provided buffer at the head of the buffer chain
   * @return The inserted buffer
   */
  def prependBuffer(buffer: Buffer) : Buffer
  
  /**
   * Inserts the provided buffer before this buffer
   * @return The inserted buffer
   */
  def insertPreviousBuffer(buffer: Buffer) : Buffer
  
  
}
