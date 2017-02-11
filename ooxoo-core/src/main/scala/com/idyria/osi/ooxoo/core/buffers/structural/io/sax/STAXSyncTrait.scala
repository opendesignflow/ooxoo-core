package com.idyria.osi.ooxoo.core.buffers.structural.io.sax

import java.io.File
import java.io.FileOutputStream
import com.idyria.osi.ooxoo.core.buffers.structural.Buffer
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import java.io.PrintStream
import java.net.URL
import java.io.ByteArrayOutputStream
import com.idyria.osi.tea.files.FileWatcherAdvanced

/**
 * @author zm4632
 */
trait STAXSyncTrait extends ElementBuffer {
  
  var staxPreviousFile : Option[File] = None
  var staxFileWatcher : Option[FileWatcherAdvanced] = None
  
  
  def onFileReload(listener:Any)(cl: File => Any) = (staxFileWatcher,staxPreviousFile) match {
    case (None,_) => throw new IllegalArgumentException("Cannot watch file reload without a defined file watcher")
    case (_,None) => throw new IllegalArgumentException("Cannot watch file reload without a defined file")
    case (watcher,file) => 
      
      watcher.get.onFileChange(listener, file.get) {
        cl(_)
      }
      
      
  }
  
  def toFile(f: File) = {

    // Create 
    f.getAbsoluteFile.getParentFile.mkdirs()
    
    var res = StAXIOBuffer(this, true)

    var out = new PrintStream(new FileOutputStream(f))
    out.append(res)
    out.close()
    
    this
  }

  def fromURL(url: URL) = {
    
    // Set Stax Parser and streamIn
    var io = com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer(url)
    this.appendBuffer(io)
    io.streamIn
    
    this
  }
  
  def fromFile(url: File) = {
    
    this.fromURL(url.toURI().toURL)
    
    this.staxPreviousFile = Some(url)
  }
  
  def resyncToFile = staxPreviousFile match {
    case Some(file) => this.toFile(file)
    case None => throw new IllegalAccessException(s"Cannot Resync Class ${getClass.getCanonicalName} to file because none has been set. Use the fromFile method first to set the source file")
  }
  
  def toXMLString : String = {
    var res = StAXIOBuffer(this, true)

    var bout = new ByteArrayOutputStream()
    var out = new PrintStream(bout)
    out.append(res)
    out.close()
   
    new String(bout.toByteArray())
    
  }

}