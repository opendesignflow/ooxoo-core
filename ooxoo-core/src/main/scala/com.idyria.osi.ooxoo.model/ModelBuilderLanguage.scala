package com.idyria.osi.ooxoo.model


import com.idyria.osi.ooxoo.core.buffers.datatypes._
import scala.language.implicitConversions

import com.idyria.osi.tea.listeners.ListeningSupport


/**
    This trait contains all the language Wrappers and Conversions used in Model Builder.
    It is separated to lighten real model management in ModelBuilder from convienience language
*/
trait ModelBuilderLanguage extends ListeningSupport {



     class IsWordElementWrapper( var left: Element) {

        def is(right: => Unit) = {

            println("in is definition for Element")

            @->("element.start",left)

            right

            @->("element.end",left)

        }
    }
    implicit def elementToIsWordWrapping(str: String) :  IsWordElementWrapper = new IsWordElementWrapper(str)

}
