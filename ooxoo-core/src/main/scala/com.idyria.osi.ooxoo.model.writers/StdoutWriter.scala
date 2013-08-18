package com.idyria.osi.ooxoo.model.writers


import com.idyria.osi.ooxoo.model._

import java.io.PrintStream

class PrintStreamWriter( var out : PrintStream ) extends Writer {

    def file(path: String) = {

        out.println(s"---------- File: $path -----------------")

    }

    def <<(str: String)= {
        out.println(s"${this.indentString}$str")
    }

    def finish = {
        
    }

}

/**
    Simply outputs result to stdout, for example for debugging purpose
*/
class StdoutWriter extends PrintStreamWriter (System.out) {
    
    
}