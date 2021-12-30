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
package org.odfi.ooxoo.db

import org.odfi.ooxoo.db.store.DocumentContainer

import java.io.{InputStream, OutputStream}

/**
 * Common trait to represent a document extracted from Document store
 * 
 * The common DocumentStore uses this interface to get bytes and parse
 */
trait Document {
  
  /**
   * A Unique id for this document inside a container
   */
  var id : String
  
  /**
   * Storing container
   */
  var container : DocumentContainer = null
  
  /**
   * Open A Stream to read from this document
   */
  def toInputStream : InputStream
  
  /**
   * Open a stream to write to this document
   */
  def toOutputStream : OutputStream
  
  /**
   * If the document exists
   */
  def exists : Boolean
  
}
