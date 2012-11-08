/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.extra;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.idyria.osi.ooxoo.core.Buffer;
import com.idyria.osi.ooxoo.core.tu.TransferUnit;
import com.idyria.utils.java.TeaStringUtils;
import com.idyria.utils.java.env.TeaEnvUtils;


/**
 * This Buffer extract environement variable defined in the provided string and
 * replace them with the env value, or don't touch if value is not found
 * 
 * Format:
 * 
 * ${ENVVAR}
 * 
 * @author rleys
 * 
 */
public class EnvironementVariableStringBuffer extends Buffer<String> {

	/**
	 * Maps string values to their env variable name
	 */
	private HashMap<String,String> stringToEnvironement = new HashMap<String,String>();
	
	
	/**
	 * 
	 */
	public EnvironementVariableStringBuffer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param value
	 */
	public EnvironementVariableStringBuffer(String value) {
		super(value);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uni.hd.cag.ooxoo.core.Buffer#doWrapping(uni.hd.cag.ooxoo.core.tu.TransferUnit
	 * )
	 */
	@Override
	protected TransferUnit doWrapping(TransferUnit tu) {
		
		//-- Foreach String -> ENV map, and replace each found String with the env variable
		/*System.out.println("Got to wrap: "+tu.getValue());
		String result = tu.getValue();
		for (String key: this.stringToEnvironement.keySet()) {
			//System.out.println("Replacing: "+key);
			result = result.replace(key, "${"+this.stringToEnvironement.get(key)+"}");
			
		}*/
		
		
		
		tu.setValue(this.convertToString());
		return tu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uni.hd.cag.ooxoo.core.Buffer#doUnwrapping(uni.hd.cag.ooxoo.core.tu.
	 * TransferUnit)
	 */
	@Override
	protected TransferUnit doUnwrapping(TransferUnit tu) {
		
		//-- Find ${...} strings and replace with environement
		tu.setValue(this.resolve(tu.getValue()));
		
		return tu;
	}

	/**
	 * Does the env variable replacement operation
	 * @param source
	 * @return
	 */
	private String resolve(String source) {
		
		String result = source;
		// Regexp
		String regexp="(?:.*(?:\\$\\{(.*)\\}))+.*";
		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(source);
		if (m.matches()) {
		//System.err.println("MATCH!: "+m.groupCount());
			// Foreach groups to locate each variable to replace
			for (int i=1;i<=m.groupCount();i++) {
				
				// Get Variable
				String envvar = m.group(i);
				//System.err.println("Replacing!: "+envvar);
				// Get Value
				String val = System.getenv(envvar);
				
				// Add to map and replace if there is one
				if (val!=null) {
					this.stringToEnvironement.put(val, envvar);
					result = result.replace("${"+envvar+"}",val);
				}
				
			}
			
		} else {
			//System.err.println("Doesn't match ("+source+")");
		}
		
		return result;
		
	}
	
	/* (non-Javadoc)
	 * @see uni.hd.cag.ooxoo.core.Buffer#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(String value) {
		// Set to super and resolve
		super.setValue(this.convertFromString(value));

	}

	@Override
	protected String convertFromString(String value) {
		// TODO Auto-generated method stub
		return this.resolve(value);
	}

	/**
	 * Converts to string, by adding Env variables
	 * 
	 */
	@Override
	protected String convertToString() {
		// TODO Auto-generated method stub
		return TeaEnvUtils.replaceWithEnv(super.convertToString());
	}

	
	
	
	
}
