package com.idyria.osi.ooxoo.model


/**
    Produce an output based on:

    - a model
    - an output writer interface


*/
abstract class Producer {


    def produce(mode: Model, output: Writer)

}

trait Writer {

    // Indentation
    //----------------------
    var indentList = List[String]()

    def indent = this.indentList = "    " :: indentList

    def outdent = this.indentList = indentList.tail

    def indentString = indentList.mkString

    // Output
    //------------

    /**
        Opens a file at provided path
    */
    def file(path: String)

    /**
        Writes a line to output
    */
    def <<(str: String)

    /**
        Finish writing
    */
    def finish


}