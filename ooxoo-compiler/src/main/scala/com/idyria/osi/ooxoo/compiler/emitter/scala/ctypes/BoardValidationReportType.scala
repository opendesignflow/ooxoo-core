
/**
 *
 */
package com.idyria.osi.ooxoo.compiler.emitter.scala.ctypes;



/**	
 *
				Gathers a validation report associated with validation
				results
			
 */
class BoardValidationReportType {
    
    
    
/**	
 *
						The Time at which the Validation Report has been
						fetched
					
 */ 
	var  Timestamp : com.idyria.osi.ooxoo.core.buffers.datatypes.DateTimeBuffer = null
 
	var  Error : com.idyria.osi.ooxoo3.core.buffers.structural.XList[com.idyria.osi.ooxoo.core.buffers.common.ObjectBuffer] = null
 
	var  EEPROMConfig : com.idyria.osi.ooxoo3.core.buffers.structural.XList[com.idyria.osi.ooxoo.core.buffers.common.ObjectBuffer] = null
 
	var  ValidationProcessReport : com.idyria.osi.ooxoo.core.buffers.common.ObjectBuffer = null


}       
