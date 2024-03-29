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
package org.odfi.ooxoo.core.buffers.structural.io.sax

import java.io.File
import java.io.FileOutputStream
import org.odfi.ooxoo.core.buffers.structural.{Buffer, ElementBuffer}
import org.odfi.ooxoo.core.buffers.structural.io.sax

import java.io.PrintStream
import java.net.URL
import java.io.ByteArrayOutputStream
import org.odfi.tea.files.FileWatcherAdvanced

import java.io.OutputStream
import java.io.InputStream
import java.io.FileInputStream
import java.lang.ref.WeakReference
import java.io.ByteArrayInputStream
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import org.w3c.dom.Node

/**
 * @author zm4632
 */
trait STAXSyncTrait extends ElementBuffer {

    var staxPreviousFile: Option[File] = None
    var __staxFileWatcher: Option[FileWatcherAdvanced] = None
    var staxWatchListeners = Map[WeakReference[Any], (File => Any)]()

    def staxFileWatcher_=(w: FileWatcherAdvanced) = {
        this.__staxFileWatcher = Some(w)

        staxTryWatchStart
    }
    def staxFileWatcher = __staxFileWatcher

    def staxTryWatchStart = (__staxFileWatcher, staxPreviousFile) match {
        case (None, _) =>
        case (_, None) =>
        case (Some(watcher), Some(file)) if (watcher.isMonitoredBy(this, file)) =>
        case (Some(watcher), Some(file)) =>

            // Monitor
            watcher.onFileChange(this, file) {
                f =>
                    staxIgnoreNextReload match {
                        case false =>

                            // Call all listeners, and clean weak ones
                            staxWatchListeners.foreach {
                                case (ref, cl) if (ref.get() == null) =>
                                    staxWatchListeners = staxWatchListeners - ref
                                case (ref, cl) =>
                                    cl(f)
                            }
                        case true =>
                            staxIgnoreNextReload = false
                            null
                    }
            }

    }

    var staxIgnoreNextReload = false

    /**
     * Used to know which objets are listening for reload on this file, to ignore their run in case of local write out
     */
    //var staxLocalListeners = List[WeakReference[Any]]()

    def onFileReload(listener: Any)(cl: File => Any) = (__staxFileWatcher, staxPreviousFile) match {
        case (None, _) => throw new IllegalArgumentException("Cannot watch file reload without a defined file watcher")
        case (_, None) => throw new IllegalArgumentException("Cannot watch file reload without a defined file")
        case (watcher, file) =>

            //staxLocalListeners = staxLocalListeners :+ new WeakReference(listener)
            watcher.get.onFileChange(listener, file.get) {
                staxIgnoreNextReload match {
                    case false => cl(_)
                    case true  => null
                }
            }

    }

    def toOutputStream(os: OutputStream, prefixes: Map[String, String] = Map[String, String]()) = {

        var bytesWritten = StAXIOBuffer.writeToOutputStream(this, os, true, prefixes)

        this
    }

    /**
     * Parent of File is created by default
     * Pleae check for validity before calling this method to ensure no useless folders are created
     */
    def toFile(f: File, prefixes: Map[String, String] = Map[String, String]()) = {

        this.synchronized {

            val sourceFile = f.getCanonicalFile
            sourceFile.getParentFile.mkdirs()

            // Ignore next reload
            this.__staxFileWatcher match {
                case Some(watcher) =>
                    staxIgnoreNextReload = true
                case None =>
            }
            // Write out
            var fos = new FileOutputStream(sourceFile)
            toOutputStream(fos, prefixes)
            fos.close

            staxPreviousFile = Some(sourceFile)
            staxTryWatchStart

            this
        }
    }

    def fromURL(url: URL) = {

        // Set Stax Parser and streamIn
        var io = sax.StAXIOBuffer(url)
        this.appendBuffer(io)
        io.streamIn

        this
    }

    def fromInputStream(is: InputStream) = {

        var io = sax.StAXIOBuffer(is)
        this.appendBuffer(io)
        io.streamIn
        is.close

        this

    }

    def fromString(s: String) = fromInputStream(new ByteArrayInputStream(s.getBytes))

    def fromNode(node:Node) = {
       var io = sax.StAXIOBuffer(node)
        this.appendBuffer(io)
        io.streamIn

        this
    }
    
    /**
     * Parent of File is created by default
     * Pleae check for validity before calling this method to ensure no useless folders are created
     */
    def fromFile(f: File) = {

        val sourceFile = f.getCanonicalFile
        sourceFile.getParentFile.mkdirs()
        try {
            sourceFile.exists() match {
                case true  => this.fromInputStream(new FileInputStream(f))
                case false =>
            }

        } catch {
            case e: Throwable =>
                e.printStackTrace()

        }
        this.staxPreviousFile = Some(sourceFile)

        this
    }
    
    def fromElement(elt:Element) = {
      
    }

    def resyncToFile = staxPreviousFile match {
        case Some(file) => this.toFile(file)
        case None       => throw new IllegalAccessException(s"Cannot Resync Class ${getClass.getCanonicalName} to file because none has been set. Use the fromFile method first to set the source file")
    }

    def toXMLDocument = {

        var resStr = toXMLStringNoIndenting

        //println(s"ToXMLDoc: "+resStr)
        // Parse Back
        //----------------
        var factory =
            DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true)
        var builder = factory.newDocumentBuilder();
        
        builder.parse(new ByteArrayInputStream(resStr.getBytes))
    }

    def toXMLString: String = {
        var res = sax.StAXIOBuffer(this, indenting = true)

        var bout = new ByteArrayOutputStream()
        var out = new PrintStream(bout)
        out.append(res)
        out.close()

        new String(bout.toByteArray())

    }

    def toXMLStringWithNamespaces(ns: Map[String, String], indenting: Boolean = false): String = {

        var res = sax.StAXIOBuffer(this, indenting = indenting, ns)

        var bout = new ByteArrayOutputStream()
        var out = new PrintStream(bout)
        out.append(res)
        out.close()

        new String(bout.toByteArray())
    }

    def toXMLStringNoIndenting = {
        var res = sax.StAXIOBuffer(this, indenting = false)

        var bout = new ByteArrayOutputStream()
        var out = new PrintStream(bout)
        out.append(res)
        out.close()

        new String(bout.toByteArray())
    }

}
