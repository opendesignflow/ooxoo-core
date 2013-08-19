package com.idyira.osi.ooxoo.maven;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface producer {

    /**
        The class name of a model Producer
        To be added to a model like this
    
        @producer("path.to.Producer")
        object model extends ModelBuilder {
            ...
        }

    */
    String value() ; 
}
