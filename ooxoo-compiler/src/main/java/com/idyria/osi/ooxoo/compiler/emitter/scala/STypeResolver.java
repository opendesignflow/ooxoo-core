/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.emitter.scala;

import java.util.HashMap;
import java.util.Map.Entry;

import com.idyria.osi.ooxoo.core.buffers.common.ObjectBuffer;
import com.idyria.osi.ooxoo.core.wrap.OOXAnyType;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.Value;

/**
 * @author rleys
 *
 */
public class STypeResolver extends ExtensionFunctionDefinition {

	// Maps xsd base type to a java class
	public static HashMap<String, Class> types = new HashMap<String, Class>();

	// FIXME correct the matching table
	static {

		types.put("string", com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer.class); // ok
		types.put("dateTime", com.idyria.osi.ooxoo.core.buffers.datatypes.DateTimeBuffer.class); // ok
		types.put("duration", com.idyria.osi.ooxoo.core.buffers.datatypes.DurationBuffer.class);
		types.put("anyURI", com.idyria.osi.ooxoo.core.buffers.datatypes.AnyURIBuffer.class); // ok
		types.put("anyType", OOXAnyType.class); // ok
		types.put("ID", com.idyria.osi.ooxoo.core.buffers.datatypes.IDBuffer.class); // ok
		types.put("base64Binary",
				com.idyria.osi.ooxoo.core.buffers.datatypes.Base64BinaryBuffer.class); // ok
		types.put("boolean", com.idyria.osi.ooxoo.core.buffers.datatypes.XSDBooleanBuffer.class); // ok
		types.put("decimal", com.idyria.osi.ooxoo.core.buffers.datatypes.DecimalBuffer.class); // ok
		types.put("float", com.idyria.osi.ooxoo.core.buffers.datatypes.FloatBuffer.class); // ok
		types.put("double", java.lang.Double.class); // ok
		types.put("time", com.idyria.osi.ooxoo.core.buffers.datatypes.TimeBuffer.class); // ok
		types.put("date", com.idyria.osi.ooxoo.core.buffers.datatypes.DateBuffer.class); // ok
		
		types.put("gYearMonth", com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer.class); // ok
		types.put("gYear", com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer.class); // ok
		types.put("gMonthDay", com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer.class); // ok
		types.put("gDay", com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer.class); // ok
		types.put("gMonth", com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer.class); // ok
		
		types.put("hexBinary", com.idyria.osi.ooxoo.core.buffers.datatypes.HexBinaryBuffer.class); // ok
		types.put("QName", com.idyria.osi.ooxoo.core.buffers.datatypes.QNameBuffer.class); // ok

		types.put("normalizedString", com.idyria.osi.ooxoo.core.buffers.datatypes.NormalizedStringBuffer.class); // ok
																// normalizedStringconverter
		types.put("token", com.idyria.osi.ooxoo.core.buffers.datatypes.TokenBuffer.class); // ok Tokenconverter
		types.put("language", com.idyria.osi.ooxoo.core.buffers.datatypes.LanguageBuffer.class); // ok
																				// language
																				// class
																				// and
																				// converter
		types.put("NMTOKEN", com.idyria.osi.ooxoo.core.buffers.datatypes.NMToken.class); // ok String converter
		types.put("NMTOKENS", com.idyria.osi.ooxoo.core.buffers.datatypes.NMTokens.class); // ok String converter
		types.put("Name", com.idyria.osi.ooxoo.core.buffers.datatypes.NameBuffer.class); // ok String converter //
													// unsuported
		types.put("NCName", com.idyria.osi.ooxoo.core.buffers.datatypes.NCNameBuffer.class); // ok String converter
		types.put("IDREF", com.idyria.osi.ooxoo.core.buffers.datatypes.IDBuffer.class); // ok IDRef converter
		types.put("IDREFS", java.lang.String.class); // ok IDRefs converter
		types.put("ENTITY", com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer.class); // ok String converter
														// // unsuported
		types.put("ENTITIES", com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer.class); // ok String converter
														// // unsuported
		types.put("integer", com.idyria.osi.ooxoo.core.buffers.datatypes.IntegerBuffer.class); // ok integerconverter
		types.put("nonPositiveInteger", com.idyria.osi.ooxoo.core.buffers.datatypes.NonPositiveIntegerBuffer.class); // ok
																	// NonPositiveintegerconverter
		types.put("negativeInteger", com.idyria.osi.ooxoo.core.buffers.datatypes.NonNegativeIntegerBuffer.class); // ok
		types.put("allNNI", com.idyria.osi.ooxoo.core.buffers.datatypes.MaxOccursBuffer.class); // ok
																// negativeIntegerconverter
		types.put("long", com.idyria.osi.ooxoo.core.buffers.datatypes.LongBuffer.class); // ok longconverter
		types.put("int", com.idyria.osi.ooxoo.core.buffers.datatypes.IntegerBuffer.class);// ok intconverter
		types.put("short", com.idyria.osi.ooxoo.core.buffers.datatypes.IntegerBuffer.class); // ok short converter
		types.put("byte", java.lang.Byte.class); // ok byteconverter
		types.put("nonNegativeInteger", com.idyria.osi.ooxoo.core.buffers.datatypes.NonNegativeIntegerBuffer.class); // ok
																	// NonNegativeintegerconverter
		types.put("unsignedLong", com.idyria.osi.ooxoo.core.buffers.datatypes.LongBuffer.class);
		types.put("unsignedInt", com.idyria.osi.ooxoo.core.buffers.datatypes.IntegerBuffer.class);
		types.put("unsignedShort", com.idyria.osi.ooxoo.core.buffers.datatypes.IntegerBuffer.class);
		types.put("unsignedByte", java.lang.Byte.class);
		types.put("positiveInteger", com.idyria.osi.ooxoo.core.buffers.datatypes.NonNegativeIntegerBuffer.class); // ok
																// IntegerConverter

		
		types.put("element", com.idyria.osi.ooxoo3.core.buffers.structural.ElementBuffer.class); // ok
		// IntegerConverter
		
	}
	
	
	/**
	 * 
	 */
	public STypeResolver() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Given the java class name, returns the XSD base type name, or null
	 * @param classname
	 * @return
	 */
	public static String getXSDTypeName(String classname) {
		
		// Get the class
		try {
			Class cl = Class.forName(classname);
			
			for (Entry<String,Class> ent:types.entrySet()) {
				
				if (ent.getValue() == cl) 
					return ent.getKey();
				
				
			}
			
			// If none found
			return null;
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			return null;
		}
		
		
		
	}
	
	@Override
	public ExtensionFunctionCall makeCallExpression() {
		
		return new ExtensionFunctionCall() {

			@Override
			public SequenceIterator call(SequenceIterator[] si,
					XPathContext context) throws XPathException {
				
				// Get type representation
				//-------------------
				String argument = si[0].next().getStringValue();
				String[] splitted = argument.split(":");
				String prefix = splitted.length>1 ? splitted[0] : "";
				String type = splitted.length>1 ? splitted[1] : splitted[0];
				
				//-- Resolve URI
				short nc = context.getNamePool().getCodeForPrefix(prefix);
				String nsuri = context.getNamePool().getURIFromURICode(nc);
				
				
				// Resolve
				//--------------------
				
				//-- If no nsuri -> XSD type
				if (nsuri.length()==0) {
					
					return Value.asIterator(StringValue.makeStringValue(STypeResolver.types.get(type).getCanonicalName()));
					
				} else {
					
					//-- Try to look into registered types map, otherwise return ObjectBuffer
					
					return Value.asIterator(StringValue.makeStringValue(ObjectBuffer.class.getCanonicalName()));
				}
				

//				System.out.println("Called extension for type "+argument);
//				System.out.println("\tNS for prefix "+prefix+" is: "+nsuri);
//				
//				return Value.asIterator(StringValue.makeStringValue("fromExtension"));
				
			}
			
		};
	}
	
	@Override
	public SequenceType getResultType(SequenceType[] arg0) {
		// TODO Auto-generated method stub
		return SequenceType.SINGLE_STRING;
	}
	
	@Override
	public StructuredQName getFunctionQName() {
		// TODO Auto-generated method stub
		return new StructuredQName("ooxoo", "com:idyria:osi:ooxoo", "resolveType");
	}
	
	@Override
	public SequenceType[] getArgumentTypes() {
		// TODO Auto-generated method stub
		return new SequenceType[]{SequenceType.SINGLE_STRING};
	}

}
