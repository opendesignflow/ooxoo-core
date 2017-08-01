package com.idyria.osi.ooxoo.core.buffers.id

import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import org.apache.commons.lang3.RandomUtils
import com.idyria.osi.tea.random.UniqueLongGenerator
import com.idyria.osi.ooxoo.core.buffers.structural.xattribute
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer

trait ElementWithID extends ElementBuffer {
  
  @xattribute(name="eid")
  var eid : XSDStringBuffer = UniqueLongGenerator.getStaticInstance.generate().toString
  
}