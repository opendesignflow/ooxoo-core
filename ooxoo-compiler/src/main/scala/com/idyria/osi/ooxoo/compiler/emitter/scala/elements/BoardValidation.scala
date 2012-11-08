
/**
 *
 */
package com.idyria.osi.ooxoo.compiler.emitter.scala.elements;



/**	
 *
				The Board Validation element stores the result of a
				board validation
			
 */
class BoardValidation extends com.idyria.osi.ooxoo3.core.buffers.structural.ElementBuffer {
    
    
    
class SubClassTest {
    
    
    

}       

/**	
 *
							The SerialID of the board that has been validated
						
 */ 
	var  SerialID : com.idyria.osi.ooxoo.core.buffers.common.ObjectBuffer = null

/**	
 *
							The ID provided by the tester, to match back the
							tested board
						
 */ 
	var  TesterSubjectID : com.idyria.osi.ooxoo.core.buffers.datatypes.IDBuffer = null

/**	
 *
							Gathers all the validation reports that have bee
							received for this board
						
 */ 
	var  BoardValidationReport : com.idyria.osi.ooxoo3.core.buffers.structural.XList[com.idyria.osi.ooxoo.core.buffers.common.ObjectBuffer] = null


}       
