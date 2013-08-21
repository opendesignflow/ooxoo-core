/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.structural

import java.lang.reflect.Field

import scala.reflect.runtime.universe._

import com.idyria.osi.ooxoo.core.buffers.datatypes.QName
import com.idyria.osi.ooxoo.core.buffers.structural.io.IOBuffer
import com.idyria.osi.ooxoo.core.utils.ScalaReflectUtils

import com.idyria.osi.tea.logging.TLog

/**
  Just a type marker
*/
trait HierarchicalBuffer {

}

/**
 *
 * Trait for a buffer to be declared "Vertical", meaning it will produce the result of other sub buffers
 *
 * @author rleys
 *
 */
trait VerticalBuffer extends BaseBuffer with HierarchicalBuffer {

  /**
   * This will be true if the element matching this Buffer has been received, and the next one must go to one sub field
   */
  protected var inHierarchy = false

  protected var stackSize = 0


  // Search
  //---------------------------

  /**
    Search for a Buffer in this object hierarchy that matches the provided search pattern
  */
  def search[T <: Buffer]( searchPath: String) : Buffer = {

    null

  }



  /**
   * This Override performs the work of a vertical buffer:
   *
   *  - The Push is propagated like a standart buffer
   *  - The local class is inspected for eventual field buffers to also push out to
   *
   */
  override def streamOut(du: DataUnit) = {

    // Normal streamOut
    //----------

    super.streamOut(du)

    // Attributes
    //------------------
    ScalaReflectUtils.getAnnotatedFields(this, classOf[xattribute]).filter(ScalaReflectUtils.getFieldValue(this, _)!=null).foreach{
      f =>


       // println(s"Streamout for attribute "+f.getName)
      try {


          //-- Get value
          var value = ScalaReflectUtils.getFieldValue(this,f).asInstanceOf[Buffer]

          //-- streamOut
        	value.appendBuffer(this.lastBuffer)
        	value -> {

        	  du =>
        	    var attribute = xattribute_base(f)
        	    du.attribute = attribute
        	    du

        	}

      } catch {
        case e : Throwable => throw new RuntimeException(s"An error occured while streamOut of attribute ${f.getName} in class ${getClass.getCanonicalName}",e)
      }
    }



    // Sub Elements
    //-------------------
    ScalaReflectUtils.getAnnotatedFields(this, classOf[xelement]).filter(ScalaReflectUtils.getFieldValue(this, _)!=null).foreach{
      f =>


        //-- Get value
        var value = ScalaReflectUtils.getFieldValue(this,f).asInstanceOf[Buffer]

        //-- streamOut
       	value.appendBuffer(this.lastBuffer)
       	value -> {
       	  du =>

       	    var element = xelement_base(f)
       	    du.element = element
       	    du

       	}




    }

    // Any
    //-----------------
    ScalaReflectUtils.getAnnotatedFields(this, classOf[any]).filter(ScalaReflectUtils.getFieldValue(this, _)!=null).foreach {

      f => 

        TLog.logFine(s"Streamout for any field in ${getClass}")

          //-- Get value
          var value = ScalaReflectUtils.getFieldValue(this,f).asInstanceOf[Buffer]

          //-- streamOut
          value.appendBuffer(this.lastBuffer)
          value.->


    }

    // Close hierarchy
    //--------------
    //println("Closing from VBuffer")
    super.streamOut(new DataUnit)

   

  }

  /**
   * Call up the provided closure to all the subfields that can be fetched in or pushedout
   * @param complete, true allows null fields to be seen, false does not
   */
  /*def propagate(nullFields: Boolean)(cl: (Symbol, Buffer) => Unit) = {


  }*/

  /**
   * Gets a data unit from chain
   */
  override def streamIn(du: DataUnit) = {

   // require(du.attribute != null || du.element != null)

   //println(s"In VBuffer streaming for ${getClass}")

    TLog.logFine("Got DU "+du)

    // If stack size > 0, we are not concerned
    //----------------------
    if (this.stackSize>0) {

      // Hierarchical = false and not an attribute -> decrease stack size
      if (du.attribute==null && du.hierarchical==false)
        this.stackSize-=1
      // Hierarchical = true -> increase stack size
      else if (du.hierarchical==true)
        this.stackSize+=1


        // If stackSize < 0 => we are finished so we can separate from our io buffer
        if (stackSize < 0 ) {
          this.lastBuffer.remove
        }


    }
    // End of Hierarchy
    // -> remove IO buffer
    //----------------
    else if (du.attribute==null && du.element==null && du.hierarchical==false && du.value==null) {

      if (this.lastBuffer.isInstanceOf[IOBuffer]) {

        TLog.logFine("VerticalBuffer: removing last IO buffer from chain because end of hierarchy")
        this.lastBuffer.remove
      }

    }
    // Attribute
    //-----------------
    else if (du.attribute != null) {


      this.getAttributeField(du.attribute.name) match {

        case Some(buffer) =>

          TLog.logFine(s"Found attribute Buffer to pass in value: ${du.value}")
          
          //buffer.dataFromString(du.value)
          buffer <= du
          
          TLog.logFine(s"-------> ${buffer}")

        case None => 

              this.getAnyField match {

                // Any
                case Some(any) => 

                    // Stream in DU to element
                    any <= du

                case None => 
                    TLog.logFine("---> No field instance returned for attribute <---")
              }     
      }


    }
    // Top Element
    //--------------------
    else if (du.element != null && !this.inHierarchy) {

      // If we are on an any Class, then don't do this
      //----------------
      if (!this.getClass.isAnnotationPresent(classOf[any])) 
        // Verify the element matches the expected one
        //---------------
        try {

          var expected = xelement_base(this)
          if (!du.element.name.equals(expected.name)) {
            throw new RuntimeException(s"Vertical buffer on ${VerticalBuffer.this.getClass()} expected an XML element named ${expected.name}, but got: ${du.element.name} instead")
          }

        } catch {
          // No @xelement annotation defined
          case e: java.lang.NullPointerException => throw new RuntimeException(s"Class ${VerticalBuffer.this.getClass()} MUST have an @xelement annotation!");
        }

      // Set hierarchy
      if (du.hierarchical)
          this.inHierarchy = true;

    }
    // Element In Hierarchy
    //--------------------
    else if (du.element != null) {

      TLog.logFine(s"Got an XML element for subfield: ${du.element.name}");

      

      // Proceed to element
      //-----------------------
      this.getElementField(du.element.name) match {

        // Found A Buffer Matching
        //-----------------------------
        case Some(buffer) =>

          TLog.logFine(s"Found element Buffer to pass in value: ${du.value}")

          // Increase Stack size if we are fetching a non hierarchical buffer as a hierarchy in this element
          // Typical: Elements that only contain a value, and thus are DataBuffers which are non hierarchical
          
          if (!buffer.isInstanceOf[HierarchicalBuffer]) {
              // Increase Stack Size
              this.stackSize+=1
          }
          

          // Clone this IO to the buffer
          //--------------
          buffer.appendBuffer(this.lastBuffer.asInstanceOf[IOBuffer].cloneIO);

          // Stream in DU to element
          //---------------------
          buffer <= du



          TLog.logFine(s"-------> ${buffer}")

        // Nothing -> Can we stream into any ?
        //---------------
        case None =>

            this.getAnyField match {

              // Any
              //------------
              case Some(any) => 

                  // Clone this IO to the buffer
                  //--------------
                  any.appendBuffer(this.lastBuffer.asInstanceOf[IOBuffer].cloneIO);

                  // Stream in DU to element
                  //---------------------
                  any <= du



              case None => 
                //println(s"---> No field instance returned for element ${du.element.name} under ${getClass} <---")
            }

            
      }


    }

  }


  /**
   * Get instance of a buffer set on the field that matches the provided Qname
   * @return None if nothing was found
   */
  private def getElementField(name : QName) : Option[Buffer] = {

    //TLog.logFine("*Looking for field for element: /"+name.getLocalPart()+"/")

    // Get all xelement annotated fields
    // Filter on annotations not maching name
    ScalaReflectUtils.getAnnotatedFields(this, classOf[xelement]).filter {
      a =>
        var xelt = xelement_base(a)
        //TLog.logFine("xelement annotation name:/"+xelt.name+"/");
        xelt !=null && name.getLocalPart().equals(xelt.name);
    } match {

       // If there is one, get existing value of instanciate
      case x if (!x.isEmpty) =>

        // Get Value
    	var fieldValue : Buffer = ScalaReflectUtils.getFieldValue(this, x.head)
    	if (fieldValue == null)
    	  fieldValue = ScalaReflectUtils.instanciateFieldValue(this, x.head)
        return Option(fieldValue)

      case _ =>
    }

    return None


  }


  /**
   * Finds the attribute field of current class, matching provided name
   * Instanciate or return the reference to the Buffer
   */
  private def getAttributeField(name : QName) : Option[AbstractDataBuffer[AnyRef]] = {

    TLog.logFine("*Looking for field for attribute: "+name.getLocalPart())

    // Get all xattribute fields, instanciate annotation and filter out the non matching names
    ScalaReflectUtils.getAnnotatedFields(this, classOf[xattribute]).filter {
      f =>
        	var xattr = xattribute_base(f);
//        	TLog.logFine("Found field with xattribute annotation, and name:"+xattr.name)
        	xattr!=null && name.getLocalPart().equals( xattr.name)

    } match {

      // If there is one, get existing value of instanciate
	    //-------------
      case x if (!x.isEmpty) =>

        var targetField = x.head

         TLog.logFine(s"*Found field: $name")


      	// Get Value
      	var fieldValue : AbstractDataBuffer[AnyRef] = ScalaReflectUtils.getFieldValue(this, targetField)

      	// Instanciate
      	//------------------
      	if (fieldValue==null) {

      	  TLog.logFine(s"Instanciating field for attribute: $name")
      	  fieldValue = ScalaReflectUtils.instanciateFieldValue(this, targetField)

        }

        // Return
        return Option(fieldValue)

      // NOthing -> Ignore
      case  _ => return None


    }
  //return None
  }


  /**
    Only returns the first found field marked as @any
  */
  protected def getAnyField : Option[Buffer] = ScalaReflectUtils.getAnnotatedFields(this, classOf[any]).headOption match {

      case Some(field) => 

            // Get Value
            var fieldValue : Buffer = ScalaReflectUtils.getFieldValue(this, field)

            // Instanciate
            //------------------
            if (fieldValue==null) {

              fieldValue = ScalaReflectUtils.instanciateFieldValue(this, field)
 
            }

            // Return
            return Option(fieldValue)
      case None => None

  }
    

}

object VerticalBuffer {



  /**
   * Returns all the fields that have an annotation
   */
/*  def allFields(base: AnyRef): Iterable[Symbol] = {

    // Get type tag
    //--------------------
    var baseTT = scala.reflect.runtime.universe.manifestToTypeTag(scala.reflect.runtime.currentMirror, Manifest.singleType(base))

    baseTT.tpe.foreach {

      t => TLog.logFine("Available: " + t.toString())

    }

    baseTT.tpe.members.filter(
      (m: Any) =>
        m.asInstanceOf[scala.reflect.api.Universe#Symbol].isTerm && !m.asInstanceOf[scala.reflect.api.Universe#Symbol].isMethod).filter {
        f =>

          // Filter based on annotation presence
          TLog.logFine("Available: " + f)
          f.annotations.find(a => (a.tpe.erasure == typeOf[xelement] || a.tpe.erasure == typeOf[xattribute])) match {
            case None => false
            case _ => true
          }
        //f.annotations.filter( a => (a.tpe.erasure == typeOf[xelement] || a.tpe.erasure == typeOf[xattribute]))

      }.asInstanceOf[Iterable[Symbol]]

    //typeTag.members.filter(m => m.isTerm && !m.isMethod)

    //scala.reflect.runtime.Mirror.classToType(base.getClass()).members.filter(m => m.isTerm && !m.isMethod)

    /*typeTag.tpe.erasure.members.foreach{

      t : scala.reflect.api.Universe#Symbol => TLog.logFine("Available: "+t.toString())

    }*/

    //List()
  }
*/
}
