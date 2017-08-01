package com.idyria.osi.ooxoo.core.buffers.id

import com.idyria.osi.ooxoo.model.ModelBuilder
import scala.reflect.ClassTag

trait IdAndRefIdModelBuilder extends ModelBuilder {
  
  def withElementID = withTrait(classOf[ElementWithID])
  def referenceElementID[T <: ElementWithID](implicit tag : ClassTag[T]) :Unit ={
    withTrait(classOf[ElementWithReferenceID[T]].getCanonicalName+s"[${tag.runtimeClass.getCanonicalName}]")
  }
  def referenceElementID(tag : String) : Unit = {
    withTrait(classOf[ElementWithReferenceID[_]].getCanonicalName+s"[${tag}]")
  }
  
}