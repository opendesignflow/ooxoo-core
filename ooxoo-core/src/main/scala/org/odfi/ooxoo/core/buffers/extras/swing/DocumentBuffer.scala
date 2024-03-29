package org.odfi.ooxoo.core.buffers.extras.swing

import org.odfi.ooxoo.core.buffers.structural.{BaseBufferTrait, DataUnit}

import javax.swing.text.PlainDocument

/**
  This is a special Buffer to connect a Swing Document to the buffer chain, and an XSDStringBuffer

*/
class DocumentBuffer extends BaseBufferTrait {

  // Plain Document used to connect to Swin UI
  var document = new PlainDocument



  override def streamOut(du: DataUnit) = {

    du.value = document.getText(0,document.getLength)
    super.streamOut(du);

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

  }
}


