/**
 * 
 */
package com.idyria.osi.ooxoo.core.wrap.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author rtek
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Ooxattribute {

	String use() default "optional";
	
	String defaultValue() default "";
}
