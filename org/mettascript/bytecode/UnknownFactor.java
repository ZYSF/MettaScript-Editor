/* No copyright, no warranty, only code. 
 * This file was created on 16 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode;

import java.io.PrintStream;

/**
 *
 * @author Zak Fenton
 */
public class UnknownFactor extends ConstantLike {
	private TextConstant name;

	public UnknownFactor(BytecodeFile constantPool, TextConstant name, int referenceIndex) {
		super(constantPool);
		this.name = name;
		this.index = -referenceIndex - 1;
	}

	@Override
	public void print(PrintStream output, boolean jsonFormat) {
		if (jsonFormat) {
			output.print(name.index);
		} else {
			output.println("UNKNOWN FACTOR " + index + ":" + name + " (referenced as " + (-1 - index) + ")");
		}
	}

	public int getTextIndex() {
		return name.index;
	}
}
