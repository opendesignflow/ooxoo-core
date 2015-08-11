package com.idyria.osi.ooxoo.core.buffers.extras.listeners

import com.idyria.osi.ooxoo.core.buffers.structural.Buffer
import com.idyria.osi.ooxoo.core.buffers.structural.DataUnit
import scala.reflect.ClassTag
import com.idyria.osi.ooxoo.core.buffers.structural.BaseBufferTrait

class DataChangeInterceptor[DT](var cl: (DT => Any), val tag: ClassTag[DT]) extends BaseBufferTrait {

    def intercept(du:DataUnit) = {
        println("Intercepted data")
        if(tag.runtimeClass==classOf[java.lang.Boolean]) {
           cl(du.value.toBoolean.asInstanceOf[DT]) 
        }
        
        if(tag.runtimeClass==classOf[scala.Boolean]) {
           cl(du.value.toBoolean.asInstanceOf[DT]) 
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
    
    override def pull(du: DataUnit) = {
        
        if(tag.runtimeClass==classOf[java.lang.Boolean]) {
           cl(du.value.toBoolean.asInstanceOf[DT]) 
        }
        
       super.pull( du)
       

    }

}

trait DataChangeInterceptorTrait {

    def interceptData[T](f: T => Any)(implicit tag: ClassTag[T]): Buffer = {

        //,implicit tag: ClassTag[T]
        
        println(s"Create data interceptor with ${tag.runtimeClass}")
        var changeBuffer = new DataChangeInterceptor[T](f, tag)

        changeBuffer
    }

}


