/*
 * #%L
 * Core runtime for OOXOO
 * %%
 * Copyright (C) 2008 - 2014 OSI / Computer Architecture Group @ Uni. Heidelberg
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
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

        // Save as file written
        //----------
        filesWritten =  path :: filesWritten 

        println(s"Opened File to : $file")
    }

    override def finish = {
        if (this.out != null) {
            this.out.close()
        }
    }
    
}