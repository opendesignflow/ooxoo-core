package com.idyria.osi.ooxoo.db.tests

import com.idyria.osi.ooxoo.model.ModelBuilder
import com.idyria.osi.ooxoo.model.producers
import com.idyria.osi.ooxoo.model.producer
import com.idyria.osi.ooxoo.model.out.markdown.MDProducer
import com.idyria.osi.ooxoo.model.out.scala.ScalaProducer
import com.idyria.osi.ooxoo.core.buffers.id.IdAndRefIdModelBuilder
import com.idyria.osi.ooxoo.model.out.scala.JPAProducer
   
@producers(Array(
  new producer(value = classOf[JPAProducer]),
  new producer(value = classOf[MDProducer])))
object TryJPAModel extends ModelBuilder with IdAndRefIdModelBuilder {

  "TryJPAConfig" is {

    attribute("id") isGeneratedIDKey
 
    "Value" ofType("double")
    
    "User" multiple {
      
      attribute("id") isGeneratedIDKey
      
      "Name" ofType("string")
      
    }
    
  }

}