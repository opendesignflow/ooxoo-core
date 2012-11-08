/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.out;

import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotableType;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotation.Documentation;

/**
 * @author rtek
 *
 */
public class OutputUtils {

	/**
	 * 
	 */
	public OutputUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static String extractDocumentation(ZaxCAnnotableType ant) {
		String res = null;
		if (ant.getAnnotation()!=null && ant.getAnnotation().getDocs()!=null) {
			
			for (Documentation doc : ant.getAnnotation().getDocs()) {
				res = doc.getContent()+"\n";
			}
			
		}
		return res;
	}

}
