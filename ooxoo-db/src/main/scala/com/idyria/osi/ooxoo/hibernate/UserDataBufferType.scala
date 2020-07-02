/*-
 * #%L
 * OOXOO Db Project
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
package com.idyria.osi.ooxoo.hibernate

import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer
import org.hibernate.usertype.UserType
import scala.reflect.ClassTag
import com.idyria.osi.ooxoo.core.buffers.datatypes.IntegerBuffer
import java.sql.Types
import java.sql.ResultSet
import java.sql.PreparedStatement
import org.hibernate.`type`.IntegerType
import org.hibernate.engine.spi.SharedSessionContractImplementor
import java.io.Serializable

abstract class UserDataBufferType[BT <: AbstractDataBuffer[_]](implicit tag: ClassTag[BT]) extends UserType with Serializable {

  def returnedClass = tag.runtimeClass

  // Get
  def nullSafeGet(rs: ResultSet, names: Array[String], session: SharedSessionContractImplementor,owner: Any) = {
    rs.wasNull() match {
      case true => null
      case false =>
        val buffer = tag.runtimeClass.getDeclaredConstructor().newInstance().asInstanceOf[AbstractDataBuffer[Any]]
        buffer.data = getDataFromResult(rs, names)
        buffer
    }
  }

  def getDataFromResult(rs: ResultSet, names: Array[String]): Any
  def setDataToResult(ps: PreparedStatement, value: AbstractDataBuffer[Any], index: Int, session: SharedSessionContractImplementor) : Unit

  // Set
  def nullSafeSet(ps: PreparedStatement, value: Any, index: Int, session: SharedSessionContractImplementor) = {

    value match {
      case null =>
        ps.setNull(index, sqlTypes()(0))
      case other =>

        val buffer = value.asInstanceOf[AbstractDataBuffer[Any]]
        setDataToResult(ps, buffer, index, session)
      //buffer.data = ps.setL
    }

  }

  // Utils
  //-----------
  def equals(a: Any, b: Any) = {
    (a == b) ||
      ((a != null) && (b != null) && (a.equals(b)))
  }

  def hashCode(o: Any) = o.hashCode()
  
  def deepCopy(o:Any) = o.asInstanceOf[Object]
  
  def isMutable = true
  
  def disassemble(o:Any) = o.asInstanceOf[Serializable]
  
  def assemble(cached:Serializable,owner:Any) = cached.asInstanceOf[Object]
  
  def replace(o:Any,target:Any,owner:Any) = o.asInstanceOf[Object]

}

class IntegerBufferUserType extends UserDataBufferType[IntegerBuffer] {

  def sqlTypes = Array(Types.INTEGER)

  def getDataFromResult(rs: ResultSet, names: Array[String]) = rs.getInt(names(1))

  def setDataToResult(ps: PreparedStatement, value: AbstractDataBuffer[Any], index: Int, session: SharedSessionContractImplementor) = {
    IntegerType.INSTANCE.set(ps, value.data.asInstanceOf[Int], index, session)
  }

}




