

import org.odfi.ooxoo.model.out.scala.ScalaProducer
import org.odfi.ooxoo.model.{ModelBuilder, producer, producers}
@producers(Array(
    new producer(value=classOf[ScalaProducer])
))
class SOAP extends ModelBuilder {

    //name="SOAPModel"
    
    //namespace("env" -> "superNamespace")

    "env:Envelope" is {



        "env:Header" is {

           

        }

        "env:Body" is {

         
        }

    }

}
