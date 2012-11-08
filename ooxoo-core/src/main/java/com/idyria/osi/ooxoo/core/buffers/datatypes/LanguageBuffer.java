package com.idyria.osi.ooxoo.core.buffers.datatypes;

import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Vector;


/**
 * 
 * 
 * @author Rtek
 * 
 */
public class LanguageBuffer extends TokenBuffer {


	/**
	 * The language Name associated with this code value
	 */
	private String cachedFullLanguageName = null;

	
	
	/**
	 * 
	 */
	public LanguageBuffer() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param str
	 * @throws SyntaxException
	 */
	public LanguageBuffer(String languageCode) throws SyntaxException {
		super(languageCode);
		this.validate();
	}
	
	public static LanguageBuffer fromString(String value) throws SyntaxException {

		LanguageBuffer res = null;

		// Check null value
		if (value == null || value.length()==0)
			return null;

		res = new LanguageBuffer(value);
		return res;

	}


	public String getLanguageName() throws SyntaxException {

		String res = "";
		try {

			if (this.cachedFullLanguageName != null)
				res = this.cachedFullLanguageName;
			else {
				ResourceBundle rb = ResourceBundle
						.getBundle("com.znw.xml.zaxb.datatypes.impl"
								.replaceAll("\\.", "/")
								+ "/languages");
				res = rb.getString(this.value);

				this.cachedFullLanguageName = res;
			}

		} catch (Exception ex) {

			throw new SyntaxException("Provided Language Code is not valid !!");
		}

		return res;
	}

	

	/**
	 * Returns available languages : [][0] = language name, [][1] = code
	 */
	public static String[][] getLanguages() {

		// Vector to store results
		Vector<String[]> vect = new Vector<String[]>();

		ResourceBundle rb = ResourceBundle
				.getBundle("com.znw.xml.zaxb.datatypes.impl".replaceAll("\\.",
						"/")
						+ "/languages");

		Enumeration<String> enu = rb.getKeys();

		while (enu.hasMoreElements()) {
			String key = enu.nextElement();

			String[] res = new String[2];
			res[0] = rb.getString(key);
			res[1] = key;

			vect.add(res);

		}

		return vect.toArray(new String[vect.size()][]);

	}

	
	/**
	 * @see com.idyria.tools.xml.oox.datatypes.impl.StringConstrainedType#validate()
	 */
	@Override
	public boolean validate() throws SyntaxException {
		boolean bol = super.validate();
		
		if (bol)
			bol = this.value.matches("[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*");
		
		return bol;
		
	}

	

}
