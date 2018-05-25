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

package com.idyria.osi.ooxoo.lib.json

import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.AnyBufferTrait
import com.idyria.osi.tea.tree.TTreeBuilder
import com.idyria.osi.tea.tree.TTreeNode
import com.idyria.osi.ooxoo.core.buffers.structural.AnyElementBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.DataUnit
import com.idyria.osi.ooxoo.core.buffers.structural.xelement_base
import com.idyria.osi.ooxoo.core.buffers.structural.Buffer
import com.idyria.osi.ooxoo.core.buffers.structural.BaseBufferTrait

trait JSONNode extends BaseBufferTrait with JSonUtilTrait with TTreeNode[JSONNode] {

  var name = ""

  var children = List[JSONNode]()

  def ::(name: String) = {
    this.name = name

  }

  def addChild(n: JSONNode) = {
    children = children :+ n
  }

 
}

class JSONObjectNode extends JSONNode {
  
   // Stream out
  override def streamOut(du: DataUnit) = {
    println("Streamout: "+name)
    lockIO
    try {
      //-- Send Hierarchy open
      var du = new DataUnit
      du.element = new xelement_base
      du.element.name = name
      du.hierarchical = true
      super.streamOut(du)
      
      //-- Send for children
      this.getIOChain match {
        case Some(ioChain) =>
          children.foreach {
            c =>
              println("Streamout child: "+c.name)
              //println("- Calling streamout on element: " + value.getClass.getSimpleName)

              c.appendBuffer(ioChain)
              c.streamOut()
          }
        case None =>
      }
      
      //-- Close
      println("Close: "+name)
      var cdu = new DataUnit
      cdu.setHierarchyClose
      super.streamOut(cdu)
    } finally {
      unlockIO
      cleanIOChain
    }
    //-- 
  }

  
  
}
