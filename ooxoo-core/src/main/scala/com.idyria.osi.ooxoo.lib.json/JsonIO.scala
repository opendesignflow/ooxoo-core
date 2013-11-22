package com.idyria.osi.ooxoo.lib.json

import com.idyria.osi.ooxoo.core.buffers.structural.io.BaseIOBuffer
import com.idyria.osi.tea.logging.TLogSource
import java.io.Reader
import scala.util.parsing.combinator.RegexParsers
import scala.io.Source
import scala.util.parsing.input.StreamReader
import com.idyria.osi.ooxoo.core.buffers.structural.DataUnit
import com.idyria.osi.ooxoo.core.buffers.structural.xelement_base
import java.io.PrintStream
import java.io.CharArrayWriter
import java.io.PrintWriter
import java.net.URLEncoder

class JsonIO(var stringInput: Reader = null, var outputArray : CharArrayWriter = null) extends BaseIOBuffer with TLogSource with RegexParsers {

  def top = "{" ~> jsonHierarchy <~ "}" ^^ {

    r =>
      //println(s"In Top Matcher: ")

      r.foreach {
        du =>
          //   println(s"Sending DU: hier: ${du.hierarchical}, close: "+du.isHierarchyClose)
          super.streamIn(du)
      }

      /*r.foreach {
        du => 
          println(s"DatUnit: "+du.element.name)
      }*/
      r
  }

  def jsonHierarchy: Parser[List[DataUnit]] = ("\"" ~> ("""[\w ]+""".r) ^^ { r => println(s"Start: $r"); r }) ~ ("\"" ~> ":" ~> "{" ~> repsep(jsonHierarchy | multipleValues | simpleValue, ",").? <~ ("}" ^^ { v => println(s"--Close"); v })) ^^ {
    r =>

      //   println("Matched JSON Hierarchy: " + r)

      // !! Always send an element opening hierarchy, and a close just after
      var du = new DataUnit
      du.element = new xelement_base
      du.element.name = r._1
      du.setHierarchical(true)

      var fl = r._2 match {
        case Some(l) => l.flatten
        case None    => List()
      }

      //du :: ( r.2.flatten.flatten )
      // du :: r._2.
      List()

      du :: (fl :+ DataUnit.closeHierarchy)
  }

  def simpleValue: Parser[List[DataUnit]] = "\"" ~> ("""[\w ]+""".r) ~ ("\"" ~ ":" ~> ("\"" ~> ("""[^" ]+""".r) <~ "\"")) ^^ {
    r =>

      println(s"Matched Simple Value: " + r._2)

      // !! Always send an element opening hierarchy, and a close just after
      var du = new DataUnit
      du.hierarchical = true
      du.element = new xelement_base
      du.element.name = r._1
      du.value = r._2

      List(du, DataUnit.closeHierarchy)
  }

  def multipleValues: Parser[List[DataUnit]] = ("\"" ~> ("""[\w ]+""".r)) ~ ("\"" ~> ":" ~> "[" ~> repsep(("\"" ~> ("""[^" ]+""".r) <~ "\""), ",") <~ "]") ^^ {
    r =>

      //println(s"Matched Multiple Values: " + r)
      r._2.map {
        value =>

          // !! Always send an element opening hierarchy, and a close just after
          var du = new DataUnit
          du.hierarchical = true
          du.element = new xelement_base
          du.element.name = r._1
          du.value = value
          List(du, DataUnit.closeHierarchy)
      }.flatten

  }

  override def streamIn = {

    // XML input must be provided
    require(this.stringInput != null)

    /* parseAll(top, StreamReader(stringInput)) match {
      case Success(result, _) => println(s"Done Parsing JSon")
      case failure: NoSuccess =>

        println(s"Error: " + failure)
      //scala.sys.error(failure.msg)
    }*/

    top(StreamReader(stringInput)) match {
      case Success(result, _) =>
      //println(s"Done Parsing JSon")
      case failure: NoSuccess =>

        //println(s"Error: " + failure)
        scala.sys.error(failure.toString)
    }

  }

  var ignoreClose = false
  var output : PrintWriter = null
  
  override def streamOut(du: DataUnit): Unit = {

    require(this.outputArray!=null)
    if (output==null) {
      
      output = new PrintWriter(this.outputArray)
    }

    
    
   // println(s"Got streamout")

    (du.isHierarchyClose, du.hierarchical, du.element, du.value) match {

     
      
      // Open
      //---------
      case (close, true, element, value) if (element != null) =>

        //println(s"Open")

        output.print(s""""${element.name}":""")

        // Value: Set value to element
        // Not value: Open hierarchy
        value match {
          case null =>
            output.println(s"{")
          
          case v  =>
            output.println(s"""\"${URLEncoder.encode(value,"UTF8")}\",""")
            
            // Ignore next close, because this output does not need a normal close
            ignoreClose = true
        }

        // Close already?
        close match {
          case true =>
           // output.println("}")
          case false =>
        }

      // Close
      //-------------
      case (true, _, _, _) =>

        //   println(s"Close")
      	ignoreClose match {
      	  case true => ignoreClose = false
      	  case false => output.println(s"""},""")
      	}
        

      // Single element with value
      //--------------------------------
      case (false, false, element, value) if(element!=null) =>

        output.println(s""""${element.name}":"${URLEncoder.encode(value,"UTF8")}",""")
        ignoreClose = true
        
      // Value only
      //-------------------
      case (false, false, null, value) =>

        output.println(s"""\"${URLEncoder.encode(value,"UTF8")}\",""")
        ignoreClose = true

      case (close, hier, element, value) =>

        println(s"Not Supported construct: " + element)
    }

    // Pass it on
    //----------------
    super.streamOut(du)

  }

  def finish : String = {
    
    // Resolve the wrongly defined ,} sequences, and remove the last ,
    outputArray.toString().replace(",\n}","\n}").replace("\n","").dropRight(1)
    
  }
  
  def cloneIO: com.idyria.osi.ooxoo.core.buffers.structural.io.IOBuffer = {
    this
  }

}