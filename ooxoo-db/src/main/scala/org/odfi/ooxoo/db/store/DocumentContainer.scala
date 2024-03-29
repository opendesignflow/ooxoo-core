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
import org.odfi.ooxoo.core.buffers.structural.io.sax.{STAXSyncTrait, StAXIOBuffer}
import org.odfi.ooxoo.db.traits.DBContainerReference
import org.odfi.ooxoo.db.{Document, DocumentWrapper, FileDocument}
import org.odfi.tea.listeners.ListeningSupport

import java.lang.ref.WeakReference
import scala.reflect.{ClassTag, classTag}

/**
 * A Document Container really contains documents and fetches/stores them
 */
trait DocumentContainer extends ListeningSupport {

    // Infos
    //----------------

    /**
     * The id mandatory for a container
     * Each container must be uniquely identified by an id
     *
     */
    var id: String

    /**
     * Lists all the documents available
     */
    def documents: Iterable[Document]

    /**
     * Returns the list of all Documents that are mappable to the provided parameter type
     */
    def documentsAs[T <: ElementBuffer: ClassTag]: Iterable[T] = {

        documents.map { d => documentFromClass[T](d.id) }.filterNot { _ == None }.map(_.get)

    }

    // Pure Document Interface
    //-------------------------

    /**
     * Caches parsed documents
     * The Reference to parsed data is Weak to allow Garbage collection if needed
     */
    var parsedDocumentCache = Map[String, WeakReference[ElementBuffer]]()

    /**
     * Retrieve a Document interface from document
     */
    def getDocument(path: String): Option[Document]

    def clearCached(id: String) = {
        this.parsedDocumentCache = this.parsedDocumentCache - id
    }

    def getCached[T <: ElementBuffer: ClassTag](id: String): Option[T] = {

        //println(s"**DB** Looking for Cached document: ${this.id}/$id")

        //-- Look into cache
        this.parsedDocumentCache.get(id) match {

            //-- In Map, reference is not weak and types match
            //case Some(reference) if (reference.get() != null && Thread.currentThread.getContextClassLoader().loadClass(s"${classTag[T]}").isAssignableFrom(reference.get().getClass)) =>
            case Some(reference) if (reference.get() != null) =>

                Option(reference.get().asInstanceOf[T])

            //-- In map and reference is weak, return none
            //-- NOthing, return none
            case Some(reference) if (reference.get != null) =>
                // println(s"**DB** Rebuilding ${this.id}/$id because class tag does not match : ${reference.get().getClass.getClassLoader.hashCode()} <-> ${classTag[T]}")
                None
            case _ =>
                //  println(s"**DB** Rebuilding ${this.id}/$id because reference is gone")
                None

        }

    }

    /**
     * Retrieve a document from container and parse it using OOXOO core
     *
     * FIXME: Add some more type testing with cache to avoid two document() calls with different type causing classCastException
     * @return None or and Option containing the provided topElement
     */
    def document[T <: ElementBuffer: ClassTag](path: String, topElement: T, autocreate: Boolean = false): Option[T] = {

        //-- Look into cache
        this.getCached[T](path) match {

            //-- In Map and reference is not weak, return
            case Some(res) => Option(res)

            //-- In map, but reference is weak
            //-- Or None
            //-- Try to parse
            case None =>

                //-- Get Document
                this.getDocument(path) match {

                    // No Document, create if necessary
                    case /*Some(document)*/ noneOrAbsent if ((noneOrAbsent.isEmpty && autocreate) || (noneOrAbsent.isDefined && noneOrAbsent.get.exists == false && autocreate)) =>
                        var doc = this.writeDocument(path, topElement)

                        if (topElement.isInstanceOf[STAXSyncTrait] && doc.isInstanceOf[FileDocument]) {
                            topElement.asInstanceOf[STAXSyncTrait].fromFile(doc.asInstanceOf[FileDocument].file)
                        }

                        topElement match {
                            case e: DBContainerReference =>
                                e.parentContainer = Some(this)
                            case _ =>
                        }

                        Some(topElement)

                    case Some(document) if (document.exists) =>

                        //-- Parse
                        //-- Connect STAX Sync

                        if (topElement.isInstanceOf[STAXSyncTrait] && document.isInstanceOf[FileDocument]) {
                            topElement.asInstanceOf[STAXSyncTrait].fromFile(document.asInstanceOf[FileDocument].file)
                        } else {

                            var io = new StAXIOBuffer(document.toInputStream)
                            topElement.appendBuffer(io)
                            io.streamIn
                        }

                        topElement match {
                            case e: DBContainerReference =>
                                e.parentContainer = Some(this)
                            case _ =>
                        }

                        //-- Cache
                        this.parsedDocumentCache = this.parsedDocumentCache + (document.id -> new WeakReference(topElement))

                        //-- Return
                        Some(topElement)

                    case None => None

                    case _    => None
                }

        }

    }

    /**
     * Retrieve a document from container and parse it using OOXOO core
     *
     * FIXME: Add some more type testing with cache to avoid two document() calls with different type causing classCastException
     * @return None or and Option containing the provided topElement
     */
    def documentFromClass[T <: ElementBuffer: ClassTag](path: String, autocreate: Boolean = false): Option[T] = {

        //-- Look into cache
        this.getCached[T](path) match {

            //-- In Map and reference is not weak, return
            case Some(res) => Option(res)

            //-- In map, but reference is weak
            //-- Or None
            //-- Try to parse
            case None =>

                //-- Get Document
                this.getDocument(path) match {
                    case None if (autocreate)                 => document(path, classTag[T].runtimeClass.getDeclaredConstructor().newInstance().asInstanceOf[T], true)
                    case None                                 => None

                    case Some(document) if !(document.exists) => None
                    case Some(document) =>

                        // Create top Document
                        var top = Thread.currentThread.getContextClassLoader().loadClass(s"${classTag[T]}").getDeclaredConstructor().newInstance().asInstanceOf[T]
                        //println("Reflection instancitation of "+classTag[T])
                        //classTag[T].

                        //-- Parse
                        var io = new StAXIOBuffer(document.toInputStream)
                        top.appendBuffer(io)
                        io.streamIn

                        //-- Cache
                        this.parsedDocumentCache = this.parsedDocumentCache + (document.id -> new WeakReference(top))

                        //-- Return
                        Option(top)

                }

        }

    }

    def documentWithNew[T <: ElementBuffer: ClassTag](path: String, topElement: T)(cl: T => Unit): T = {

        //-- Look into cache
        this.getCached[T](path) match {

            //-- In Map and reference is not weak, return
            case Some(res) => res

            //-- In map, but reference is weak
            //-- Or None
            //-- Try to parse
            case None =>

                //-- Get Document
                this.getDocument(path) match {

                    //-- Document exists, otherwise create
                    case Some(document) if (document.exists) =>

                        //-- Parse
                        //-- Connect STAX Sync

                        if (topElement.isInstanceOf[STAXSyncTrait] && document.isInstanceOf[FileDocument]) {
                            topElement.asInstanceOf[STAXSyncTrait].fromFile(document.asInstanceOf[FileDocument].file)
                        } else {

                            var io = new StAXIOBuffer(document.toInputStream)
                            topElement.appendBuffer(io)
                            io.streamIn
                        }

                        topElement match {
                            case e: DBContainerReference =>
                                e.parentContainer = Some(this)
                            case _ =>
                        }

                        //-- Cache
                        this.parsedDocumentCache = this.parsedDocumentCache + (document.id -> new WeakReference(topElement))

                        //-- Return
                        topElement

                    case _ =>

                        //println(s"****** Create NEW ODCUMENT $path ******")
                        //-- Call new closure, then save
                        cl(topElement)
                        var doc = this.writeDocument(path, topElement)

                        if (topElement.isInstanceOf[STAXSyncTrait] && doc.isInstanceOf[FileDocument]) {
                            topElement.asInstanceOf[STAXSyncTrait].fromFile(doc.asInstanceOf[FileDocument].file)
                        }

                        topElement match {
                            case e: DBContainerReference =>
                                e.parentContainer = Some(this)
                            case _ =>
                        }

                        topElement
                }

        }

    }

    /**
     * Get an Interface To Document information with parsed node
     */
    def documentWrapper[T <: ElementBuffer: ClassTag](path: String, topElement: T): Option[DocumentWrapper[T]] = {

        // Get/Create Document
        var doc = this.getDocument(path).get

        // Get//Create element
        this.document(path, topElement)

        return Some(new DocumentWrapper(topElement, doc))

    }

    /**
     * Gets all the Documents from current container, that match the given type
     */
    def getAllDocuments[T <: ElementBuffer: ClassTag]: List[T] = {

        // Prepare results
        //---------------
        var res = List[T]()

        // Go through all documents an try to parse
        this.documents.foreach {
            doc =>

                //-- Try to get cached
                this.getCached[T](doc.id) match {
                    case Some(parsed) => res = res :+ parsed

                    //-- Nothing cached, try to parse
                    case None =>

                        try {

                            println(s"Getting document ${doc.id}")
                            //-- Prepare a top element
                            var top = Thread.currentThread.getContextClassLoader().loadClass(s"${classTag[T]}").getDeclaredConstructor().newInstance().asInstanceOf[T]
                            this.document[T](doc.id, top)

                            //-- OK
                            res = res :+ top
                            //top = Thread.currentThread.getContextClassLoader().loadClass(s"${classTag[T]}").newInstance.asInstanceOf[T]

                        } catch {
                            // Error , don't retain this document
                            case e: Throwable =>
                        }

                }

        }
        res
    }

    /**
     * Write a Buffer type to a document
     *
     * Listening point:
     *
     * document.writen((path,topElement))
     */
    def writeDocument[T <: ElementBuffer: ClassTag](path: String, topElement: T): Document

}
