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
  
  
  def toOutputStream(os:OutputStream) = {

    var res = StAXIOBuffer(this, true)

    var out = new PrintStream(os)
    out.append(res)
    out.close()
    
    this
  }
  
  def toFile(f: File) = {

    // Create 
    f.getAbsoluteFile.getParentFile.mkdirs()
    
    var fos =  new FileOutputStream(f)
    toOutputStream(fos)
    
    staxPreviousFile = Some(f)
    
    this
  }

  def fromURL(url: URL) = {
    
    // Set Stax Parser and streamIn
    var io = com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer(url)
    this.appendBuffer(io)
    io.streamIn
    
    this
  }
  
  def fromInputStream(is:InputStream) = {
    
    var io = com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer(is)
    this.appendBuffer(io)
    io.streamIn
    is.close
    
    this
    
  }
  
  def fromFile(f: File) = {
    
    this.fromInputStream(new FileInputStream(f))
    
    
    this.staxPreviousFile = Some(f)
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