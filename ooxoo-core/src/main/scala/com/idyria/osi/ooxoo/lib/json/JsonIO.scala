/*
 * #%L
 * Core runtime for OOXOO
 * %%
 * Copyright (C) 2006 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import scala.collection.mutable.ArrayStack

class JsonIO(var stringInput: Reader = null, var outputArray: CharArrayWriter = null) extends BaseIOBuffer with TLogSource with RegexParsers {

    /**
     * Prefix attribute name with _a_ to avoid conflicts with elements
     * To be enabled case by case
     */
    var safeAttributes = false

    // Utils
    //------------
    def cleanAttributeName(name: String) = safeAttributes match {
        case false => name
        case true  => "_a_" + name
    }

    // Language
    //---------------
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

    def jsonHierarchy: Parser[List[DataUnit]] = ("\"" ~> ("""[\w ]+""".r) ^^ { r => logFine(s"Start: $r"); r }) ~ ("\"" ~> ":" ~> "{" ~> repsep(jsonHierarchy | multipleValues | simpleValue | booleanValue | intValue | doubleValue | nullValue, ",").? <~ ("}" ^^ { v => logFine(s"--Close"); v })) ^^ {
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

    def intValue: Parser[List[DataUnit]] = "\"" ~> ("""[\w @]+""".r) ~ ("\"" ~ ":" ~> ("""[\d.-]+""".r)) ^^ {
        r =>
            logFine(s"Matched Int Value: " + r)
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

    def doubleValue: Parser[List[DataUnit]] = "\"" ~> ("""[\w @]+""".r) ~ ("\"" ~ ":" ~> ("""[\d\.]+""".r)) ^^ {
        r =>
            logFine(s"Matched Double Value "+r._1+": " + r)
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
    
     def nullValue: Parser[List[DataUnit]] = "\"" ~> ("""[\w @]+""".r) ~ ("\"" ~ ":" ~> ("""null""".r)) ^^ {
        r =>
            logFine(s"Matched Null Value "+r._1+": " + r)
            var du = new DataUnit
            r._1 match {
                // Attribute
                case name if (name.startsWith("_@")) =>
                    du.attribute = new xattribute_base
                    du.attribute.name = name.drop(2)
                    du.value = null

                    List(du)
                case name =>

                    du.element = new xelement_base
                    du.element.name = r._1
                    du.hierarchical = true
                    du.value = null

                    List(du, DataUnit.closeHierarchy)

            }
    }

    def simpleValue: Parser[List[DataUnit]] = "\"" ~> ("""[\w @]+""".r) ~ ("\"" ~ ":" ~> ("\"" ~> ("""(?:[^"\\]|\\.)*""".r) <~ "\"")) ^^ {
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

    //var multipleCurrentNameStack = ArrayStack[String]()
    def multipleValues: Parser[List[DataUnit]] = ("\"" ~> ("""[\w ]+""".r) ) ~("\"" ~> ":" ~> "[" ~> (repsep(("\"" ~> ("""[^" ]*""".r) <~ "\"") | multipleValuesHierarchy, ",")).? <~ "]") ^^ {
        r =>

            logFine(s"Matched Multiple Values: " + r._1)
            r._2 match {
                
                case Some(values) =>
                    values.map {
                        case value : List[_] => 
                            
                            // !! Always send an element opening hierarchy, and a close just after
                            var du = new DataUnit
                            du.hierarchical = true
                            du.element = new xelement_base
                            du.element.name = r._1
                            
                            du :: (value.asInstanceOf[List[DataUnit]] :+ DataUnit.closeHierarchy) 
                            
                            
                        case value : String =>

                            // !! Always send an element opening hierarchy, and a close just after
                            var du = new DataUnit
                            du.hierarchical = true
                            du.element = new xelement_base
                            du.element.name = r._1
                            du.value = value
                            List(du, DataUnit.closeHierarchy)
                    }.flatten
                case None => List()
            }

    }

    def multipleValuesHierarchy: Parser[List[DataUnit]] = ("{" ~> repsep(jsonHierarchy | multipleValues | simpleValue | booleanValue | intValue | doubleValue | nullValue, ",").? <~ "}") ^^ {
        r =>

             /*  println("Matched JSON Hierarchy in multiple value: " + multipleCurrentNameStack.head)

            // !! Always send an element opening hierarchy, and a close just after
            // The first DU opens an element for the container, then the next one are normal
            var du = new DataUnit
            du.element = new xelement_base
            du.element.name =multipleCurrentNameStack.head
            du.setHierarchical(true)*/

            var fl = r match {
                case Some(l) => l.flatten
                case None    => List()
            }

            /*//du :: ( r.2.flatten.flatten )
            // du :: r._2.
            List()

            du :: (fl :+ DataUnit.closeHierarchy)*/
            fl
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

    // Stream out
    //--------------------
    var ignoreClose = false
    var output: PrintWriter = null

    override def streamOut(du: DataUnit): Unit = {

        require(this.outputArray != null)
        if (output == null) {

            output = new PrintWriter(this.outputArray)
        }

        // println(s"Got streamout")
        logFine[JsonIO]("Got streamout")

        (du.isHierarchyClose, du.hierarchical, du.element, du.value) match {

            // Open
            //---------
            case (close, true, element, value) if (element != null) =>

                logFine[JsonIO]("Open Element")

                // Detect multiple elements presence using IO chain stack
                //-------------------
                var (isMultiple, isFirst) = this.previousStack.headOption match {
                    case Some(p) if (p.isInstanceOf[XList[_]]) => (true, p.asInstanceOf[XList[_]].head == this.firstBuffer)
                    case _                                     => (false, false)
                }

                //-- Name output, don't output if multiple and not first
                (isMultiple, isFirst) match {
                    case (true, false) =>
                    case _ =>

                        logFine[JsonIO]("Write Name")
                        output.print(s""""${element.name}":""")
                }

                value match {

                    // Not value and multiple, open Multiple Hierarchy
                    case null if (isMultiple && isFirst) =>

                        logFine[JsonIO]("Open Multiple Hierarchy")
                        output.print(s"[{")

                    // Not value: Open hierarchy
                    case null =>

                        logFine[JsonIO]("Open Hierarchy")
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

                logFine[JsonIO]("Close")
                ignoreClose match {
                    case true => ignoreClose = false
                    case false =>

                        logFine[JsonIO]("Close Hierarchy")
                        // Detect multiple elements presence using IO chain stack
                        //-------------------
                        var (isMultiple, isLast) = this.previousStack.headOption match {
                            case Some(p) if (p.isInstanceOf[XList[_]]) => (true, p.asInstanceOf[XList[_]].last == this.firstBuffer)
                            case _                                     => (false, false)
                        }

                        //-- Close: Add multiple close if multiple and last
                        (isMultiple, isLast) match {
                            case (true, true) =>

                                logFine[JsonIO]("Close last multiple ")
                                output.print(s"""}],""")

                            case _ => output.print(s"""},""")
                        }

                }

            // Single element with value
            //--------------------------------
            case (false, false, element, value) if (element != null) =>

                logFine[JsonIO]("Element with value")

                // Detect multiple elements presence using IO chain stack
                //-------------------
                var (isMultiple, isFirst, isLast) = this.previousStack.headOption match {
                    case Some(p) if (p.isInstanceOf[XList[_]]) => (true, p.asInstanceOf[XList[_]].head == this.firstBuffer, p.asInstanceOf[XList[_]].last == this.firstBuffer)
                    case _                                     => (false, false, false)
                }

                //-- Name output: Normal, multiple first, or multiple last
                //-----------
                (isMultiple, isFirst) match {

                    // Multiple first
                    case (true, true) =>

                        logFine[JsonIO]("Multiple Values")
                        output.print(s""""${element.name}":[""")

                    // Multiple not first: none
                    case (true, false) =>
                    // Normal
                    case _ =>

                        output.print(s""""${element.name}":""")
                }

                // Value
                //---------------
                value match {
                    case null => output.print(s"""""""")
                    case v =>

                        logFine[JsonIO]("Simple value: " + value)
                        output.print(s""""${value}"""")
                }

                // Close : Close last multiple or just a ,
                //----------------
                (isMultiple, isLast) match {

                    // Last
                    case (true, true) =>

                        logFine[JsonIO]("Close multiple: " + value)
                        output.print(s"""],""")

                    // Otherwise normal ,
                    case _ =>

                        output.print(s""",""")
                }

            // Attribute
            //---------------
            case (false, false, null, value) if (du.attribute != null) =>

                output.print(s""""${cleanAttributeName(du.attribute.name)}": \"${value}\",""")

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
