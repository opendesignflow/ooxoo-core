package test

import com.idyria.osi.ooxoo.core.utils._

trait Named  {


    var name : String = null

}


class Test extends Named {

    var lfield : String = null

}

object ReflectTry extends App {



    // Instanciate
    //------------------
    var inst = new Test

    // List Fields
    //------------------
    ScalaReflectUtils.getFields(inst) foreach {
        f => println("Field: "+ f.getName)
    }




}
