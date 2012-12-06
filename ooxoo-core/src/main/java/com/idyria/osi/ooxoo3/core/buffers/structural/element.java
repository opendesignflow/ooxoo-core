/**
 * 
 */
package com.idyria.osi.ooxoo3.core.buffers.structural;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author rleys
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.TYPE})
public @interface element {

	String name();
	String ns() default "";
}
