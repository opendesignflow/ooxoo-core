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
/**
 *
 */
package org.odfi.ooxoo.core.buffers.datatypes

import scala.language.implicitConversions

class QName(localPart:String) extends XSDStringBuffer {

    // QName wrapper
    //-------------
    var qname =  javax.xml.namespace.QName.valueOf(localPart) 

    data = localPart

    def getLocalPart() = qname.getLocalPart

    override def toString = qname.toString

    


}

/**
 * @author rleys
 *
 */
object QName {

  def apply() = new QName("")
  
  implicit def convertStringToQName(str: String) : QName = new QName(str)

}
