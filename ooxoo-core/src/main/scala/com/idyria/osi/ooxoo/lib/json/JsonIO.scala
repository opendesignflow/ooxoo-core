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
import com.idyria.osi.ooxoo.core.buffers.structural.xattribute_base
import com.idyria.osi.ooxoo.core.buffers.structural.XList
import java.io.ByteArrayOutputStream
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer

class JsonIO(var stringInput: Reader = null, var outputArray: CharArrayWriter = null) extends BaseIOBuffer with TLogSource with RegexParsers {

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

  def jsonHierarchy: Parser[List[DataUnit]] = ("\"" ~> ("""[\w ]+""".r) ^^ { r => logFine(s"Start: $r"); r }) ~ ("\"" ~> ":" ~> "{" ~> repsep(jsonHierarchy | multipleValues | simpleValue, ",").? <~ ("}" ^^ { v => logFine(s"--Close"); v })) ^^ {
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

  def booleanValue: Parser[List[DataUnit]] = "\"" ~> ("""[\w @]+""".r) ~ ("\"" ~ ":" ~> ("true" | "false")) ^^ {
    r =>
      logFine(s"Matched Boolean Value: " + r)
      var du = new DataUnit
      r._1 match {
        // Attribute
        case name if (name.startsWith("_@")) =>
          du.attribute = new xattribute_base
          du.attribute.name = name.drop(2)
          du.value = r._2

          List(du)
        case name =>

          du.element = new xelement_base
          du.element.name = r._1
          du.hierarchical = true
          du.value = r._2

          List(du, DataUnit.closeHierarchy)

      }
  }

  def simpleValue: Parser[List[DataUnit]] = "\"" ~> ("""[\w @]+""".r) ~ ("\"" ~ ":" ~> ("\"" ~> ("""[^" ]+""".r) <~ "\"")) ^^ {
    r =>

      logFine(s"Matched Simple Value: " + r)

      var du = new DataUnit
      r._1 match {
        // Attribute
        case name if (name.startsWith("_@")) =>

          du.attribute = new xattribute_base
          du.attribute.name = name.drop(2)
          du.value = r._2

          List(du)

        // Element
        // !! Always send an element opening hierarchy, and a close just after
        case name =>

          du.element = new xelement_base
          du.element.name = r._1
          du.hierarchical = true
          du.value = r._2

          List(du, DataUnit.closeHierarchy)
      }

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
  var output: PrintWriter = null

  override def streamOut(du: DataUnit): Unit = {

    require(this.outputArray != null)
    if (output == null) {

      output = new PrintWriter(this.outputArray)
    }

    // println(s"Got streamout")

    (du.isHierarchyClose, du.hierarchical, du.element, du.value) match {

      // Open
      //---------
      case (close, true, element, value) if (element != null) =>

        //println(s"Open")

        // Detect multiple elements presence using IO chain stack
        //-------------------
        var (isMultiple, isFirst) = this.previousStack.headOption match {
          case Some(p) if (p.isInstanceOf[XList[_]]) => (true, p.asInstanceOf[XList[_]].head == this.firstBuffer)
          case _ => (false, false)
        }

        //-- Name output, don't output if multiple and not first
        (isMultiple, isFirst) match {
          case (true, false) =>
          case _ => output.print(s""""${element.name}":""")
        }

        value match {

          // Not value and multiple, open Multiple Hierarchy
          case null if (isMultiple && isFirst) => output.print(s"[{")

          // Not value: Open hierarchy
          case null =>
            output.print(s"{")

          // Value: Set value to element
          case v =>
            output.print(s"""\"${value}\",""")

            // Ignore next close, because this output does not need a normal close
            ignoreClose = true
        }

      // Close
      //-------------
      case (true, _, _, _) =>

        //   println(s"Close")
        ignoreClose match {
          case true => ignoreClose = false
          case false =>

            // Detect multiple elements presence using IO chain stack
            //-------------------
            var (isMultiple, isLast) = this.previousStack.headOption match {
              case Some(p) if (p.isInstanceOf[XList[_]]) => (true, p.asInstanceOf[XList[_]].last == this.firstBuffer)
              case _ => (false, false)
            }

            //-- Close: Add multiple close if multiple and last
            (isMultiple, isLast) match {
              case (true, true) => output.print(s"""}],""")
              case _ => output.print(s"""},""")
            }

        }

      // Single element with value
      //--------------------------------
      case (false, false, element, value) if (element != null) =>

        // Detect multiple elements presence using IO chain stack
        //-------------------
        var (isMultiple, isFirst, isLast) = this.previousStack.headOption match {
          case Some(p) if (p.isInstanceOf[XList[_]]) => (true, p.asInstanceOf[XList[_]].head == this.firstBuffer, p.asInstanceOf[XList[_]].last == this.firstBuffer)
          case _ => (false, false, false)
        }

        //-- Name output: Normal, multiple first, or multiple last
        //-----------
        (isMultiple, isFirst) match {

          // Multiple first
          case (true, true) => output.print(s""""${element.name}":[""")

          // Multiple not first: none
          case (true, false) =>
          // Normal
          case _ => output.print(s""""${element.name}":""")
        }

        // Value
        //---------------
        value match {
          case null => output.print(s"""""""")
          case v => output.print(s""""${value}"""")
        }
        //output.println(s""""${element.name}":"${URLEncoder.encode(value, "UTF8")}",""")
        /* try {
          output.print(s""""${URLEncoder.encode(value, "UTF8")}"""")
        } catch {
          case e : Throwable => 
            println(s"JSION Encode fail: "+value)
        }*/

        // Close : Close last multiple or just a ,
        //----------------
        (isMultiple, isLast) match {

          // Last
          case (true, true) => output.print(s"""],""")

          // Otherwise normal ,
          case _ => output.print(s""",""")
        }
      //ignoreClose = true

      // Attribute
      //---------------
      case (false, false, null, value) if (du.attribute != null) =>

        output.print(s""""_a_${du.attribute.name}": \"${value}\",""")

      // Value only
      //-------------------
      case (false, false, null, value) =>

        output.print(s"""\"${value}\",""")
        ignoreClose = true

      case (close, hier, element, value) =>

        logFine(s"Not Supported construct: " + element)
    }

    // Pass it on
    //----------------
    super.streamOut(du)

  }

  def finish: String = {

    // Resolve the wrongly defined ,} sequences, and remove the last ,
    outputArray.toString().replace(",}", "}").trim.dropRight(1)
    //outputArray.toString().replace(",\n}", "\n}").dropRight(2)
  }

  def cloneIO: com.idyria.osi.ooxoo.core.buffers.structural.io.IOBuffer = {
    this
  }

}

object JsonIO {

  /**
   * Streams out an ElementBuffer to a string
   */
  def apply(in: ElementBuffer, indenting: Boolean = false): String = {

    var io = new JsonIO(outputArray = new CharArrayWriter)
    //io.indenting = indenting
    in.appendBuffer(io)
    in.streamOut()
    in.cleanIOChain

    return io.finish

  }

}
