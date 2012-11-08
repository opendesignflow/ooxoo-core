/**
 * 
 */
package com.idyria.osi.ooxoo.core.tryout;

import javax.swing.JFrame;


import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JButton;

import com.idyria.osi.ooxoo.core.AttributeBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.IntegerBuffer;
import com.idyria.osi.ooxoo.core.buffers.swing.SwingStringModelBuffer;
import com.idyria.osi.ooxoo.core.tu.TransferUnit;


import java.awt.Insets;

/**
 * @author rtek
 *
 */
public class OOXOO2Tryout extends JFrame {

	private JPanel jPanel = null;
	private JTextField jTextField = null;
	private JButton jButton = null;

	private IntegerBuffer integer = new IntegerBuffer();  //  @jve:decl-index=0:
	
	/**
	 * 
	 */
	public OOXOO2Tryout() {
		super();
		this.setTitle("OOXOO2 Tryout");
		initialize();
		
	}
	
	private void initialize() {
        this.setSize(new Dimension(400, 400));
        this.setContentPane(getJPanel());
		
		
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.insets = new Insets(5, 0, 0, 0);
			gridBagConstraints1.gridy = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.insets = new Insets(0, 5, 0, 5);
			gridBagConstraints.gridx = 0;
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getJTextField(), gridBagConstraints);
			jPanel.add(getJButton(), gridBagConstraints1);
		}
		return jPanel;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			
		}
		return jTextField;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Set and wrap");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					integer.setValue(2);
					TransferUnit resultUnit = integer.wrap();
					System.out.println("String result: "+resultUnit.getValue());
				}
			});
		}
		return jButton;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		OOXOO2Tryout tr = new OOXOO2Tryout();
		
		// A Buffer for an Integer
		tr.integer = new IntegerBuffer();
		
		
		// SwingModel transparent buffer
		SwingStringModelBuffer sbuffer = new SwingStringModelBuffer();
		tr.jTextField.setDocument(sbuffer.getDocument());
		tr.integer.setNextBuffer(sbuffer);
		
		// Link to an Attribute Wrapper
		AttributeBuffer attribute = new AttributeBuffer();
		sbuffer.setNextBuffer(attribute);
		
		
		
		
		tr.setVisible(true);
		
		
		
		

	}

}
