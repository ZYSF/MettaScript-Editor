/* No copyright, no warranty, only code. 
 * This file was created on 15 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 *
 * @author Zak Fenton
 */
public abstract class AbstractBytecodeObject {

	public AbstractBytecodeObject() {
		// TODO Auto-generated constructor stub
	}

	public abstract void print(PrintStream output, boolean jsonFormat);
	
	public final String printToString() {
		return printToString(false);
	}
	
	public final String printToString(boolean jsonFormat) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		print(ps, jsonFormat);
		ps.close();
		return baos.toString();
	}
}
