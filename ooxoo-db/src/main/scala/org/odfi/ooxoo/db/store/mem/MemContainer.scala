/*-
 * #%L
 * OOXOO Db Project
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
package org.odfi.ooxoo.db.store.mem

import org.odfi.ooxoo.core.buffers.structural.ElementBuffer
import org.odfi.ooxoo.db.Document
import org.odfi.ooxoo.db.store.DocumentContainer

import scala.reflect.ClassTag

class MemContainer(var id : String)  extends DocumentContainer {
  
  var documentsMap = Map[String,MemDocument]()
  
 /** As seen from class MemContainer, the missing signatures are as follows.
 *  For convenience, these are usable as stub implementations.
 */
  def documents: Iterable[Document] = {
    documentsMap.values
  }
  
  def getDocument(path: String): Option[Document] = {
    documentsMap.get(path)
  }
  def writeDocument[T <: ElementBuffer : ClassTag](path: String,topElement: T): Document  = {
    
    documentsMap.get(path) match {
      case Some(memdocument) => 
        memdocument.elt = topElement
      memdocument
      case None =>  
        var doc =  new MemDocument(path,topElement)
        documentsMap = documentsMap.updated(path,doc)
        doc
    }
   
  }
}

class MemDocument(var id : String ,var elt:ElementBuffer) extends Document {
  
  def exists: Boolean = {
    true
  }
//  def id_=(x$1: String): Unit = ???
  def toInputStream: java.io.InputStream = {
    null
  }
  def toOutputStream: java.io.OutputStream = {
    null
  }
  
  
}
