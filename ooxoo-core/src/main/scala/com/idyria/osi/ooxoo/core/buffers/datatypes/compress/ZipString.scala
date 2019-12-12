/*-
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
package com.idyria.osi.ooxoo.core.buffers.datatypes.compress

import com.idyria.osi.ooxoo.core.buffers.structural.DataUnit
import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream
import java.io.ByteArrayInputStream
import org.odfi.tea.io.TeaIOUtils
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream
import java.io.ByteArrayOutputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import java.util.Base64

class ZipString extends AbstractDataBuffer[String] {

  var base64Buffer = ""

  override def streamIn(du: DataUnit) = {
    super.streamIn(du)
    if (du.isHierarchyClose) {

     decompressData
    }
  }

  def decompressData = {
    val decompressor = new BZip2CompressorInputStream(new ByteArrayInputStream(Base64.getDecoder.decode(base64Buffer)))
    this.data = new String(TeaIOUtils.swallowStream(decompressor))
    base64Buffer = ""
  }

  def dataFromString(str: String): String = {
    base64Buffer += str.trim

    this.data
  }

  def dataToString: String = data match {
    case null => null
    case d =>

      val outputBytes = new ByteArrayOutputStream
      val compression = new BZip2CompressorOutputStream(outputBytes,BZip2CompressorOutputStream.chooseBlockSize(d.size))
      compression.write(d.getBytes)
      compression.flush
      compression.finish()
      compression.close()
      Base64.getEncoder.encodeToString(outputBytes.toByteArray())
  }

  override def toString: String = this.dataToString

}
