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
package org.odfi.ooxoo.db.traits

import org.odfi.ooxoo.core.buffers.structural.ElementBuffer
import org.odfi.ooxoo.core.buffers.structural.io.sax.{STAXSyncTrait, StAXIOBuffer}

import java.io.File
import java.lang.ref.WeakReference
import scala.reflect.ClassTag

trait DBLocalDocumentCache {
  
  var documentsCache = Map[String,WeakReference[ElementBuffer]]()
  
  def getCachedDocument[T <: ElementBuffer](name:String,file:File)(implicit tag : ClassTag[T]) : Option[T] = {
    
    documentsCache.get(name) match {
      case Some(doc) if (doc.get()!=null && tag.runtimeClass.isAssignableFrom(doc.getClass)) => Some(doc.asInstanceOf[T]) 
      case others if(file.exists()==false) => None
      case others => 
        
        //-- Instanciate
        var elt = tag.runtimeClass.getDeclaredConstructor().newInstance().asInstanceOf[ElementBuffer]
        
        //-- Streamin
        elt match {
          case stx : STAXSyncTrait => 
            stx.fromFile(file)
          case other => 
            
            var b =  StAXIOBuffer(file.toURI().toURL())
            elt.appendBuffer(b)
            b.streamIn
            b
            
        }
        
        //-- Save
        this.documentsCache = this.documentsCache + (name -> new WeakReference(elt))
        
        Some(elt.asInstanceOf[T])
        
    }
    
  }
  
}
