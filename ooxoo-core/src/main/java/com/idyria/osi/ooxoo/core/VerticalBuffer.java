/**
 * 
 */
package com.idyria.osi.ooxoo.core;

import java.util.Vector;


import com.idyria.osi.ooxoo.core.tu.TransferUnit;
import com.idyria.utils.java.logging.TeaLogging;

/**
 * @author rtek
 * 
 */
public abstract class VerticalBuffer<VT> extends Buffer<VT> {

	/**
	 * The actions we want to run after horizontal buffering
	 */
	protected Vector<Runnable> verticalActions = new Vector<Runnable>();

	/**
	 * 
	 */
	public VerticalBuffer() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param value
	 */
	public VerticalBuffer(VT value) {
		super(value);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.idyria.ooxoo.core.Buffer#wrap(com.idyria.ooxoo.core.tu.TransferUnit)
	 */
	@Override
	public TransferUnit wrap(TransferUnit tu, WrappingContext context) {
		TransferUnit rtu = null;
		this.ooxooWrappingContext = context;
		try {

			// Let top horizontal Wrapping take place
			rtu = super.wrap(tu, context);

			TeaLogging.teaLogInfo("Doing " + this.verticalActions.size()
					+ " vertical wrapping on " + getClass());
			if (this.verticalActions.size() > 2) {
				System.exit(0);
			}
			// Proceed with vertical actions
			for (Runnable run : this.verticalActions) {

				run.run();

			}
		} finally {
			// Remove now
			this.verticalActions.clear();
		}

		// TeaLogging.teaLogInfo("Doine vertical informations ("+this.verticalActions.size()+")");

		// Return
		return rtu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.idyria.ooxoo.core.Buffer#unwrap(com.idyria.ooxoo.core.tu.TransferUnit
	 * )
	 */
	@Override
	public TransferUnit unwrap(TransferUnit tu, WrappingContext context) throws UnwrapException {

		TransferUnit rtu = null;
		this.ooxooWrappingContext = context;
		try {
			// Let top horizontal Unwrapping take place
			try {
				rtu = super.unwrap(tu, context);
			}  finally {

				//TeaLogging.teaLogInfo("Doing vertical unwrapping");
				// Proceed with vertical actions
				for (Runnable run : this.verticalActions) {
	
					run.run();
	
				}
			}
//			catch (UnwrapException e) {
//				// Maybe the chain was broken, that doesn't mean we don't have
//				// to go further
//			}
		} finally {
			// Remove now
			this.verticalActions.clear();
		}
		// Return
		return rtu;
	}

}
