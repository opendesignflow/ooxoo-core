/**
 *
 */
package com.idyria.osi.ooxoo3.core.buffers.structural

import scala.beans.BeanProperty
import com.idyria.osi.ooxoo3.core.buffers.structural.io.IOBuffer

/**
 * The Base Buffer class is a default implementation providing Buffer infrastructure.
 * It is used by the main ooxoo buffer classe, and is intended to be usually derived by the user.
 *
 * The Buffer trait can be used for specific application, but usually, simply deriving this class will
 * be sufficient
 *
 * To be implented by subclasses:
 *
 * 	<ul>
 * 		<li>createDataUnit : DataUnit</li>
 *  </ul>
 *
 * @author rleys
 *
 */
abstract class BaseBuffer extends BaseBufferTrait {

  

}