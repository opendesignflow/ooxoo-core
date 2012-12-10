/**
 *
 */
package com.idyria.osi.ooxoo3.core.buffers.structural

import java.lang.reflect.Field
import com.idyria.osi.ooxoo3.core.buffers.structural.io.IOBuffer
import java.lang.reflect.Modifier

/**
 *
 * Trait for a buffer to be declared "Vertical", meaning it will produce the result of other sub buffers
 *
 * @author rleys
 *
 */
abstract class VerticalBuffer extends BaseBuffer {

  def allFields(cl: Class[_]): Set[Field] = {

    var allFields = Set[Field]()
    var currentClass: Class[_] = this.getClass
    while (currentClass != null) {
      for (field <- (currentClass.getFields()))
        allFields += field
      for (field <- (currentClass.getDeclaredFields()))
        allFields += field
      currentClass = currentClass.getSuperclass()

    }
    allFields

  }

  /**
   * This Override performs the work of a vertical buffer:
   *
   *  - The Push is propagated like a standart buffer
   *  - The local class is inspected for eventual field buffers to also push out to
   *
   */
  override def pushOut(du: DataUnit) = {

    // Normal pushout
    //----------
    super.pushOut(du)

    //println("Inspecting fields for class: "+this.getClass().getSimpleName())

    propagate(false) {
      
      (f , buffer) => buffer.pushOut {
            du =>
            // Type
            var elt = f.getAnnotation[element](classOf[element])
            if (elt!=null) {
              du.setElement(elt)
            }else {
              du.setAttribute(f.getAnnotation[attribute](classOf[attribute]))
            }
            
            // Hierarchical?
            if (classOf[VerticalBuffer].isAssignableFrom(f.getType()))
              du.setHierarchical(true)
            
            du
        }
      
    }
 

  }

  /**
   * Call up the provided closure to all the subfields that can be fetched in or pushedout
   * @param complete, true allows null fields to be seen, false does not
   */
  def propagate(nullFields : Boolean)(cl: (Field,Buffer) => Unit) = {
    
    var ioChainRoot: IOBuffer = null

    // Inspect for complete class hierarchy
    //------------
    this.allFields(this.getClass) filter {
      // Filter non annotated, null  and private values
      f => 
         f.setAccessible(true)
        ((f.getAnnotation[element](classOf[element]) != null || f.getAnnotation[attribute](classOf[attribute]) != null) 
            &&( nullFields || f.get(this)!=null ))
    } foreach {
      f =>
        
        //-- Get Buffer
        var fieldBuffer = (f.get(this).asInstanceOf[Buffer])

        // Clone and add IO Chain
        //--------

        //-- Create first: Find all the IO Buffers of current chain
        if (ioChainRoot == null) {
          var allIos = Set[IOBuffer]()
          this.foreachNextBuffer {
            b =>
              if (classOf[IOBuffer].isAssignableFrom(b.getClass()))
                allIos += (b.asInstanceOf[IOBuffer]).cloneIO
          }

          //-- Rewire them together
          if (allIos.size > 0) {
            ioChainRoot = allIos.head
            var currentInChain = ioChainRoot
            allIos = allIos.takeRight(0)
            for (iobuffer <- allIos) {
              currentInChain.insertNextBuffer(iobuffer)
              currentInChain = iobuffer
            }
          }
        }

        // Append io to current buffer
        fieldBuffer.appendBuffer(ioChainRoot)

        // Push and inject the element/attribute type
        //---------------
        cl(f,fieldBuffer)
        
        
        // Close hierarchy level for this
        
        println(s"Found field: ${f.getName()}")
    } // EOF All Fields
    
    // Notify end of level to IO Buffers
    if (ioChainRoot!=null) {
      ioChainRoot.foreachNextBuffer(b => (b.asInstanceOf[IOBuffer]).eofLevel)
    }
    
  }
  
}