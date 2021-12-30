/*-
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
package org.odfi.ooxoo.lib.json

import org.odfi.tea.tree.TTreeBuilder
import org.odfi.tea.tree.TTreeNode
import org.odfi.ooxoo.core.buffers.structural.{AnyBufferTrait, DataUnit, ElementBuffer, xelement_base}

import scala.language.implicitConversions

class JSONObject extends TTreeBuilder[JSONNode] {

  trait BuilderJSONNode extends JSONNode {

    override def ::(name: String) = {
      this.name = name
     // println("Set name: " + name)
     // println("Current top: " + tNodeStack.head + " <->" + this)
      // popNode
      popNode(this)
    }
    
    def *::(name: String) = {
      this.name = name
      //println("Set name: " + name)
      //println("Current top: " + tNodeStack.head + " <->" + this)
      // popNode
      popNode(this)
    }
  }

  class BuilderObjectNode extends JSONObjectNode with BuilderJSONNode

  class StringValueNode(var value: String) extends BuilderJSONNode {

    override def streamOut(du: DataUnit) = {

      var du = new DataUnit
      du.element = new xelement_base
      du.element.name = name
      du.value = value
      super.streamOut(du)
    }
  }
  
  class ListValueNode(var vals: List[Any]) extends BuilderJSONNode {

    override def streamOut(du: DataUnit) = {

      // Open
      var du = new DataUnit
      du.element = new xelement_base
      du.element.name = name
      du.hierarchical = true
      super.streamOut(du)
    }
  }

  // Output
  //-----------
  def toJSON = {
    println("toJ Current top: " + tNodeStack.head)
    println("head of stack: " + this.tNodeStack.head.name)
    this.tNodeStack.head.toJSONString
  }

  // Language
  //------------
  implicit def listToNode(lst: List[Any]) : BuilderObjectNode = {
    
    // Create Node
    val objNode = new BuilderObjectNode
    pushNode(objNode)
    
    
    
    objNode
    
    

  }

  implicit def stringToNode(str: String) : StringValueNode = {
    val n = new StringValueNode(str)
    pushNode(n)
    n

  }

  implicit def clToNode(cl: => Any) : BuilderObjectNode = {
    println("Create node")
    new BuilderObjectNode {
      pushNode(this)
      cl
    }
  }

}
