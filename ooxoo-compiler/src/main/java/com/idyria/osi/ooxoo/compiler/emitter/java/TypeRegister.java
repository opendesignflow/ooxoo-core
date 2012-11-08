/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.emitter.java;

import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.value.SequenceType;

/**
 * 
 * This extension function is designed to register new namespaces to package in the type resolver
 * 
 * @author rleys
 *
 */
public class TypeRegister extends ExtensionFunctionDefinition {

	/**
	 * The type resolver we want to register
	 */
	private TypeResolver typeResolver = null;
	
	/**
	 * 
	 */
	public TypeRegister(TypeResolver resolver) {
		this.typeResolver = resolver;
	}

	/* (non-Javadoc)
	 * @see net.sf.saxon.lib.ExtensionFunctionDefinition#getArgumentTypes()
	 */
	@Override
	public SequenceType[] getArgumentTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.saxon.lib.ExtensionFunctionDefinition#getFunctionQName()
	 */
	@Override
	public StructuredQName getFunctionQName() {
		// TODO Auto-generated method stub
		return new StructuredQName("ooxoo", "com:idyria:osi:ooxoo", "registerNamespaceMapping");
	}

	/* (non-Javadoc)
	 * @see net.sf.saxon.lib.ExtensionFunctionDefinition#getResultType(net.sf.saxon.value.SequenceType[])
	 */
	@Override
	public SequenceType getResultType(SequenceType[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.saxon.lib.ExtensionFunctionDefinition#makeCallExpression()
	 */
	@Override
	public ExtensionFunctionCall makeCallExpression() {
		// TODO Auto-generated method stub
		return null;
	}

}
