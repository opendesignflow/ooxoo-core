package com.idyria.osi.ooxoo.core.buffers.extras.listeners

import com.idyria.osi.ooxoo.core.buffers.structural.Buffer
import com.idyria.osi.ooxoo.core.buffers.structural.DataUnit
import scala.reflect.ClassTag
import com.idyria.osi.ooxoo.core.buffers.structural.BaseBufferTrait

class DataChangeInterceptor[DT: ClassTag](var cl: (DT => Any)) extends BaseBufferTrait {

  def getTag(implicit tag: ClassTag[DT]) = tag

  def intercept(du: DataUnit)(implicit tag: ClassTag[DT]) = {
    //println("Intercepted data")

    du.value match {
      case null =>
      case v if(v.trim().length==0) => 
      case _ =>
        tag match {
          case tag if (tag.runtimeClass == classOf[java.lang.Boolean]) =>
            cl(du.value.toBoolean.asInstanceOf[DT])
          case tag if (tag.runtimeClass == classOf[scala.Boolean]) =>
            cl(du.value.toBoolean.asInstanceOf[DT])
          case tag if (tag.runtimeClass == classOf[String]) =>
            cl(du.value.asInstanceOf[DT])
          case tag if (tag.runtimeClass == classOf[java.lang.String]) =>
            cl(du.value.asInstanceOf[DT])
          case _ =>
            throw new RuntimeException("Data Interceptor for: " + tag.runtimeClass + " is not supported")

        }

    }

  }

  override def pushLeft(du: DataUnit) = {

    super.pushLeft(du)
    intercept(du)
  }

  override def pushRight(du: DataUnit) = {

    super.pushRight(du)
    intercept(du)

  }

 /* override def streamIn(du: DataUnit) = {

    super.pushRight(du)
    intercept(du)

  }*/

  override def pull(du: DataUnit) = {

    if (getTag.runtimeClass == classOf[java.lang.Boolean]) {
      cl(du.value.toBoolean.asInstanceOf[DT])
    }

    super.pull(du)

  }

}

class StringDataChangeInterceptor(cl: (String => Any)) extends DataChangeInterceptor[String](cl)
object StringDataChangeInterceptor {
  def apply(cl: String => Any) = {
    new DataChangeInterceptor[String](cl)
  }
}
trait DataChangeInterceptorTrait {

  def interceptData[T](f: T => Any)(implicit tag: ClassTag[T]): Buffer = {

    //,implicit tag: ClassTag[T]

    println(s"Create data interceptor with ${tag.runtimeClass}")
    var changeBuffer = new DataChangeInterceptor[T](f)

    changeBuffer
  }

}


