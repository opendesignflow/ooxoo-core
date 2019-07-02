/*-
 * #%L
 * Core runtime for OOXOO
 * %%
 * Copyright (C) 2006 - 2018 Open Design Flow
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
package com.idyria.osi.ooxoo.core.buffers.structural

import com.idyria.osi.tea.errors.TError

trait AttachedImplementation[MT <: Any] {

    var attachedInstance: Option[MT] = None

    def ensureInstance = attachedInstance match {
        case Some(inst) =>
            inst

        case None =>

            attachedInstance = Some(createInstance)
            attachedInstance.get
    }
    
    def safeEnsureInstance : ErrorOption[MT] = {
        try {
            ESome(ensureInstance)
        } catch {
            case e : Throwable => EError(e)
        }
    }

    def createInstance: MT

}