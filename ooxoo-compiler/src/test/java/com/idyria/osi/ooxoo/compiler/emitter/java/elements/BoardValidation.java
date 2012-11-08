
/**
 *
 */
package com.idyria.osi.ooxoo.compiler.emitter.java.elements;


		
			   
		
		
			
				  
				  
				  
				  
			
		
	  
		
			   
/**	
 *
				The Board Validation element stores the result of a
				board validation
			
 */
		
		
			
				  
				  
				  
				  
			
		
	  
public class BoardValidation {
	
	
	
		
			   
		
		
			
				  
				  
         
         
         
					
						   
/**	
 *
							The SerialID of the board that has been validated
						
 */
					
				  
		
		protected com.idyria.osi.ooxoo.core.buffers.common.ObjectBuffer SerialID = null;
		
	
				  
         
         
         
					
						   
/**	
 *
							The ID provided by the tester, to match back the
							tested board
						
 */
					
				  
		
		protected com.idyria.osi.ooxoo.core.buffers.datatypes.IDBuffer TesterSubjectID = null;
		
	
				  
         
         
         
					
						   
/**	
 *
							Gathers all the validation reports that have bee
							received for this board
						
 */
					
				  
		
		protected OOXList<com.idyria.osi.ooxoo.core.buffers.common.ObjectBuffer> BoardValidationReport = null;
		
	
			
		
	  

	public BoardValidation() {
	
	}

}		
