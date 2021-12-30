package org.odfi.ooxoo.db.tests

import org.odfi.ooxoo.model.ModelBuilder
import org.odfi.ooxoo.model.producers
import org.odfi.ooxoo.model.producer
import org.odfi.ooxoo.model.out.markdown.MDProducer
import org.odfi.ooxoo.model.out.scala.ScalaProducer
import org.odfi.ooxoo.core.buffers.id.IdAndRefIdModelBuilder
import org.odfi.ooxoo.model.out.scala.JPAProducer
   
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