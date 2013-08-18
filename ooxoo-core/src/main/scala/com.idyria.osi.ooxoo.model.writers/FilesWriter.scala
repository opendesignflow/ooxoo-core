package com.idyria.osi.ooxoo.model.writers


import com.idyria.osi.ooxoo.model._
import java.io._

/**

Write outputs to a base folder

*/
class FileWriters(

    var baseFolder : File

    ) extends PrintStreamWriter (null) {


    override def file(path: String) = {

        // Close actual output
        //---------------
        if (this.out != null) {
            this.out.close()
        }

        // To File
        //---------------
        var file = new File(baseFolder,path)

        // Prepare Folder
        //---------------------
        file.getParentFile.mkdirs

        // Set to current output
        //----------------
        this.out = new PrintStream(new FileOutputStream(file))

        println(s"Opened File to : $file")
    }

    override def finish = {
        if (this.out != null) {
            this.out.close()
        }
    }
    
}