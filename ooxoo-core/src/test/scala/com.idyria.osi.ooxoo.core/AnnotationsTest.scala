
package com.idyria.osi.ooxoo.core

import java.io.StringReader
import java.io.StringWriter
import org.scalatest._
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.VerticalBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.XList
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer
import com.idyria.osi.ooxoo.core.buffers.structural._
import org.scalatest.matchers.ShouldMatchers


import com.idyria.osi.ooxoo.core.utils._



/**
 * @author rleys
 *
 */
class AnnotationsTest extends FunSuite with ShouldMatchers with GivenWhenThen {

    @xelement
    class TopClass {

    }


    test("xelement from top class") {

        given("xelement on class")
        //-------------------------
        var xelt = xelement_base(new TopClass)


        given("instance is not null")
        //-------------------------
        assert(xelt!=null)

        given("name if the name of the class")
        //-------------------------
        expect("TopClass")(xelt.name)



    }


    test("xelement from sub class") {

        @xelement()
        class EmbeddedClass {

        }

        given("xelement on class")
        //-------------------------
        var xelt = xelement_base(new EmbeddedClass)


        given("instance is not null")
        //-------------------------
        assert(xelt!=null)

        given("name is the name of the class (original class name: "+(new EmbeddedClass).getClass.getSimpleName+")")
        //-------------------------
        expect("EmbeddedClass")(xelt.name)


    }

    test("xelement sub class field") {

        @xelement()
        class EmbeddedClass {

            @xelement
            var elt : XSDStringBuffer = null

        }

        given("An EmbeddedClass instance")
        //----------------------------------------------------------
        var inst = new EmbeddedClass

        then("Field must be detected")
        //----------------------------------------------------------
        var fields = ScalaReflectUtils.getFields(inst)

        assert(fields.size>0)

        then("There is one xelement field named elt")
        //----------------------------------------------------------
        var xeltFields = ScalaReflectUtils.getAnnotatedFields(inst, classOf[xelement])

        expect(1)(xeltFields.size)
        expect("elt")(xeltFields.head.getName)

        then("Name of xelement is the name of the field")
        //----------------------------------------------------------
        var xelt = xelement_base(xeltFields.head)

        assert(xelt!=null)
        expect("elt")(xelt.name)

    }

    test("xattribute sub class field") {

        @xelement()
        class EmbeddedClass {

            @xattribute()
            var attr : XSDStringBuffer = null

        }

        // Field must be detected
        //----------------------------
        given("An EmbeddedClass instance")
        var inst = new EmbeddedClass

        then("Field must be detected")
        //-------------------------------------
        var fields = ScalaReflectUtils.getAnnotatedFields(inst, classOf[xattribute])

        assert(fields.size==1)

        then("Name of xattribute is the name of the field")
        //-------------------------------------------------
        var xattr = xattribute_base(fields.head)
        expect("attr")(xattr.name)

    }

   /* test("Instanciate subclass type") {

        class SubClassType extends XSDStringBuffer {

        }


        given("A SubClass instance")
        //---------------------------------------------
        var inst = new SubClassType

        then("We can instanciate it per reflection")
        //var res = inst.getClass.newInstance


        var eConst = inst.getClass.getEnclosingConstructor
        println("Test: "+eConst)

         inst.getClass.getDeclaredConstructors.foreach {

            c =>
            println("Constructor: "+c)
            println("Currently in: "+this)
        }
        //var cl = Class.forName(inst.getClass.getCanonicalName)
        //cl.newInstance
    }*/


    @xelement()
    class EmbeddedClass {

        class SubClassType extends XSDStringBuffer {

        }

        @xattribute()
        var attr : SubClassType = null

    }


    test("Field with top class subclass type") {




        given("An EmbeddedClass instance")
        //---------------------------------------------
        var inst = new EmbeddedClass

        given("An attribute field, with subclass type")
        //---------------------------------------------
        var field = ScalaReflectUtils.getAnnotatedFields(inst, classOf[xattribute]).head
        assert(field!=null)

        then("The Field can be instanciated")
        //---------------------------------------------
        var instance : Buffer = ScalaReflectUtils.instanciateFieldValue(inst,field)
        assert(instance!=null)
    }



}
