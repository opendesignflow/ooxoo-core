package org.odfi.ooxoo

import org.odfi.ooxoo.core.utils.ScalaReflectUtils

trait Named {

  var name: String = null

}


class Test extends Named {

  var lfield: String = null

}

object ReflectTry extends App {


  // Instanciate
  //------------------
  var inst = new Test

  inst.name = "Test"


  // List again
  //------------------
  //println("Hi!")
  //println("Class: "+inst.getClass)
  inst.getClass.getDeclaredFields.foreach {
    f =>
      println("Field: " + f.getName)
      f.setAccessible(true)
      println("  -> : " + f.get(inst))
    //f.setAccessible.
  }

  println("Hi2!")
  ScalaReflectUtils.getFields(inst).foreach {
    f =>
      println("Field2: " + f.getName)
  }
  /*

  // List Fields
  //------------------
  ScalaReflectUtils.getFields(inst) foreach {
      f =>
       println("Field: "+f.name)

       // Get
       var m = inst.getClass().getDeclaredMethod(f.name+"")


       println("Res: "+m.invoke(inst,Array[Object]()))

       // Get Instance mirror of source
       var mirror = scala.reflect.runtime.universe.runtimeMirror(Thread.currentThread().getContextClassLoader());

/*
* #%L
* Core runtime for OOXOO
* %%
* Copyright (C) 2006 - 2017 Open Design Flow
* %%
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
* #L%
*/
       var instanceMirror = mirror.reflect(inst)

       println("Getter: "+f.asTerm.getter+" , is it one: "+f.asTerm.isLocal)
       println("----> Alt: "+f.asTerm.allOverriddenSymbols)
       println("----> Alt: "+f.asTerm.alternatives.head.asTerm)

       //var getterMirror = instanceMirror.reflectMethod(f.asTerm)

      // println("Result: "+getterMirror())

       //var fieldMirror = instanceMirror.reflectField(f.asTerm)
        //var fieldMirror = instanceMirror.reflectField(f.asTerm.privateWithin.asTerm)


       //fieldMirror.get.asInstanceOf[T]

  }

*/


}
