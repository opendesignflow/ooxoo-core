package com.idyria.osi.ooxoo.core.buffers.structural.io.sax

import java.io.File
import java.io.FileOutputStream
import com.idyria.osi.ooxoo.core.buffers.structural.Buffer
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import java.io.PrintStream
import java.net.URL
import java.io.ByteArrayOutputStream
import com.idyria.osi.tea.files.FileWatcherAdvanced
import java.io.OutputStream
import java.io.InputStream
import java.io.FileInputStream
import java.lang.ref.WeakReference

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

  def toOutputStream(os: OutputStream) = {

    var bytesWritten = StAXIOBuffer.writeToOutputStream(this, os, true)

    this
  }

  /**
   * Parent of File is created by default
   * Pleae check for validity before calling this method to ensure no useless folders are created
   */
  def toFile(f: File) = {

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
      toOutputStream(fos)

      staxPreviousFile = Some(sourceFile)
      staxTryWatchStart

      this
    }
  }

  def fromURL(url: URL) = {

    // Set Stax Parser and streamIn
    var io = com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer(url)
    this.appendBuffer(io)
    io.streamIn

    this
  }

  def fromInputStream(is: InputStream) = {

    var io = com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer(is)
    this.appendBuffer(io)
    io.streamIn
    is.close

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

  def resyncToFile = staxPreviousFile match {
    case Some(file) => this.toFile(file)
    case None       => throw new IllegalAccessException(s"Cannot Resync Class ${getClass.getCanonicalName} to file because none has been set. Use the fromFile method first to set the source file")
  }

  def toXMLString: String = {
    var res = StAXIOBuffer(this, true)

    var bout = new ByteArrayOutputStream()
    var out = new PrintStream(bout)
    out.append(res)
    out.close()

    new String(bout.toByteArray())

  }

}