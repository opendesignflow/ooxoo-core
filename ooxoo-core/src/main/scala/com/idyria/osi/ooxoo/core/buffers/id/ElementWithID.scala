/*-
 * #%L
 * Core runtime for OOXOO
 * %%
 * Copyright (C) 2006 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package com.idyria.osi.ooxoo.core.buffers.id

import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import org.apache.commons.lang3.RandomUtils
import com.idyria.osi.tea.random.UniqueLongGenerator
import com.idyria.osi.ooxoo.core.buffers.structural.xattribute
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer

trait ElementWithID extends ElementBuffer {
  
  @xattribute(name="eid")
  var eid : XSDStringBuffer = ""
  
  /**
   * Turn the value into a standard ID string.
   * For example " " is turned to "-"
   */
  def stringToStdEId(value:Any) = {
    value.toString().replaceAll("""[^\w-_]""", "").replaceAll("""\s+""", "-").toLowerCase()
  }
  
}
