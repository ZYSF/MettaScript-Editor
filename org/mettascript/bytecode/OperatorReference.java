/* No copyright, no warranty, only code. 
 * This file was created on 15 Nov 2014. It was a good day.
 */
package org.mettascript.bytecode;

import java.io.PrintStream;

import org.mettascript.parser.ValueSyntax;

/**
 *
 * @author Zak Fenton
 */
public class OperatorReference extends ConstantLike {

	private String name;
	
	OperatorReference(BytecodeFile bytecodeFile, String name) {
		super(bytecodeFile);
		this.name = name;
	}

	@Override
	public void print(PrintStream output, boolean jsonFormat) {
		if (jsonFormat) {
			output.print("    " + ValueSyntax.getSourceForString(name));
		} else {
			output.println("OPERATOR " + getIndex() + ": " + name);
		}
	}
	
	@Override
	public String toString() {
		return "OPERATOR " + getIndex() + ": " + name;
	}
	
	public String getNameString() {
		return name;
	}
}
