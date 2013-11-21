package com.idyria.osi.ooxoo.lib.json

import com.idyria.osi.ooxoo.core.buffers.structural.io.BaseIOBuffer
import com.idyria.osi.tea.logging.TLogSource
import java.io.Reader
import scala.util.parsing.combinator.RegexParsers
import scala.io.Source
import scala.util.parsing.input.StreamReader
import com.idyria.osi.ooxoo.core.buffers.structural.DataUnit
import com.idyria.osi.ooxoo.core.buffers.structural.xelement_base

class JsonIO(var stringInput: Reader = null) extends BaseIOBuffer with TLogSource with RegexParsers {

  def top = "{" ~> jsonHierarchy <~ "}" ^^ {
    
    r => 
      println(s"In Top Matcher: ")
      
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

  def jsonHierarchy: Parser[List[DataUnit]] = ("\"" ~> ("""[\w ]+""".r) ^^ { r => println(s"Start: $r");r}) ~ ("\"" ~> ":" ~> "{" ~> repsep( jsonHierarchy | multipleValues  | simpleValue,",").? <~ ("}" ^^ {v => println(s"--Close");v})) ^^ {
    r =>

   //   println("Matched JSON Hierarchy: " + r)
      
      // !! Always send an element opening hierarchy, and a close just after
      var du = new DataUnit
      du.element = new xelement_base
      du.element.name = r._1
      du.setHierarchical(true)

      var fl = r._2 match {
        case Some(l) => l.flatten 
        case None => List()
      }
      
       //du :: ( r.2.flatten.flatten )
     // du :: r._2.
       List()
      
      du :: (fl :+ DataUnit.closeHierarchy)
  }

  def simpleValue: Parser[List[DataUnit]] = "\"" ~> ("""[\w ]+""".r) ~ ("\"" ~ ":" ~> ("\"" ~> ("""[\w ]+""".r) <~ "\"")) ^^ {
    r =>

      //println(s"Matched Simple Value: " + r)
      
      // !! Always send an element opening hierarchy, and a close just after
      var du = new DataUnit
      du.hierarchical = true
      du.element = new xelement_base
      du.element.name = r._1
      du.value = r._2
      List(du,DataUnit.closeHierarchy)
  }
  
  def multipleValues: Parser[List[DataUnit]] = ("\"" ~> ("""[\w ]+""".r)) ~ ("\"" ~> ":"  ~> "[" ~> repsep(("\"" ~> ("""[\w ]+""".r) <~ "\""),",") <~ "]")  ^^ {
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
	      List(du,DataUnit.closeHierarchy)
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
      case Success(result, _) => println(s"Done Parsing JSon")
      case failure: NoSuccess =>

        println(s"Error: " + failure)
      //scala.sys.error(failure.msg)
    }

  }

  def cloneIO: com.idyria.osi.ooxoo.core.buffers.structural.io.IOBuffer = {
    this
  }

}