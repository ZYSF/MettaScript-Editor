/* No copyright, no warranty, only code. 
 * This file was created on 15 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode;

import java.io.PrintStream;

import org.mettascript.runtime.*;

/**
 *
 * @author Zak Fenton
 */
public abstract class Constant extends ConstantLike {
	
	public enum Type {
		INTEGER,
		TEXT
	}

	Constant(BytecodeFile constantPool) {
		super(constantPool);
	}
	
	public abstract String getValueString();
	
	public abstract Type getType();
	
	public abstract Value getValue();

	@Override
	public void print(PrintStream output, boolean jsonFormat) {
		if (jsonFormat) {
			output.print("    " + getValueString());
		} else {
			output.println("CONSTANT " + getType() + " " + getIndex() + ":" + getValueString());
		}
	}

	@Override
	public String toString() {
		return getIndex() + " [" + getValueString() + "]";
	}
}
