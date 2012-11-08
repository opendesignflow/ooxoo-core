/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.swing;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import com.idyria.osi.ooxoo.core.Buffer;
import com.idyria.osi.ooxoo.core.tu.TransferUnit;


/**
 * @author rtek
 * 
 */
public class SwingStringModelBuffer extends Buffer<String> implements
		DocumentListener {

	private PlainDocument document = new PlainDocument();

	/**
	 * 
	 */
	public SwingStringModelBuffer() {
		// Connect this buffer as listener
		this.document.addDocumentListener(this);

	}

	@Override
	protected String convertToString() {
		// Create String
		try {
			return this.document.getText(0, this.document.getLength());
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uni.hd.cag.ooxoo.core.Buffer#convertFromString(java.lang.String)
	 */
	@Override
	protected String convertFromString(String value) {

		try {

			// Clear
			this.document.remove(0, this.document.getLength());

			// Set value
			this.document.insertString(0, value, null);

		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}

		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idyria.ooxoo.core.Buffer#createTransferUnit()
	 */
	@Override
	protected TransferUnit createTransferUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.idyria.ooxoo.core.Buffer#doWrapping(com.idyria.ooxoo.core.TransferUnit
	 * )
	 */
	@Override
	protected TransferUnit doWrapping(TransferUnit tu) {
		// TODO Auto-generated method stub
		String val = tu.getValue();
		try {
			document.remove(0, document.getLength());
			document.insertString(0, val, null);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tu;
	}

	/**
	 * @return the document
	 */
	public PlainDocument getDocument() {
		return document;
	}

	@Override
	protected TransferUnit doUnwrapping(TransferUnit tu) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		this.propagateToNext();
		this.propagateToPrevious();

	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		this.propagateToNext();
		this.propagateToPrevious();

	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		this.propagateToNext();
		this.propagateToPrevious();

	}

}
