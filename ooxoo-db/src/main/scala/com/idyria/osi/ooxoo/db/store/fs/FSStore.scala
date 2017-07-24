/*-
 * #%L
 * OOXOO Db Project
 * %%
 * Copyright (C) 2006 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package com.idyria.osi.ooxoo.db.store.fs

import com.idyria.osi.ooxoo.db.store.DocumentStore
import java.io.File
import com.idyria.osi.ooxoo.db.store.DocumentContainer
import com.idyria.osi.ooxoo.db.Document
import java.io.FileInputStream
import com.idyria.osi.ooxoo.core.buffers.structural.Buffer
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer
import java.io.FileOutputStream
import scala.reflect.ClassTag
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo.db.FileDocument
import com.idyria.osi.tea.file.DirectoryUtilities

/**
 * FSSStore is a simple filesystem store implementation
 */
class FSStore(

    /**
     * The Base Folder where containers are stored
     */
    var baseFolder: File) extends DocumentStore {

  baseFolder.mkdirs()
  
  // Utils
  //------------
  def cleanId(id: String): String = id.trim.replace(s"""/""", ".").replace("\\", ".")

  // Containers
  //-----------------

  /**
   * Map holding the created container up to now
   */
  var containerCache = Map[String, FSContainer]()

  /**
   * id format: any string but no "/", they will be converted to .
   */
  def container(id: String): FSContainer = {

    // Clean id
    var cleanedId = cleanId(id)

    // Look in cache, if not create
    containerCache.get(cleanedId) match {
      case Some(container) => container

      // Create and add
      case None =>

        //-- Create Folder and check
        var containerFolder = new File(baseFolder, cleanedId)
        if (containerFolder.exists() && !containerFolder.isDirectory()) {
          throw new RuntimeException(s"Could not create FSContainer $cleanedId because file already exists and is not a folder: ${containerFolder.getAbsolutePath()}")
        }
        containerFolder.mkdirs()

        //-- Create container
        var container = new FSContainer(containerFolder)
        this.containerCache = this.containerCache + (cleanedId -> container)
        container

    }

  }

  /**
   * List all the folders and create a container for erach
   */
  def containers(): Iterable[FSContainer] = {

    this.baseFolder.listFiles() match {
      case null  => Nil
      case files => files.filter(_.isDirectory()).map(f => container(f.getName()))
    }

  }
  
  // Cleaning Interface
  //-----------------------
  
  def wipe = {
    DirectoryUtilities.deleteDirectoryContent(this.baseFolder)
  }

  // XPath interface
  //----------------

}

class FSContainer(

    /**
     * Base folder of container where documents are stored
     */
    var baseFolder: File) extends DocumentContainer {

  // Constructor
  //---------------

  //-- Container Id is the folder name
  var id = baseFolder.getName()

  // Infos
  //-----------------

  /**
   * List folder content, and create a document for all the files
   */
  def documents: Iterable[Document] = {

    this.baseFolder.listFiles() match {
      case null  => Nil
      case files => 
        
        files.filter(_.isFile()).map(f => this.getDocument(f.getName).get)
    }

  }

  // Document
  //---------------------------

  /**
   * A document is an XML File in the base folder
   */
  def getDocument(path: String): Option[Document] = {

    //-- Append xml to path
    var filePath = path match {
      case path if (path.endsWith("xml")) => path
      case _                              => s"$path.xml"
    }

    //-- Create File
    var documentFile = new File(baseFolder, filePath)
    //println(s"Got document: ${documentFile.toURI.toURL()}")
    //-- Exists ?
    var fsdoc = new FSDocument(documentFile)
    fsdoc.container = this
    Some(fsdoc)

  }

  /**
   *
   */
  def writeDocument[T <: ElementBuffer: ClassTag](path: String, topElement: T): Document = {

    //-- Append xml to path
    var filePath = path match {
      case path if (path.endsWith("xml")) => path
      case _                              => s"$path.xml"
    }

    //-- Get Document 
    var document = this.getDocument(path) match {
      case Some(doc) => doc

      //-- Create   
      case None =>

        var doc = new FSDocument(new File(baseFolder, filePath))
        doc
    }

    //-- Add IO Buffer
    var io = new StAXIOBuffer()
    io.output = document.toOutputStream
    topElement.appendBuffer(io)
    topElement.streamOut()

    //-- Listeners call
    this.@->("document.writen", (path, topElement))
    this.@->("document.writen")
    
    document

  }

}

class FSDocument(

    /**
     * The File holding document data
     */
    val file: File) extends FileDocument {

  var id = file.getName
  
  def exists = file.exists()

  def toInputStream = new FileInputStream(file)

  def toOutputStream = new FileOutputStream(file)

}
