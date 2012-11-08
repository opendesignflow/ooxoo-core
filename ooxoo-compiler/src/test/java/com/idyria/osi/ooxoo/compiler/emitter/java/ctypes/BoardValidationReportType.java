
/**
 *
 */
package com.idyria.osi.ooxoo.compiler.emitter.java.ctypes;


		
			   
		
		
			   
			   
			   
			   
		
	  
		
			   
/**	
 *
				Gathers a validation report associated with validation
				results
			
 */
		
		
			   
			   
			   
			   
		
	  
public class BoardValidationReportType {
	
	
	
		
			   
		
		
			   
         
         
         
				
					    
/**	
 *
						The Time at which the Validation Report has been
						fetched
					
 */
				
			   
		
		protected com.idyria.osi.ooxoo.core.buffers.datatypes.DateTimeBuffer Timestamp = null;
		
	
			   
         
         
         
      
		
		protected OOXList<com.idyria.osi.ooxoo.core.buffers.common.ObjectBuffer> Error = null;
		
	
			   
         
         
         
      
		
		protected OOXList<com.idyria.osi.ooxoo.core.buffers.common.ObjectBuffer> EEPROMConfig = null;
		
	
			   
         
         
         
      
		
		protected com.idyria.osi.ooxoo.core.buffers.common.ObjectBuffer ValidationProcessReport = null;
		
	
		
	  

	public BoardValidationReportType() {
	
	}

}		
