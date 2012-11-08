/**
 * 
 */
package com.idyria.osi.ooxoo.compiler;

/**
 * @author rtek
 * 
 */
public class CompilerUtils {

	/**
	 * 
	 */
	public CompilerUtils() {
		// TODO Auto-generated constructor stub
	}

	public static <T> T createFromString(Class<T> targetType, String value) {
		if (value == null || value.length()==0)
			return null;
		try {
			return targetType.getConstructor(new Class<?>[] { String.class })
					.newInstance(value);
		} catch (Exception e) {
			//
		}
		return null;
	}

}
