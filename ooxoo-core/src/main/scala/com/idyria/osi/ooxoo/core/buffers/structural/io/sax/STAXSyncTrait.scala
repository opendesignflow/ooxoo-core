package com.idyria.osi.ooxoo.core.buffers.structural.io.sax

import java.io.File
import java.io.FileOutputStream
import com.idyria.osi.ooxoo.core.buffers.structural.Buffer
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import java.io.PrintStream
import java.net.URL

/**
 * @author zm4632
 */
trait STAXSyncTrait extends ElementBuffer {

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

}