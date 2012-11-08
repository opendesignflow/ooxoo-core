/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.out.java;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author Rtek
 *
 */
public final class SectionOutputStream extends PrintStream {

	private ByteArrayOutputStream bytes =null;
	
	public SectionOutputStream(){
		super(new ByteArrayOutputStream());

		bytes = (ByteArrayOutputStream) super.out;
		
		
	}

	/**
	 * @return the bytes
	 */
	public final ByteArrayOutputStream getBytes() {
		return bytes;
	}

	
	public final synchronized byte[] toByteArray() {
		
		return bytes.toByteArray();
		
	}
	
	
	public final synchronized byte[] returnAndClose() {
		
		byte[] ret = bytes.toByteArray();
		
		try {
			bytes.close();
			this.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
		
	}

	
	/* *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
	
		return new String(toByteArray());
	}
	
	
}
