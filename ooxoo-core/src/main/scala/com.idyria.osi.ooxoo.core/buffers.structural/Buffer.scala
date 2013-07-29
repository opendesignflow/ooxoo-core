/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.structural

/**
 *
 * Base common Buffer trait
 *
 * This trait defines the base methods for the buffer system.
 *
 * @author rleys
 *
 */
trait Buffer{





  // Streamout/in Interface
  //---------------------------------

  /**
   * Creates a data unit from this buffer.
   * This is used when the buffer is the starting point of a streamOut or streamIn
   */
  def createDataUnit : DataUnit = null


  /**
   * Pushs a data unit coming from previous buffer in chain
   */
  def streamOut(du : DataUnit)

  /**
   * Pushs a data unit created locally out to the next buffer chain (right direction)
   */
  def streamOut

  /**
   * Pushs a data unit created locally out to the next buffer chain
   * The provided closure is called on the locally created dataunit for injection purpose
   */
  def streamOut( cl : DataUnit => DataUnit)

  /**
   * Alias for streamOut
   */
  def -> = streamOut

  /**
   * Alias for propagatin streamOut
   */
  def ->(du: DataUnit) = streamOut(du)

  /**
   * Alias for streamOut with closure on DU
   */
  def ->( cl : DataUnit => DataUnit)  = streamOut(cl)



  /**
    Streamin without dataUnit is used if element is supposed to produce input DataUnits.
    Per default this method does nothing
  */
  def streamIn = {}


  /**
   * streamIn a data unit from the next buffer
   */
  def streamIn(du: DataUnit)

  /**
   *  Alias for streamIn
   */
  def <= (du: DataUnit) = streamIn(du)


  //-----------------------------
  // Push / Pull Interface
  //-----------------------------

  /**
    Push Left and Right, and creates the data unit if none has been given

  */
  def push( du: DataUnit ) : Unit = {

    // DU Preparation
    //--------
    var dataUnit = du
    if (dataUnit==null) {
      dataUnit = this.createDataUnit
    }

    // Push
    //----------
    this.pushRight(dataUnit)
    this.pushLeft(dataUnit)
  }
  def push : Unit = push(null)

  /**
    Push a Data Unit to the right of the buffer chain
    The user should override this method to be able to react on Push
  */
  def pushRight( du: DataUnit ) : Unit = {

    if (getNextBuffer!=null) {
      getNextBuffer.pushRight(du)
    }

  }

  /**
    Push a Data Unit to the left of the buffer chain
    The user should override this method to be able to react on Push
  */
  def pushLeft( du: DataUnit ) : Unit  = {

    if (getPreviousBuffer!=null) {
      getPreviousBuffer.pushLeft(du)
    }

  }


  /**
    Request value pull from right buffer
    If we have someone on the left, respond using pull(dataUnit)

    @return The Data Unit to be pulled in
  */
  def pull : DataUnit = {

    var du : DataUnit = null

    // Pull Right
    if (getNextBuffer!=null)
      du = getNextBuffer.pull


    // Create Data Unit if necessary
    if (du==null)
      du = this.createDataUnit

    // Pull in if no buffer on the left
    if (getPreviousBuffer==null)
      this.pull(du)

    du
  }


  /**
    Called on the buffer when some datas get pulled in
  */
  def pull(du:DataUnit) = {

  }




  // Buffer Chain Management
  //-------------------------------


  def printForwardChain : String = {

    var buffers = Set[Buffer](this)
    this.foreachNextBuffer {
      b =>
          if (b!=null)
          buffers+=b
    }
    buffers.mkString(" -> ")

  }


  /**
   * Remove ourselves, and chain previous and next together
   */
  def remove : Buffer = {

    // Get previous and next
    var previous = this.getPreviousBuffer
    var next = this.getNextBuffer

    // Next becomes next of previous
    //----------
    if (previous!=null)
      previous.setNextBuffer(next)

    // Previous becomes previous of next
    //------------
    if (next!=null)
      next.setPreviousBuffer(previous)

    // Return this
    this.setPreviousBuffer(null)
    this.setNextBuffer(null)
    this
  }

  /**
   * Gets the next buffer
   */
  def getNextBuffer : Buffer

  /**
   * Set the next buffer
   */
  def setNextBuffer(buffer:Buffer) : Buffer

  /**
   * Appends a buffer at the end of the chain
   *
   * @return The inserted buffer
   */
  def appendBuffer(buffer : Buffer) : Buffer

  /**
    Appends a Buffer at the end of the chain

    @return The inserted buffer
   */
  def - (buffer : Buffer) : Buffer = appendBuffer(buffer)

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
      if (currentBuffer!=null)
    	  closure(currentBuffer)
      currentBuffer = currentBuffer.getNextBuffer
    }

  }

  /**
   * Get the last buffer in the chain
   */
  def lastBuffer : Buffer = {

    var res = this
    this.foreachNextBuffer {
      b => res =  b
    }
    res

  }


  /**
   * Get the previous buffer
   */
  def getPreviousBuffer : Buffer

  def setPreviousBuffer(buffer:Buffer) : Buffer

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
