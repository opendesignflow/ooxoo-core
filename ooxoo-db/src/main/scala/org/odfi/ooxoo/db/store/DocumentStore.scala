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
package org.odfi.ooxoo.db.store

import org.odfi.ooxoo.core.buffers.structural.ElementBuffer
import org.odfi.ooxoo.db.Document

import scala.reflect.ClassTag

/**
 * Base Trait for Document Store
 *
 * A Document Store is a main interface to fetch documents
 */
trait DocumentStore {

  // Container Interface
  //--------------------------
  
  /**
   * Returns a container for the provided id
   */
  def container(id: String): DocumentContainer

  /**
   * Returns a list of all known containers
   */
  def containers() : Iterable[DocumentContainer]
  
  // Document Interface
  //-----------------------

  /**
   * path format:  "containerid"/"documentid"
   */
  def document(path: String): Option[Document] = {

    path.split("/") match {

      case splitted if (splitted.length != 2) => throw new RuntimeException(s"""DocumentStore document path $path not conform to containerid/documentid format """)

      case splitted                           => this.container(splitted(0)).getDocument(splitted(1))

    }

  }

  def document[T <: ElementBuffer : ClassTag](path: String, topElement: T): Option[T] = {

    path.split("/") match {

      case splitted if (splitted.length != 2) => throw new RuntimeException(s"""DocumentStore document path $path not conform to containerid/documentid format """)

      case splitted                           => this.container(splitted(0)).document[T](splitted(1), topElement)

    }

  }

  // XPath Interface
  //---------------------

}

