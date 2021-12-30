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
package org.odfi.ooxoo.model.writers

import org.odfi.ooxoo.model.Writer
import java.io.PrintStream

class PrintStreamWriter(var out: PrintStream) extends Writer {

  var filesWritten = List[String]()

  def cleanOutput(path: String) = {

  }

  /**
   * Also saves the path of written file for the fileWritten
   */
  def file(path: String) = {

    out.println(s"---------- File: $path -----------------")

    filesWritten = path :: filesWritten
  }

  def fileWritten(path: String): Boolean = {

    this.filesWritten.contains(path)

  }

  def <<(str: String): Writer = {
    out.println(s"${this.indentString}$str")
    this
  }

  def finish = {

  }

  def getWriterForFile(f: String) = {
    this
  }

}

/**
 * Simply outputs result to stdout, for example for debugging purpose
 */
class StdoutWriter extends PrintStreamWriter(System.out) {

}
